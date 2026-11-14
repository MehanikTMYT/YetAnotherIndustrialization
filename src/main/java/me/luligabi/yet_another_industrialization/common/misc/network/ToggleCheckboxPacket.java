package me.luligabi.yet_another_industrialization.common.misc.network;

import aztech.modern_industrialization.machines.gui.MachineMenuServer;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ToggleCheckboxPacket(int syncId, boolean value) implements CustomPacketPayload {
    public static final Type<ToggleCheckboxPacket> TYPE = new Type<>(YAIIds.id("toggle_checkbox"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleCheckboxPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ToggleCheckboxPacket::syncId,
            ByteBufCodecs.BOOL, ToggleCheckboxPacket::value, ToggleCheckboxPacket::new);
    @Override public Type<ToggleCheckboxPacket> type() { return TYPE; }

    public void apply(ServerPlayer player) {
        if (player.containerMenu instanceof MachineMenuServer menu && menu.containerId == syncId
                && menu.stillValid(player)) {
            var checkbox = menu.blockEntity.guiComponents.getNullable(ToggleCheckbox.class);
            if (checkbox != null) checkbox.action.accept(value);
        }
    }
}
