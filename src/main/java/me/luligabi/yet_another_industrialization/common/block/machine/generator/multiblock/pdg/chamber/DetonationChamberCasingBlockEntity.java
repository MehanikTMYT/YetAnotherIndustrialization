package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.PulseDetonationGeneratorBlockEntity;
import me.luligabi.yet_another_industrialization.mixin.AbstractCraftingMultiblockBlockEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * port-26.1.2: перенос Kotlin {@code pdg/chamber/DetonationChamberCasingBlockEntity.kt}.
 *
 * <p>Визуальная «камера сгорания» Pulse Detonation Generator: крутит прогресс от контроллера,
 * когда тот активен. Факты API26 (javap MI 2.6.0-26.1.2-fleet01): {@code MachineBlockEntity(BEP,
 * MachineGuiParameters, OrientationComponent.Params)}; {@code MachineGuiParameters.Builder(String, boolean)};
 * {@code OrientationComponent.Params.noFacingNoOutput()}; {@code MIInventory.EMPTY};
 * {@code CrafterComponent#getProgress()}; NBT — {@code MachineComponent#writeNbt(ValueOutput)} /
 * {@code #readNbt(ValueInput, boolean)} + {@code writeClientNbt/readClientNbt} (без CompoundTag).</p>
 */
public class DetonationChamberCasingBlockEntity extends MachineBlockEntity implements Tickable {

    public static final String ID = "detonation_chamber_casing";
    public static final String NAME = "Detonation Chamber Casing";

    private final Data data = new Data();

    private PulseDetonationGeneratorBlockEntity controller;

    public DetonationChamberCasingBlockEntity(BEP bep) {
        super(bep,
                new MachineGuiParameters.Builder(ID, false).build(),
                OrientationComponent.Params.noFacingNoOutput());
        registerComponents(data);
    }

    public void setController(PulseDetonationGeneratorBlockEntity controller) {
        this.controller = controller;
    }

    public PulseDetonationGeneratorBlockEntity getController() {
        return controller;
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide()) {
            return;
        }
        boolean controllerActive = controller != null
                && ((AbstractCraftingMultiblockBlockEntityAccessor) controller).getIsActive().isActive;
        if (!controllerActive) {
            if (data.progress <= 0f) {
                return;
            }
            data.progress -= 0.05f;
            data.onSequence = false;
            sync();
            return;
        }
        if (!data.onSequence) {
            data.progress = controller.getCrafterComponent().getProgress();
            if (data.progress > 0.99f) {
                data.onSequence = true;
            }
        } else {
            float progress = controller.getCrafterComponent().getProgress();
            float progressSqr = progress * progress;
            // visual progress on a repeated recipe starts at 68% (around when the red becomes more visible)
            data.progress = Math.min(0.68f + (progressSqr * 0.68f), 1f);
        }
        sync();
    }

    @Override
    public void openMenu(ServerPlayer player) {
        // апстрим: FIXME remove onUse to prevent attempt to open menu — меню не открываем
    }

    @Override
    public MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(YAIMachines.Casings.DETONATION_CHAMBER_CASING);
    }

    public static class Data implements MachineComponent {

        private float progress = 0f;
        private boolean onSequence = false;

        @Override
        public void writeNbt(ValueOutput output) {
            writeClientNbt(output);
            output.putBoolean("onSequence", onSequence);
        }

        @Override
        public void readNbt(ValueInput input, boolean isUpgradingMachine) {
            readClientNbt(input);
            onSequence = input.getBooleanOr("onSequence", false);
        }

        @Override
        public void writeClientNbt(ValueOutput output) {
            output.putFloat("progress", progress);
        }

        @Override
        public void readClientNbt(ValueInput input) {
            progress = input.getFloatOr("progress", 0f);
        }
    }
}
