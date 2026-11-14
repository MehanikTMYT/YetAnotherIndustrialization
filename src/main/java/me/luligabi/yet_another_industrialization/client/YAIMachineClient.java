package me.luligabi.yet_another_industrialization.client;

import aztech.modern_industrialization.client.machines.GuiComponentsClient;
import me.luligabi.yet_another_industrialization.client.component.ChargingSlotClient;
import me.luligabi.yet_another_industrialization.client.component.LargeStorageUnitGuiClient;
import me.luligabi.yet_another_industrialization.client.component.ToggleCheckboxClient;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.ChargingSlot;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitGui;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox;
import me.luligabi.yet_another_industrialization.client.renderer.FlightPylonBER;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class YAIMachineClient {
    private YAIMachineClient() {}
    public static void init(IEventBus bus) {
        bus.addListener(EntityRenderersEvent.RegisterRenderers.class, YAIMachineClient::registerRenderers);
        bus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(() -> {
            GuiComponentsClient.register(ToggleCheckbox.TYPE, ToggleCheckboxClient::new);
            GuiComponentsClient.register(ChargingSlot.TYPE, ChargingSlotClient::new);
            GuiComponentsClient.register(LargeStorageUnitGui.TYPE, LargeStorageUnitGuiClient::new);
        }));
    }
    @SuppressWarnings("unchecked")
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // The hook owns the deferred type; resolve it after registries have frozen.
        BlockEntityType<MultiblockMachineBlockEntity> pylonType =
                (BlockEntityType<MultiblockMachineBlockEntity>) (BlockEntityType<?>)
                        BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(YAIIds.id(FlightPylonBlockEntity.ID));
        event.registerBlockEntityRenderer(pylonType, FlightPylonBER::new);
    }
}
