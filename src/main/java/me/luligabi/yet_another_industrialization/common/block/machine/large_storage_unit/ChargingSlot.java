package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit;

import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.inventory.HackySlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.gui.GuiComponent.MenuFacade;
import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public final class ChargingSlot implements GuiComponentServer<Unit, Unit> {
    public static final Type<Unit, Unit> TYPE = new Type<>(YAIIds.id("charging_slot"),
            StreamCodec.unit(Unit.INSTANCE), StreamCodec.unit(Unit.INSTANCE));
    public static final int SLOT_X = -21;
    public static final int SLOT_Y = 76;
    private final MachineBlockEntity machine;
    private final ChargingSlotComponent charged;
    public ChargingSlot(MachineBlockEntity machine, ChargingSlotComponent charged) {
        this.machine = machine;
        this.charged = charged;
    }
    @Override public void setupMenu(MenuFacade menu) {
        menu.addSlotToMenu(new HackySlot(SLOT_X, SLOT_Y) {
            @Override protected ItemStack getRealStack() { return charged.getChargingItem(); }
            @Override protected void setRealStack(ItemStack stack) { charged.setChargingItem(machine, stack); }
            @Override public boolean mayPlace(ItemStack stack) { return ChargingSlot.mayPlace(stack); }
        }, SlotGroup.CONFIGURABLE_STACKS);
    }
    public static boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty() && ItemAccess.forStack(stack).getCapability(EnergyApi.ITEM) != null;
    }
    @Override public Unit getParams() { return Unit.INSTANCE; }
    @Override public Unit extractData() { return Unit.INSTANCE; }
    @Override public Type<Unit, Unit> getType() { return TYPE; }
}
