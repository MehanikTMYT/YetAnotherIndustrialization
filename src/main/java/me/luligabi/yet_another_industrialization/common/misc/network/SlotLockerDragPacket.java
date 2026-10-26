package me.luligabi.yet_another_industrialization.common.misc.network;
import java.util.Objects;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
public record SlotLockerDragPacket(int containerId, int slotIndex, boolean isItem,
        ItemResource itemVariant, FluidResource fluidVariant) implements CustomPacketPayload {
    public static final Type<SlotLockerDragPacket> TYPE = new Type<>(YAIIds.id("slot_locker_drag_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SlotLockerDragPacket> STREAM_CODEC =
        StreamCodec.ofMember(SlotLockerDragPacket::write, SlotLockerDragPacket::read);
    public SlotLockerDragPacket {
        Objects.requireNonNull(itemVariant); Objects.requireNonNull(fluidVariant);
    }
    public SlotLockerDragPacket(int containerId, int slotIndex, ItemResource item) {
        this(containerId, slotIndex, true, item, FluidResource.EMPTY);
    }
    public SlotLockerDragPacket(int containerId, int slotIndex, FluidResource fluid) {
        this(containerId, slotIndex, false, ItemResource.EMPTY, fluid);
    }
    @Override public Type<SlotLockerDragPacket> type() { return TYPE; }
    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(containerId); buffer.writeVarInt(slotIndex); buffer.writeBoolean(isItem);
        if (isItem) ItemResource.STREAM_CODEC.encode(buffer, itemVariant);
        else FluidResource.STREAM_CODEC.encode(buffer, fluidVariant);
    }
    private static SlotLockerDragPacket read(RegistryFriendlyByteBuf buffer) {
        int container = buffer.readInt(), slot = buffer.readVarInt();
        return buffer.readBoolean() ? new SlotLockerDragPacket(container, slot, ItemResource.STREAM_CODEC.decode(buffer))
            : new SlotLockerDragPacket(container, slot, FluidResource.STREAM_CODEC.decode(buffer));
    }
    public boolean apply(ServerPlayer player) {
        var menu = player.containerMenu;
        if (menu.containerId != containerId || slotIndex < 0 || slotIndex >= menu.slots.size()
                || !menu.stillValid(player)) return false;
        var slot = menu.getSlot(slotIndex);
        if (!slot.mayPickup(player)) return false;
        var stack = slot.getItem();
        if (!(stack.getItem() instanceof YAIDraggable draggable)) return false;
        boolean changed = isItem ? draggable.dragItem(stack, itemVariant, false) : draggable.dragFluid(stack, fluidVariant, false);
        if (changed) { slot.setChanged(); menu.broadcastChanges(); }
        return changed;
    }
}
