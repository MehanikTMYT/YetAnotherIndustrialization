package me.luligabi.yet_another_industrialization;
import java.util.List;
import aztech.modern_industrialization.inventory.*;
import aztech.modern_industrialization.util.Simulation;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import me.luligabi.yet_another_industrialization.common.item.tools.StorageSlotLockerItem;
import me.luligabi.yet_another_industrialization.common.misc.component.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
public final class LockerChecks {
    private static int checks;
    private static void check(boolean value, String message) { checks++; if (!value) throw new AssertionError(message); }
    public static void run(net.minecraft.server.MinecraftServer server) {
        var item = ItemResource.of(Items.DIAMOND);
        var fluid = FluidResource.of(Fluids.WATER);
        for (var mode : SlotLockerData.Mode.values()) {
            var itemSlot = ConfigurableItemStack.standardInputSlot();
            var fluidSlot = ConfigurableFluidStack.standardInputSlot(4000);
            boolean unlock = mode.name().startsWith("UNLOCK");
            if (unlock) { itemSlot.playerLock(Items.DIAMOND, Simulation.ACT); fluidSlot.playerLock(Fluids.WATER, Simulation.ACT); }
            var inventory = new MIInventory(List.of(itemSlot), List.of(fluidSlot),
                new SlotPositions.Builder().addSlot(0,0).build(), new SlotPositions.Builder().addSlot(0,0).build());
            var data = new SlotLockerData(item, fluid, mode);
            check(StorageSlotLockerItem.applyLocks(inventory, data), "mode changes inventory " + mode);
            check(itemSlot.isPlayerLocked() == (mode.lockItem == null ? unlock : mode.lockItem), "item direction " + mode);
            check(fluidSlot.isPlayerLocked() == (mode.lockFluid == null ? unlock : mode.lockFluid), "fluid direction " + mode);
            check(!StorageSlotLockerItem.applyLocks(inventory, data), "repeated operation has no changes " + mode);
        }
        var protectedItem = ConfigurableItemStack.lockedInputSlot(Items.DIAMOND);
        var protectedFluid = ConfigurableFluidStack.lockedInputSlot(4000, Fluids.WATER);
        var protectedInventory = new MIInventory(List.of(protectedItem), List.of(protectedFluid),
            new SlotPositions.Builder().addSlot(0,0).build(), new SlotPositions.Builder().addSlot(0,0).build());
        check(!StorageSlotLockerItem.applyLocks(protectedInventory,
            new SlotLockerData(item, fluid, SlotLockerData.Mode.UNLOCK_BOTH)), "fixed machine slots remain locked");
        check(protectedItem.isPlayerLocked() && protectedFluid.isPlayerLocked(), "fixed locks unchanged");
        check(!StorageSlotLockerItem.applyLocks(MIInventory.EMPTY, SlotLockerData.EMPTY), "empty inventory");
        var stack = YAIItems.STORAGE_SLOT_LOCKER.toStack();
        var copy = stack.copy();
        var tool = YAIItems.STORAGE_SLOT_LOCKER.get();
        check(tool.dragItem(stack, item, true), "item simulation accepted");
        check(stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get()).isEmpty(), "simulation leaves component unchanged");
        check(tool.dragItem(stack, item, false) && tool.dragFluid(stack, fluid, false), "select item and fluid");
        check(stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get()).itemVariant().equals(item)
            && stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get()).fluidVariant().equals(fluid), "both selections retained");
        check(copy.get(YAIDataComponents.SLOT_LOCKER_DATA.get()).isEmpty(), "copy unaffected by selection");
        check(tool.getTooltipImage(stack).isPresent(), "tooltip data");
        stack.remove(YAIDataComponents.SLOT_LOCKER_DATA.get());
        check(!tool.dragItem(stack, item, false) && !tool.dragFluid(stack, fluid, true), "missing component rejected");

        var player = net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(server.overworld());
        var selected = YAIItems.STORAGE_SLOT_LOCKER.toStack();
        player.getInventory().setItem(0, selected);
        check(!new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerChangeModePacket(-1).apply(player), "negative inventory index");
        check(!new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerChangeModePacket(10000).apply(player), "oversize inventory index");
        check(new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerChangeModePacket(0).apply(player), "mode handler");
        check(selected.get(YAIDataComponents.SLOT_LOCKER_DATA.get()).mode() == SlotLockerData.Mode.LOCK_FLUID, "mode advanced");
        int slotIndex = -1;
        for (int i = 0; i < player.containerMenu.slots.size(); i++)
            if (player.containerMenu.getSlot(i).getItem() == selected) slotIndex = i;
        check(slotIndex >= 0, "find held tool in menu");
        var packet = new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket(
            player.containerMenu.containerId, slotIndex, item);
        check(packet.apply(player), "drag handler");
        check(selected.get(YAIDataComponents.SLOT_LOCKER_DATA.get()).itemVariant().equals(item), "drag updates actual player stack");
        check(!new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket(
            player.containerMenu.containerId + 1, slotIndex, fluid).apply(player), "stale container rejected");
        check(!new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket(
            player.containerMenu.containerId, -1, fluid).apply(player), "negative menu index rejected");
        check(!new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket(
            player.containerMenu.containerId, 10000, fluid).apply(player), "oversize menu index rejected");
        for (var payload : List.of(packet, new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket(
                player.containerMenu.containerId, slotIndex, fluid))) {
            var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), server.registryAccess());
            try {
                var codec = me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket.STREAM_CODEC;
                codec.encode(buffer, payload);
                check(codec.decode(buffer).equals(payload) && buffer.readableBytes() == 0, "drag payload roundtrip");
            } finally { buffer.release(); }
        }
        System.out.println("YAI_LOCKER_PASS checks=" + checks);
    }
}
