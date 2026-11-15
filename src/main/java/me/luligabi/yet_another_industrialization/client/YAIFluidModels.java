package me.luligabi.yet_another_industrialization.client;

import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;

/**
 * port-26.1.2: NeoForge 26 требует программную регистрацию флюид-моделей
 * (RegisterFluidModelsEvent; JSON-лоадера neoforge:fluid не существует —
 * эталон MI MIClient.registerFluidModels). У флюидов YAI нет собственных
 * текстур — используем vanilla water_still/water_flow.
 */
public final class YAIFluidModels {

    @SubscribeEvent
    private static void registerFluidModels(RegisterFluidModelsEvent event) {
        var still = new Material(Identifier.withDefaultNamespace("block/water_still"));
        var flowing = new Material(Identifier.withDefaultNamespace("block/water_flow"));
        for (var fluid : BuiltInRegistries.FLUID) {
            var id = BuiltInRegistries.FLUID.getKey(fluid);
            if (id != null && YAIIds.ID.equals(id.getNamespace()) && fluid.defaultFluidState().isSource()) {
                event.register(new FluidModel.Unbaked(still, flowing, null, null), fluid);
            }
        }
    }

    private YAIFluidModels() {
    }
}
