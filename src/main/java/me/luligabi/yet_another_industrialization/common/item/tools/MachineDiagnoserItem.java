package me.luligabi.yet_another_industrialization.common.item.tools;

import aztech.modern_industrialization.api.machine.component.InventoryAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.SteamCraftingMachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.SteamWaterPumpBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.SteamCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.util.TextHelper;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.util.YAIText;
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor;
import me.luligabi.yet_another_industrialization.mixin.MultiblockMachineBlockEntityAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.multiplied.AbstractSteamMultipliedCraftingMultiblockBlockEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class MachineDiagnoserItem extends Item {
    public MachineDiagnoserItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) return InteractionResult.FAIL;

        if (ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof MachineBlockEntity machine) {
            sendDiagnosis(ctx.getPlayer(), diagnose(machine), machine);
            // port-26.1.2: кулдаун из конфига (апстрим: YAI.CONFIG.machineDiagnoser().cooldownTicks());
            // addCooldown в26 принимает ItemStack, а не Item
            int cooldown = YAI.CONFIG.machineDiagnoser().cooldownTicks();
            if (cooldown > 0) ctx.getPlayer().getCooldowns().addCooldown(ctx.getItemInHand(), cooldown);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private Set<DiagnosisType> diagnose(MachineBlockEntity machine) {
        Set<DiagnosisType> diagnosis = EnumSet.noneOf(DiagnosisType.class);

        if (machine instanceof MultiblockMachineBlockEntity multiblock) {
            if (!multiblock.isShapeValid()) {
                diagnosis.add(DiagnosisType.INVALID_MULTIBLOCK_SHAPE);
                return diagnosis;
            }
            diagnoseHatches(multiblock, diagnosis);
        } else {
            if (machine instanceof SteamCraftingMachineBlockEntity || machine instanceof SteamWaterPumpBlockEntity) {
                // port-26.1.2: diagnoseSteam не портирован (ConfigurableFluidStack API 26) — стаб как в предыдущей ревизии
            } else {
                // port-26.1.2: machine.getComponents() → поле components (ComponentStorage$Server), get() → getNullable()
                EnergyComponent energy = machine.components.getNullable(EnergyComponent.class);
                if (energy != null && energy.getEu() == 0L) {
                    diagnosis.add(DiagnosisType.NO_ENERGY);
                    return diagnosis;
                }
            }
        }

        // port-26.1.2: CrafterComponent — реальный компонент; getActiveRecipe — через наш миксин-аксессор
        CrafterComponent crafter = machine.components.getNullable(CrafterComponent.class);
        if (crafter != null) {
            RecipeHolder<MachineRecipe> activeRecipe = ((CrafterComponentAccessor) crafter).getActiveRecipe();
            if (activeRecipe != null) {
                diagnoseRecipe(activeRecipe.value(), crafter, diagnosis);
            } else {
                diagnosePreRecipe(crafter, diagnosis);
            }
        }

        return diagnosis;
    }

    private void diagnoseHatches(MultiblockMachineBlockEntity machine, Set<DiagnosisType> diagnosis) {
        // ShapeTemplate.hatchFlags: Map<BlockPos, HatchFlags> (javap) — допускаемые флаги по позициям
        Collection<HatchFlags> allowedHatches = machine.getActiveShape().hatchFlags.values();
        // port-26.1.2: был Kotlin-экстеншен matchedHatches (YAIUtil.kt) — собираем HatchFlags.Builder из matched хатчей
        HatchFlags matchedHatches = matchedHatches(machine);

        if (machine instanceof LargeTankMultiblockBlockEntity) {
            if (anyAllows(allowedHatches, HatchTypes.LARGE_TANK) && !matchedHatches.allows(HatchTypes.LARGE_TANK)) {
                diagnosis.add(DiagnosisType.NO_LARGE_TANK_HATCH);
            }
        } else if (machine instanceof MultiblockInventoryComponentHolder holder) {
            diagnoseMultiblockInventory(holder.getMultiblockInventoryComponent(), allowedHatches, diagnosis);
        } else {
            MultiblockInventoryComponent inventory = machine.components.getNullable(MultiblockInventoryComponent.class);
            if (inventory != null) {
                diagnoseMultiblockInventory(inventory, allowedHatches, diagnosis);
            }
        }

        if (machine instanceof EnergyListComponentHolder holder) {
            boolean warning = true;
            if (anyAllows(allowedHatches, HatchTypes.ENERGY_INPUT) && !matchedHatches.allows(HatchTypes.ENERGY_INPUT)) {
                diagnosis.add(DiagnosisType.NO_ENERGY_INPUT);
                warning = false;
            }
            if (anyAllows(allowedHatches, HatchTypes.ENERGY_OUTPUT) && !matchedHatches.allows(HatchTypes.ENERGY_OUTPUT)) {
                diagnosis.add(DiagnosisType.NO_ENERGY_OUTPUT);
                warning = true;
            }
            // port-26.1.2: EnergyAccess.getEu() вместо getAmountAsLong()
            if (holder.getEnergyComponents().stream().allMatch(e -> e.getEu() == 0L)) {
                diagnosis.add(warning ? DiagnosisType.NO_ENERGY_WARNING : DiagnosisType.NO_ENERGY);
            }
        } else if (machine instanceof SteamCraftingMultiblockBlockEntity || machine instanceof AbstractSteamMultipliedCraftingMultiblockBlockEntity) {
            // port-26.1.2: diagnoseSteam не портирован — стаб
        }
    }

    // port-26.1.2: реплика Kotlin-экстендера matchedHatches из YAIUtil.kt
    private static HatchFlags matchedHatches(MultiblockMachineBlockEntity machine) {
        HatchFlags.Builder builder = new HatchFlags.Builder();
        for (HatchBlockEntity hatch : ((MultiblockMachineBlockEntityAccessor) machine).getShapeMatcher().getMatchedHatches()) {
            builder.with(hatch.getHatchType());
        }
        return builder.build();
    }

    private static boolean anyAllows(Collection<HatchFlags> flags, aztech.modern_industrialization.machines.multiblocks.HatchType type) {
        return flags.stream().anyMatch(f -> f.allows(type));
    }

    private void diagnoseMultiblockInventory(InventoryAccess access, Collection<HatchFlags> allowedHatches, Set<DiagnosisType> diagnosis) {
        if (access.getItemInputs().isEmpty() && anyAllows(allowedHatches, HatchTypes.ITEM_INPUT)) diagnosis.add(DiagnosisType.CANT_TAKE_ITEM_INPUT);
        if (access.getItemOutputs().isEmpty() && anyAllows(allowedHatches, HatchTypes.ITEM_OUTPUT)) diagnosis.add(DiagnosisType.CANT_PUT_ITEM_OUTPUT_WARNING);
        if (access.getFluidInputs().isEmpty() && anyAllows(allowedHatches, HatchTypes.FLUID_INPUT)) diagnosis.add(DiagnosisType.CANT_TAKE_FLUID_INPUT);
        if (access.getFluidOutputs().isEmpty() && anyAllows(allowedHatches, HatchTypes.FLUID_OUTPUT)) diagnosis.add(DiagnosisType.CANT_PUT_FLUID_OUTPUT_WARNING);
    }

    private void diagnosePreRecipe(CrafterComponent crafter, Set<DiagnosisType> diagnosis) {
        // port-26.1.2: приватный CrafterComponent.getRecipes() (MI 2.6) — через @Invoker нашего аксессора
        List<RecipeHolder<MachineRecipe>> recipes = new ArrayList<>();
        ((CrafterComponentAccessor) crafter).invokeGetRecipes().forEach(recipes::add);
        if (recipes.isEmpty()) {
            diagnosis.add(DiagnosisType.NO_RECIPE);
            return;
        }
        // апстрим: все рецепты забанены behavior.banRecipe → BANNED_RECIPE
        boolean allBanned = true;
        for (RecipeHolder<MachineRecipe> holder : recipes) {
            if (!crafter.getBehavior().banRecipe(holder.value())) {
                allBanned = false;
                break;
            }
        }
        if (allBanned) {
            diagnosis.add(DiagnosisType.BANNED_RECIPE);
        }
    }

    private void diagnoseRecipe(MachineRecipe recipe, CrafterComponent crafter, Set<DiagnosisType> diagnosis) {
        CrafterComponentAccessor crafterAccessor = (CrafterComponentAccessor) crafter;
        if (!crafterAccessor.invokeTakeItemInputs(recipe, true)) diagnosis.add(DiagnosisType.CANT_TAKE_ITEM_INPUT);
        if (!crafterAccessor.invokeTakeFluidInputs(recipe, true)) diagnosis.add(DiagnosisType.CANT_TAKE_FLUID_INPUT);
        if (!crafterAccessor.invokePutItemOutputs(recipe, true, false)) diagnosis.add(DiagnosisType.CANT_PUT_ITEM_OUTPUT);
        if (!crafterAccessor.invokePutFluidOutputs(recipe, true, false)) diagnosis.add(DiagnosisType.CANT_PUT_FLUID_OUTPUT);
        if (!recipe.conditionsMatch(crafterAccessor.getConditionContext())) diagnosis.add(DiagnosisType.UNMET_CONDITION);
    }

    private void sendDiagnosis(Player player, Set<DiagnosisType> diagnosis, MachineBlockEntity machine) {
        YAIText text = YAI.text();
        Component machineName = machine.getBlockState().getBlock().getName().copy().withStyle(TextHelper.NUMBER_TEXT);
        if (diagnosis.isEmpty()) {
            player.sendSystemMessage(text.diagnoserTitleNone(machineName).withStyle(TextHelper.NEUTRONS));
            return;
        }
        // port-26.1.2: YAIText.DIAGNOSER_TITLE.text(...) → прокси-инстанс YAI.text().diagnoserTitle(...)
        MutableComponent title = text.diagnoserTitle(machineName).withStyle(TextHelper.GRAY_TEXT);
        // апстрим: сортировка по Severity (ERROR выше WARNING), стабильная — порядок EnumSet сохраняется
        List<DiagnosisType> sorted = diagnosis.stream()
                .sorted(Comparator.comparingInt(t -> t.severity.ordinal()))
                .toList();
        for (int i = 0; i < sorted.size(); i++) {
            DiagnosisType type = sorted.get(i);
            MutableComponent diagnosisComp = Component.literal("⚠ ").withStyle(type.severity.darkColor);
            diagnosisComp.append(type.title.apply(text).withStyle(type.severity.lightColor));
            // port-26.1.2: HoverEvent стал интерфейсом — текстовый ховер это запись HoverEvent.ShowText
            diagnosisComp.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(
                    type.description.apply(text).withStyle(TextHelper.GRAY_TEXT))));
            if (i != sorted.size() - 1) diagnosisComp.append("\n");
            title.append(diagnosisComp);
        }
        player.sendSystemMessage(title);
    }

    enum DiagnosisType {
        NO_STEAM(YAIText::diagnoserNoSteam, YAIText::diagnoserNoSteamDescription, Severity.ERROR),
        NO_ENERGY(YAIText::diagnoserNoEnergy, YAIText::diagnoserNoEnergyDescription, Severity.ERROR),
        NO_ENERGY_WARNING(YAIText::diagnoserNoEnergy, YAIText::diagnoserNoEnergyWarningDescription, Severity.WARNING),
        NO_ENERGY_INPUT(YAIText::diagnoserNoEnergyInput, YAIText::diagnoserNoEnergyInputDescription, Severity.ERROR),
        NO_ENERGY_OUTPUT(YAIText::diagnoserNoEnergyOutput, YAIText::diagnoserNoEnergyOutputDescription, Severity.ERROR),
        NO_RECIPE(YAIText::diagnoserNoRecipe, YAIText::diagnoserNoRecipeDescription, Severity.ERROR),
        BANNED_RECIPE(YAIText::diagnoserBannedRecipe, YAIText::diagnoserBannedRecipeDescription, Severity.ERROR),
        CANT_TAKE_ITEM_INPUT(YAIText::diagnoserCantTakeItemInput, YAIText::diagnoserCantTakeItemInputDescription, Severity.WARNING),
        CANT_TAKE_FLUID_INPUT(YAIText::diagnoserCantTakeFluidInput, YAIText::diagnoserCantTakeFluidInputDescription, Severity.WARNING),
        CANT_PUT_ITEM_OUTPUT(YAIText::diagnoserCantPutItemOutput, YAIText::diagnoserCantPutItemOutputDescription, Severity.ERROR),
        CANT_PUT_FLUID_OUTPUT(YAIText::diagnoserCantPutFluidOutput, YAIText::diagnoserCantPutFluidOutputDescription, Severity.ERROR),
        CANT_PUT_ITEM_OUTPUT_WARNING(YAIText::diagnoserCantPutItemOutput, YAIText::diagnoserCantPutItemOutputDescription, Severity.WARNING),
        CANT_PUT_FLUID_OUTPUT_WARNING(YAIText::diagnoserCantPutFluidOutput, YAIText::diagnoserCantPutFluidOutputDescription, Severity.WARNING),
        UNMET_CONDITION(YAIText::diagnoserUnmetCondition, YAIText::diagnoserUnmetConditionDescription, Severity.ERROR),
        INVALID_MULTIBLOCK_SHAPE(YAIText::diagnoserInvalidMultiblockShape, YAIText::diagnoserInvalidMultiblockShapeDescription, Severity.ERROR),
        NO_LARGE_TANK_HATCH(YAIText::diagnoserNoLargeTankHatch, YAIText::diagnoserNoLargeTankHatchDescription, Severity.ERROR);

        // port-26.1.2: YAIText — интерфейс, текст через прокси YAI.text(); храняем method-reference
        final Function<YAIText, MutableComponent> title;
        final Function<YAIText, MutableComponent> description;
        final Severity severity;

        DiagnosisType(Function<YAIText, MutableComponent> title, Function<YAIText, MutableComponent> description, Severity severity) {
            this.title = title;
            this.description = description;
            this.severity = severity;
        }

        enum Severity {
            ERROR(ChatFormatting.RED, ChatFormatting.DARK_RED),
            WARNING(ChatFormatting.YELLOW, ChatFormatting.GOLD);

            final ChatFormatting lightColor;
            final ChatFormatting darkColor;

            Severity(ChatFormatting lightColor, ChatFormatting darkColor) {
                this.lightColor = lightColor;
                this.darkColor = darkColor;
            }
        }
    }
}
