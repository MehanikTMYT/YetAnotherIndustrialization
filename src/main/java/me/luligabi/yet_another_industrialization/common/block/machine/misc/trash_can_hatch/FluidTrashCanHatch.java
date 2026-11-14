package me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class FluidTrashCanHatch extends HatchBlockEntity {
    public static final String ID = "fluid_trash_can_hatch";
    public static final String NAME = "Fluid Trash Can Hatch";
    private final MIInventory inventory;

    public FluidTrashCanHatch(BEP bep, boolean input, Identifier blockId) {
        super(bep, new MachineGuiParameters.Builder(blockId, true).build(),
                new OrientationComponent.Params(false, false, false));
        inventory = new MIInventory(List.of(), List.of(new ConfigurableFluidStack(16_000)),
                SlotPositions.empty(), new SlotPositions.Builder().addSlot(80, 40).build());
        registerComponents(inventory);
    }
    @Override public void tickTransfer() {
        for (var stack : inventory.getFluidStacks()) stack.setAmount(0);
    }
    @Override public void appendFluidOutputs(List<ConfigurableFluidStack> list) { list.addAll(inventory.getFluidStacks()); }
    @Override public HatchType getHatchType() { return HatchTypes.FLUID_OUTPUT; }
    @Override public boolean upgradesToSteel() { return true; }
    @Override public MIInventory getInventory() { return inventory; }
}
