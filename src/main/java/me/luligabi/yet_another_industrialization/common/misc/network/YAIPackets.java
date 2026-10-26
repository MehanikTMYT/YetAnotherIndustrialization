package me.luligabi.yet_another_industrialization.common.misc.network;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Native serverbound handlers; the sender comes exclusively from the network context. */
public final class YAIPackets {
    private YAIPackets() {}
    public static void init(IEventBus bus) { bus.addListener(YAIPackets::register); }
    private static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("2");
        registrar.playToServer(SlotLockerChangeModePacket.TYPE, SlotLockerChangeModePacket.STREAM_CODEC,
            (packet, context) -> { if (context.player() instanceof ServerPlayer player) packet.apply(player); });
        registrar.playToServer(SlotLockerDragPacket.TYPE, SlotLockerDragPacket.STREAM_CODEC,
            (packet, context) -> { if (context.player() instanceof ServerPlayer player) packet.apply(player); });
    }
}
