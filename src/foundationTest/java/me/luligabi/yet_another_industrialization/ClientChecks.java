package me.luligabi.yet_another_industrialization;
import me.luligabi.yet_another_industrialization.client.SlotLockerClient;
import me.luligabi.yet_another_industrialization.client.renderer.item.StorageSlotLockerComponent;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.item.tools.StorageSlotLockerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
@Mod(value="yai_foundation_qa", dist=Dist.CLIENT)
public final class ClientChecks {
    private int ticks;
    public ClientChecks(IEventBus bus) {
        SlotLockerClient.init(bus);
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
            var mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            ticks++;
            if (ticks == 20) {
                var uuid = mc.player.getUUID();
                mc.getSingleplayerServer().execute(() -> {
                    var player = mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);
                    player.getInventory().setItem(0, YAIItems.STORAGE_SLOT_LOCKER.toStack());
                    player.inventoryMenu.broadcastChanges();
                });
                return;
            }
            if (ticks == 40) {
                net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(
                    new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerChangeModePacket(0));
                return;
            }
            if (ticks == 60) {
                int index = -1;
                for (int i = 0; i < mc.player.containerMenu.slots.size(); i++) {
                    var slot = mc.player.containerMenu.getSlot(i);
                    if (slot.container == mc.player.getInventory() && slot.getContainerSlot() == 0) index = i;
                }
                net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(
                    new me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket(
                        mc.player.containerMenu.containerId, index,
                        net.neoforged.neoforge.transfer.item.ItemResource.of(net.minecraft.world.item.Items.DIAMOND)));
                return;
            }
            if (ticks != 100) return;
            try {
                int checks = 0;
                var synchronizedData = mc.player.getInventory().getItem(0).get(
                    me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents.SLOT_LOCKER_DATA.get());
                if (synchronizedData == null || synchronizedData.mode() !=
                        me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData.Mode.LOCK_FLUID
                        || !synchronizedData.itemVariant().is(net.minecraft.world.item.Items.DIAMOND))
                    throw new AssertionError("Client/server slot locker synchronization");
                checks++;
                for (String name : new String[]{"guidebook","industrialists_goggles","dragon_egg_siphon_catalyst",
                        "tempproof_air_intake","demon_core","cachaca","ai_slop","ultradense_metal_ball_burger","energy_zap","storage_slot_locker"}) {
                    if (mc.getResourceManager().getResource(YAIIds.id("items/" + name + ".json")).isEmpty())
                        throw new AssertionError("Missing item definition: " + name);
                    checks++;
                    if (mc.getResourceManager().getResource(YAIIds.id("models/item/" + name + ".json")).isEmpty())
                        throw new AssertionError("Missing item model: " + name);
                    checks++;
                }
                var data = YAIItems.STORAGE_SLOT_LOCKER.get().getTooltipImage(YAIItems.STORAGE_SLOT_LOCKER.toStack()).orElseThrow();
                var renderer = ClientTooltipComponent.create(data);
                if (!(renderer instanceof StorageSlotLockerComponent) || renderer.getWidth(mc.font) != 40 || renderer.getHeight(mc.font) != 22)
                    throw new AssertionError("Tooltip factory/geometry");
                checks++;
                if (StorageSlotLockerComponent.textureY(true, true) != 0 ||
                    StorageSlotLockerComponent.textureY(false, true) != 18 ||
                    StorageSlotLockerComponent.textureY(false, false) != 36 ||
                    StorageSlotLockerComponent.textureY(false, null) != 54) throw new AssertionError("Atlas mode mapping");
                checks++;

                var graphics = new net.minecraft.client.gui.GuiGraphicsExtractor(mc,
                    new net.minecraft.client.renderer.state.gui.GuiRenderState(), 0, 0);
                for (var mode : me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData.Mode.values()) {
                    var populated = new StorageSlotLockerItem.TooltipData(
                        net.neoforged.neoforge.transfer.item.ItemResource.of(net.minecraft.world.item.Items.DIAMOND),
                        net.neoforged.neoforge.transfer.fluid.FluidResource.of(net.minecraft.world.level.material.Fluids.WATER), mode);
                    new StorageSlotLockerComponent(populated).extractImage(mc.font, 0, 0, 40, 22, graphics);
                    checks++;
                }
                System.out.println("YAI_CLIENT_PASS checks=" + checks);
            } catch (Throwable failure) { System.out.println("YAI_CLIENT_FAIL"); failure.printStackTrace(); }
            finally { mc.stop(); }
        });
    }
}
