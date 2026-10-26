package me.luligabi.yet_another_industrialization.common.misc.network;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
public record SlotLockerChangeModePacket(int slotIndex) implements CustomPacketPayload {
    public static final Type<SlotLockerChangeModePacket> TYPE = new Type<>(YAIIds.id("slot_locker_change_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SlotLockerChangeModePacket> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.VAR_INT, SlotLockerChangeModePacket::slotIndex, SlotLockerChangeModePacket::new);
    @Override public Type<SlotLockerChangeModePacket> type() { return TYPE; }
    public boolean apply(ServerPlayer player) {
        if (slotIndex < 0 || slotIndex >= player.getInventory().getContainerSize()) return false;
        var stack = player.getInventory().getItem(slotIndex);
        if (!stack.is(YAIItems.STORAGE_SLOT_LOCKER.get())) return false;
        var data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get());
        if (data == null) return false;
        var mode = data.mode().next();
        stack.set(YAIDataComponents.SLOT_LOCKER_DATA.get(), data.withMode(mode));
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        player.sendSystemMessage(Component.translatable("text.yet_another_industrialization.slot_locker_mode_change", mode.text()), true);
        return true;
    }
}
