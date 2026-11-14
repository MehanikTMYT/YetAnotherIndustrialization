package me.luligabi.yet_another_industrialization.common.block.machine.misc;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes;

import java.util.List;

public final class MixedHatch extends HatchBlockEntity {
    private final boolean input;
    private final boolean upgradesToSteel;
    private final MIInventory inventory;

    public MixedHatch(BEP bep, String machineId, boolean input, boolean upgradesToSteel,
                      MIInventory inventory, int backgroundHeight) {
        super(bep, new MachineGuiParameters.Builder(machineId, true).backgroundHeight(backgroundHeight).build(),
                new OrientationComponent.Params(true, true, true));
        this.input = input;
        this.upgradesToSteel = upgradesToSteel;
        this.inventory = inventory;
        registerComponents(inventory);
        registerGuiComponent(new AutoExtract(orientation, input));
    }

    @Override public void appendItemInputs(List<ConfigurableItemStack> list) {
        if (input) list.addAll(inventory.getItemStacks());
    }
    @Override public void appendItemOutputs(List<ConfigurableItemStack> list) {
        if (!input) list.addAll(inventory.getItemStacks());
    }
    @Override public void appendFluidInputs(List<ConfigurableFluidStack> list) {
        if (input) list.addAll(inventory.getFluidStacks());
    }
    @Override public void appendFluidOutputs(List<ConfigurableFluidStack> list) {
        if (!input) list.addAll(inventory.getFluidStacks());
    }
    @Override public void tickTransfer() {
        if (orientation.extractItems) {
            if (input) inventory.autoInsertItems(level, worldPosition, orientation.outputDirection);
            else inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
        }
        if (orientation.extractFluids) {
            if (input) inventory.autoInsertFluids(level, worldPosition, orientation.outputDirection);
            else inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection);
        }
    }
    @Override public HatchType getHatchType() { return input ? YAIHatchTypes.MIXED_INPUT : YAIHatchTypes.MIXED_OUTPUT; }
    @Override public boolean upgradesToSteel() { return upgradesToSteel; }
    @Override public MIInventory getInventory() { return inventory; }
}
