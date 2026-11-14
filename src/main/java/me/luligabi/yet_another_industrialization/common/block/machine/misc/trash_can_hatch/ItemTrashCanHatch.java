package me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
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

public final class ItemTrashCanHatch extends HatchBlockEntity {
    public static final String ID = "item_trash_can_hatch";
    public static final String NAME = "Item Trash Can Hatch";
    private final MIInventory inventory;

    public ItemTrashCanHatch(BEP bep, boolean input, Identifier blockId) {
        super(bep, new MachineGuiParameters.Builder(blockId, true).build(),
                new OrientationComponent.Params(false, false, false));
        inventory = new MIInventory(java.util.stream.IntStream.range(0, 2).mapToObj(i -> new ConfigurableItemStack()).toList(), List.of(),
                new SlotPositions.Builder().addSlots(80, 30, 1, 2).build(), SlotPositions.empty());
        registerComponents(inventory);
    }
    @Override public void tickTransfer() {
        for (var stack : inventory.getItemStacks()) stack.setAmount(0);
    }
    @Override public void appendItemOutputs(List<ConfigurableItemStack> list) { list.addAll(inventory.getItemStacks()); }
    @Override public HatchType getHatchType() { return HatchTypes.ITEM_OUTPUT; }
    @Override public boolean upgradesToSteel() { return true; }
    @Override public MIInventory getInventory() { return inventory; }
}
