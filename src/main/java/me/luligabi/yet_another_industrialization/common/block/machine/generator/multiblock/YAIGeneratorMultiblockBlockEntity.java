package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock;

import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.guicomponents.CraftingMultiblockGui;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.util.Simulation;
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * port-26.1.2: перенос Kotlin {@code generator/multiblock/YAIGeneratorMultiblockBlockEntity.kt} —
 * база генераторов-мультиблоков (Dragon Siphon, Pulse Detonation Generator).
 *
 * <p>Факты API26 (javap MI 2.6.0-26.1.2-fleet01):</p>
 * <ul>
 *   <li>{@code useItemOn(Player, InteractionHand, Direction)} → {@code InteractionResult}
 *       (ItemInteractionResult в 26 удалён — ловушка AGENTS.md 10);</li>
 *   <li>{@code RedstoneControlComponent#onUse(MachineBlockEntity, Player, InteractionHand)} → InteractionResult;</li>
 *   <li>{@code EnergyComponent#insertEu(long, Simulation)}; {@code Simulation.SIMULATE/ACT};</li>
 *   <li>{@code ShapeMatcher#isMatchSuccessful() / #getMatchedHatches()};
 *       {@code HatchBlockEntity#appendEnergyOutputs(List&lt;EnergyComponent&gt;)}</li>
 * </ul>
 *
 * <p>Примечание: формула {@code rem -= inserted} в {@link #insertEnergy} перенесена дословно из
 * апстрима (включая его особенность с накоплением) — поведение не менялось.</p>
 */
public abstract class YAIGeneratorMultiblockBlockEntity extends AbstractCraftingMultiblockBlockEntity
        implements CrafterComponent.Behavior, EnergyListComponentHolder {

    private final List<EnergyComponent> energyOutputs = new ArrayList<>();
    private final RedstoneControlComponent redstoneControl = new RedstoneControlComponent();

    protected YAIGeneratorMultiblockBlockEntity(BEP bep, Identifier id, ShapeTemplate[] shape) {
        this(bep, id, new OrientationComponent.Params(false, false, false), shape);
    }

    protected YAIGeneratorMultiblockBlockEntity(BEP bep, Identifier id,
                                                OrientationComponent.Params orientationParams,
                                                ShapeTemplate[] shape) {
        super(bep, id, orientationParams, shape);

        registerComponents(redstoneControl);
        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl));
        registerGuiComponent(new CraftingMultiblockGui(
                () -> shapeValid.shapeValid,
                () -> crafter.getProgress(),
                crafter,
                () -> 0));
    }

    @Override
    protected InteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        InteractionResult result = super.useItemOn(player, hand, face);
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand);
        }
        return result;
    }

    @Override
    public void onCraft() {
        var holder = ((CrafterComponentAccessor) crafter).getActiveRecipe();
        if (holder == null) {
            return;
        }
        MachineRecipe recipe = holder.value();
        if (recipe.conditions == null) {
            return;
        }
        EnergyGenerationCondition condition = null;
        for (var candidate : recipe.conditions) {
            if (candidate instanceof EnergyGenerationCondition energy) {
                condition = energy;
                break;
            }
        }
        if (condition == null) {
            return;
        }
        long amount = condition.amount();
        boolean hasInsertedEnergy = insertEnergy(amount, true) > 0;
        if (hasInsertedEnergy) {
            insertEnergy(amount, false);
        }
        onInsert(hasInsertedEnergy);
    }

    /** Вызывается после попытки вставки энергии: у PDG здесь взрыв (см. апстрим {@code onInsert}). */
    public void onInsert(boolean hasInsertedEnergy) {
    }

    public long insertEnergy(long value, boolean simulate) {
        long rem = value;
        long inserted = 0;
        for (EnergyComponent e : energyOutputs) {
            if (rem > 0) {
                inserted += e.insertEu(rem, simulate ? Simulation.SIMULATE : Simulation.ACT);
                rem -= inserted;
            }
        }
        return inserted;
    }

    @Override
    protected final void onRematch(ShapeMatcher shapeMatcher) {
        super.onRematch(shapeMatcher);
        if (shapeMatcher.isMatchSuccessful()) {
            energyOutputs.clear();
            for (HatchBlockEntity hatch : shapeMatcher.getMatchedHatches()) {
                hatch.appendEnergyOutputs(energyOutputs);
            }
            onSuccessfulRematch(shapeMatcher);
        } else {
            onFailedRematch(shapeMatcher);
        }
    }

    public void onSuccessfulRematch(ShapeMatcher shapeMatcher) {
    }

    public void onFailedRematch(ShapeMatcher shapeMatcher) {
    }

    @Override
    public long consumeEu(long max, Simulation simulation) {
        return max;
    }

    @Override
    public long getBaseRecipeEu() {
        return 0L;
    }

    @Override
    public long getMaxRecipeEu() {
        return 1L;
    }

    @Override
    public ServerLevel getCrafterWorld() {
        return level instanceof ServerLevel serverLevel ? serverLevel : null;
    }

    @Override
    public UUID getOwnerUuid() {
        return placedBy.placerId;
    }

    @Override
    protected CrafterComponent.Behavior getBehavior() {
        return this;
    }

    @Override
    public List<EnergyComponent> getEnergyComponents() {
        return energyOutputs;
    }

    @Override
    public boolean isEnabled() {
        return redstoneControl.doAllowNormalOperation(this);
    }
}
