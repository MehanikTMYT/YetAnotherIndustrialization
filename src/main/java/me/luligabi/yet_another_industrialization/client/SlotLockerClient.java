package me.luligabi.yet_another_industrialization.client;
import me.luligabi.yet_another_industrialization.client.renderer.item.StorageSlotLockerComponent;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import me.luligabi.yet_another_industrialization.common.item.tools.StorageSlotLockerItem;
import me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerChangeModePacket;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
public final class SlotLockerClient {
    private SlotLockerClient() {}
    public static void init(IEventBus bus) {
        bus.addListener(SlotLockerClient::registerTooltips);
        NeoForge.EVENT_BUS.addListener(SlotLockerClient::scroll);
    }
    private static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(StorageSlotLockerItem.TooltipData.class, StorageSlotLockerComponent::new);
    }
    private static void scroll(InputEvent.MouseScrollingEvent event) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player == null || minecraft.screen != null || !player.isShiftKeyDown()
                || !player.getMainHandItem().is(YAIItems.STORAGE_SLOT_LOCKER.get())) return;
        ClientPacketDistributor.sendToServer(new SlotLockerChangeModePacket(player.getInventory().getSelectedSlot()));
        event.setCanceled(true);
    }
}
