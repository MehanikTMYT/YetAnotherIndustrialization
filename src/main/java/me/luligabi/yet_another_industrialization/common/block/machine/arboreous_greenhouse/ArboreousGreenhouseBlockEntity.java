package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractElectricCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.OverdriveComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.YAIRecipeTypes;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedActiveShapeComponent;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection;
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier;
import me.luligabi.yet_another_industrialization.mixin.AbstractCraftingMultiblockBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * port-26.1.2: перенос Kotlin {@code arboreous_greenhouse/ArboreousGreenhouseBlockEntity.kt}.
 *
 * <p>Факты API26 (javap MI 2.6.0-26.1.2-fleet01 + neoForm 26.1.2):</p>
 * <ul>
 *   <li>{@code AbstractElectricCraftingMultiblockBlockEntity(BEP, Identifier, OrientationComponent.Params, ShapeTemplate[])}
 *       сам реализует {@code CrafterComponent.Behavior} (getBehavior final), держит
 *       {@code redstoneControl} и регистрирует SlotPanel-независимо CraftingMultiblockGui —
 *       наш класс добавляет только SlotPanel с upgrades/overdrive + SuppliedShapeSelection;</li>
 *   <li>{@code MachineTier.getBaseEu()/getMaxEu()} — int; {@code UpgradeComponent.getAddMaxEUPerTick()} — long;</li>
 *   <li>{@code components.unregister(...)} — {@code ComponentStorage#unregister(C...)}: базовый
 *       {@code activeShape} снимается, как в апстриме (два компонента не должны писать один
 *       NBT-ключ, а базовый {@code getActiveShape()} читает своё поле);</li>
 *   <li>{@code createShapeMatcher()} вызывает виртуальный {@code getActiveShape()} (javap -c), поэтому
 *       переопределения достаточно для выбора формы;</li>
 *   <li>{@code level.isClientSide()} / {@code level.getGameTime()} — методы (ловушка AGENTS.md 19).</li>
 * </ul>
 *
 * <p>REI-регистрация форм ({@code ReiMachineRecipes.registerMultiblockShape} апстрима) НЕ перенесена:
 * флот — EMI-only, зависимость REI запрещена (остаток, см. отчёт).</p>
 */
public class ArboreousGreenhouseBlockEntity extends AbstractElectricCraftingMultiblockBlockEntity
        implements EnergyListComponentHolder {

    public static final String ID = "arboreous_greenhouse";
    public static final String NAME = "Arboreous Greenhouse";

    /*
     * Паттерны апстрима: _ -> air, # -> casing, $ -> soil, @ -> glass, x -> lantern.
     */
    private static final List<String> BASE_PATTERN = List.of(
            "__#####__",
            "_#$$$$$#_",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "_#$$$$$#_",
            "__#####__"
    );

    private static final List<String> DOME_1_PATTERN = List.of(
            "__@@@@@__",
            "_#_____#_",
            "@_______@",
            "@_______@",
            "@_______@",
            "@_______@",
            "@_______@",
            "_#_____#_",
            "__@@@@@__"
    );

    private static final List<String> DOME_2_PATTERN = List.of(
            "___@@@___",
            "__#___#__",
            "_#_____#_",
            "@_______@",
            "@_______@",
            "@_______@",
            "_#_____#_",
            "__#___#__",
            "___@@@___"
    );

    private static final List<String> DOME_3_PATTERN = List.of(
            "_________",
            "___###___",
            "__#___#__",
            "_#_____#_",
            "_#_____#_",
            "_#_____#_",
            "__#___#__",
            "___###___",
            "_________"
    );

    private static final List<String> DOME_4_PATTERN = List.of(
            "_________",
            "_________",
            "___@@@___",
            "__@___@__",
            "__@_x_@__",
            "__@___@__",
            "___@@@___",
            "_________",
            "_________"
    );

    private static final List<String> DOME_5_PATTERN = List.of(
            "_________",
            "_________",
            "_________",
            "___@@@___",
            "___@#@___",
            "___@@@___",
            "_________",
            "_________",
            "_________"
    );

    private static final SimpleMember HANGING_LANTERN_MEMBER = new SimpleMember() {
        @Override
        public boolean matchesState(BlockState state, BlockEntity be) {
            return state.is(Blocks.LANTERN) && state.getValue(LanternBlock.HANGING);
        }

        @Override
        public BlockEntity newBlockEntity(RegistryAccess registries, Level level, BlockPos pos, BlockState state) {
            return null;
        }

        @Override
        public BlockState getPreviewState() {
            return Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        }
    };

    private static final HatchFlags HATCHES = new HatchFlags.Builder()
            .with(HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT,
                    HatchTypes.FLUID_INPUT, HatchTypes.ENERGY_INPUT)
            .build();

    /** Тиры и шаблоны форм считаются из data-map'а (см. {@link #initTiers()}). */
    private static List<Tier> TIERS = List.of();
    private static ShapeTemplate[] SHAPE_TEMPLATES = new ShapeTemplate[0];

    private final SuppliedActiveShapeComponent activeSoil =
            new SuppliedActiveShapeComponent(() -> SHAPE_TEMPLATES);
    private final SaplingComponent sapling = new SaplingComponent();
    private final UpgradeComponent upgrades = new UpgradeComponent();
    private final OverdriveComponent overdrive = new OverdriveComponent();

    private boolean oldActive = false;

    public ArboreousGreenhouseBlockEntity(BEP bep) {
        super(bep, YAIIds.id(ID), new OrientationComponent.Params(false, false, false), SHAPE_TEMPLATES);

        components.unregister(activeShape);
        registerComponents(activeSoil, sapling, upgrades, overdrive);

        registerGuiComponent(new SlotPanel(this)
                .withRedstoneControl(redstoneControl)
                .withUpgrades(upgrades)
                .withOverdrive(overdrive));

        registerGuiComponent(new SuppliedShapeSelection(new ShapeSelection.Behavior() {
            @Override
            public void handleClick(int line, int delta) {
                activeSoil.incrementShape(ArboreousGreenhouseBlockEntity.this, delta);
            }

            @Override
            public int getCurrentIndex(int line) {
                return activeSoil.getActiveShape();
            }
        }, this::getSoilInfo));
    }

    @Override
    public void tickExtra() {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (level.getGameTime() % 20L != 0L) {
            return;
        }
        boolean newActive = ((AbstractCraftingMultiblockBlockEntityAccessor) this).getIsActive().isActive;
        if (oldActive != newActive) {
            if (newActive) {
                sapling.update(crafter, this);
            } else {
                sapling.reset(this);
            }
        }
        oldActive = newActive;
    }

    @Override
    public void onCraft() {
        sapling.update(crafter, this);
    }

    @Override
    public aztech.modern_industrialization.machines.recipe.MachineRecipeType recipeType() {
        return YAIRecipeTypes.ARBOREOUS_GREENHOUSE;
    }

    @Override
    public long getBaseRecipeEu() {
        return MachineTier.MULTIBLOCK.getBaseEu();
    }

    @Override
    public long getMaxRecipeEu() {
        return MachineTier.MULTIBLOCK.getMaxEu() + upgrades.getAddMaxEUPerTick();
    }

    @Override
    public boolean isOverdriving() {
        return overdrive.shouldOverdrive();
    }

    @Override
    public ShapeTemplate getActiveShape() {
        return shapeAt(activeSoil.getActiveShape());
    }

    @Override
    public ShapeTemplate getBigShape() {
        return SHAPE_TEMPLATES.length == 0 ? null : SHAPE_TEMPLATES[SHAPE_TEMPLATES.length - 1];
    }

    private static ShapeTemplate shapeAt(int index) {
        if (SHAPE_TEMPLATES.length == 0) {
            // до initTiers() (DataMapsUpdatedEvent, старт сервера/клиента) машин в мире быть не может
            return null;
        }
        return SHAPE_TEMPLATES[Math.max(0, Math.min(index, SHAPE_TEMPLATES.length - 1))];
    }

    private ShapeSelection.LineInfo getSoilInfo() {
        List<Component> names = new ArrayList<>(TIERS.size());
        for (Tier tier : TIERS) {
            names.add(tier.getDisplayName());
        }
        return new ShapeSelection.LineInfo(names, true);
    }

    /** Тиры теплицы (для {@link ArboreousGreenhouseTierCondition}). */
    public static List<Tier> tiers() {
        return TIERS;
    }

    /** Индекс выбранного тира почвы (для {@link ArboreousGreenhouseTierCondition}). */
    public int getActiveSoilIndex() {
        return activeSoil.getActiveShape();
    }

    /** Вызывается на {@code DataMapsUpdatedEvent(Registries.BLOCK)} — см. YAI.java. */
    public static void initTiers() {
        List<Tier> registrationTiers = new ArrayList<>();
        ArboreousGreenhouseTier.all().forEach((blockKey, tier) -> {
            Block block = BuiltInRegistries.BLOCK.getValue(blockKey);
            Tier existing = findById(registrationTiers, tier.id());
            if (existing != null) {
                if (block != null) {
                    existing.validSoils().add(block);
                }
                return;
            }
            Tier registered = toRegisteredTier(tier);
            if (block != null) {
                registered.validSoils().add(block);
            }
            registrationTiers.add(registered);
        });
        registrationTiers.sort(Comparator.comparingInt(Tier::sortOrder));
        TIERS = List.copyOf(registrationTiers);

        ShapeTemplate[] templates = new ShapeTemplate[TIERS.size()];
        for (int i = 0; i < TIERS.size(); i++) {
            SimpleMember soil = new TierSimpleMember(TIERS.get(i));
            ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.HEATPROOF);
            addLayer(builder, 0, BASE_PATTERN, soil);
            addLayer(builder, 1, DOME_1_PATTERN, soil);
            addLayer(builder, 2, DOME_1_PATTERN, soil);
            addLayer(builder, 3, DOME_1_PATTERN, soil);
            addLayer(builder, 4, DOME_2_PATTERN, soil);
            addLayer(builder, 5, DOME_3_PATTERN, soil);
            addLayer(builder, 6, DOME_4_PATTERN, soil);
            addLayer(builder, 7, DOME_5_PATTERN, soil);
            templates[i] = builder.build();
        }
        SHAPE_TEMPLATES = templates;
        // REI multiblock shapes (апстрим ReiMachineRecipes.registerMultiblockShape) не переносим — EMI-only.
    }

    private static Tier findById(List<Tier> tiers, Identifier id) {
        for (Tier tier : tiers) {
            if (tier.id().equals(id)) {
                return tier;
            }
        }
        return null;
    }

    private static Tier toRegisteredTier(ArboreousGreenhouseTier tier) {
        return new Tier(tier.id(), tier.icon(), tier.translationKey(),
                tier.fluid(), tier.nutrientFluid(), tier.sortOrder());
    }

    /** Обход слоя паттерна: x = индекс символа (со смещением -4), z = индекс строки (апстрим addLayer). */
    private static ShapeTemplate.Builder addLayer(ShapeTemplate.Builder builder, int y, List<String> pattern,
                                                  SimpleMember soil) {
        SimpleMember casing = SimpleMember.forBlock(
                MIBlock.BLOCK_DEFINITIONS.get(MI.id("heatproof_machine_casing")));
        for (int z = 0; z < pattern.size(); z++) {
            String row = pattern.get(z);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (c == '_') {
                    continue;
                }
                SimpleMember member = switch (c) {
                    case '#' -> casing;
                    case '$' -> soil;
                    case '@' -> YAIMultiblockHelper.GLASS_MEMBER;
                    case 'x' -> HANGING_LANTERN_MEMBER;
                    default -> null;
                };
                if (member == null) {
                    continue;
                }
                if (member == casing) {
                    builder.add(x - 4, y, z, member, HATCHES);
                } else {
                    builder.add(x - 4, y, z, member);
                }
            }
        }
        return builder;
    }

    /** Зарегистрированный тир (апстрим: {@code data class Tier} — здесь обычный класс, т.к. record
     * не может содержать изменяемое поле {@code validSoils}). */
    public static final class Tier {
        private final Identifier id;
        private final Identifier icon;
        private final String translationKey;
        private final Optional<ArboreousGreenhouseTier.FluidByIdInput> fluid;
        private final Optional<ArboreousGreenhouseTier.FluidByIdInput> nutrientFluid;
        private final int sortOrder;
        private final Set<Block> validSoils = new HashSet<>();

        public Tier(Identifier id, Identifier icon, String translationKey,
                    Optional<ArboreousGreenhouseTier.FluidByIdInput> fluid,
                    Optional<ArboreousGreenhouseTier.FluidByIdInput> nutrientFluid,
                    int sortOrder) {
            this.id = id;
            this.icon = icon;
            this.translationKey = translationKey;
            this.fluid = fluid;
            this.nutrientFluid = nutrientFluid;
            this.sortOrder = sortOrder;
        }

        public Identifier id() {
            return id;
        }

        public Identifier icon() {
            return icon;
        }

        public String translationKey() {
            return translationKey;
        }

        public Optional<ArboreousGreenhouseTier.FluidByIdInput> fluid() {
            return fluid;
        }

        public Optional<ArboreousGreenhouseTier.FluidByIdInput> nutrientFluid() {
            return nutrientFluid;
        }

        public int sortOrder() {
            return sortOrder;
        }

        public BlockState blockState() {
            return BuiltInRegistries.BLOCK.getOptional(icon)
                    .map(Block::defaultBlockState)
                    .orElse(null);
        }

        public Set<Block> validSoils() {
            return validSoils;
        }

        public Component getDisplayName() {
            return Component.translatable(translationKey);
        }
    }

    private static final class TierSimpleMember implements SimpleMember {
        private final Tier tier;

        private TierSimpleMember(Tier tier) {
            this.tier = tier;
        }

        @Override
        public boolean matchesState(BlockState state, BlockEntity be) {
            return tier.validSoils().contains(state.getBlock());
        }

        @Override
        public BlockState getPreviewState() {
            BlockState iconState = tier.blockState();
            if (iconState != null) {
                return iconState;
            }
            return tier.validSoils().isEmpty() ? null
                    : tier.validSoils().iterator().next().defaultBlockState();
        }

        @Override
        public BlockEntity newBlockEntity(RegistryAccess registries, Level level, BlockPos pos, BlockState state) {
            return null;
        }
    }
}
