package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit;

import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.DropableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.swedz.tesseract.neoforge.compat.mi.api.ComponentStackHolder;

/** Persistent charging slot, also exposed to the existing component-stack-holder mixin. */
public final class ChargingSlotComponent implements MachineComponent.ServerOnly, DropableComponent, ComponentStackHolder {
    public static final String KEY = "chargingSlotStack";
    private ItemStack chargingItem = ItemStack.EMPTY;

    public void chargeItem(MIEnergyStorage storage) {
        if (chargingItem.isEmpty()) return;
        var itemStorage = ItemAccess.forStack(chargingItem).getCapability(EnergyApi.ITEM);
        if (itemStorage == null) return;
        // NeoForge26: EnergyHandlerUtil.move journals both handlers; no simulated mutation survives.
        int limit = (int) Math.min(Integer.MAX_VALUE, itemStorage.getCapacityAsLong() / 8);
        EnergyHandlerUtil.move(storage, itemStorage, limit, null);
    }

    public boolean hasItem() { return !chargingItem.isEmpty(); }
    public ItemStack getChargingItem() { return chargingItem; }
    public void setChargingItem(MachineBlockEntity machine, ItemStack stack) {
        setStack(stack);
        machine.setChanged();
        machine.sync();
    }
    @Override public ItemStack getDrop() { return chargingItem; }
    @Override public ItemStack getStack() { return chargingItem; }
    @Override public void setStack(ItemStack stack) { chargingItem = stack; }
    @Override public void writeNbt(ValueOutput output) { output.store(KEY, ItemStack.OPTIONAL_CODEC, chargingItem); }
    @Override public void readNbt(ValueInput input, boolean upgrading) {
        chargingItem = input.read(KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }
}
