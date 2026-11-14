package me.luligabi.yet_another_industrialization.common.block;

import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Solid blocks and deferred registries used by the YAI machine hook. */
public final class YAIBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(YAIIds.ID);

    // port-26.1.2: реестр BlockEntityType для машин — отдаётся в tesseract MIHookRegistry
    // (blockEntityRegistry), машины регистрирует hook-билдер (HackedMachineRegistrationHelper).
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, YAIIds.ID);

    // --- simple solid blocks ---
    public static final DeferredBlock<Block> CADMIUM_BLOCK = register("cadmium_block", BlockBehaviour.Properties.of().strength(5.0f, 6.0f));
    public static final DeferredBlock<Block> DEEP_SOIL = register("deepsoil", BlockBehaviour.Properties.of().strength(0.6f));
    public static final DeferredBlock<Block> ECHO_SOIL = register("echo_soil", BlockBehaviour.Properties.of().strength(0.6f));
    public static final DeferredBlock<Block> GRASS_BLOCK = register("grass_block", BlockBehaviour.Properties.of().strength(0.6f));
    public static final DeferredBlock<Block> MYCELIUM = register("mycelium", BlockBehaviour.Properties.of().strength(0.6f));
    public static final DeferredBlock<Block> NETHERRACK = register("netherrack", BlockBehaviour.Properties.of().strength(0.4f, 0.7f));
    public static final DeferredBlock<Block> SAND = register("sand", BlockBehaviour.Properties.of().strength(0.6f));
    public static final DeferredBlock<Block> END_STONE = register("end_stone", BlockBehaviour.Properties.of().strength(3.0f, 3.0f));
    public static final DeferredBlock<Block> SINGULARITY_BLOCK = register("singularity_block", BlockBehaviour.Properties.of().strength(50.0f, 2000.0f));

    // --- machine blocks ---
    // port-26.1.2: машины Arboreous Greenhouse / Cryogenic Precipitator / Dragon Egg Energy Siphon /
    // Pulse Detonation Generator / Detonation Chamber Casing регистрируются tesseract MI-hook'ом
    // (YAIHookListener → YAIMachines) через этот же BLOCKS — стабы-заглушки удалены, чтобы не было
    // дубликата ключа в одном DeferredRegister. Все машины и хатчи теперь регистрирует hook.
    public static final DeferredBlock<Block> BATTERY_CASING = register("battery_casing", BlockBehaviour.Properties.of().strength(3.0f, 3.0f));

    // --- hatch blocks (mixed/steel/bronze/highly_advanced/turbo) ---
    public static final DeferredBlock<Block> STEEL_PLATED_END_STONE_BRICKS = register("steel_plated_end_stone_bricks", BlockBehaviour.Properties.of().strength(4.5f, 4.5f));

    private static DeferredBlock<Block> register(String name, BlockBehaviour.Properties blockProperties) {
        return BLOCKS.registerBlock(name, properties -> new Block(properties));
    }

    private YAIBlocks() {}

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus); // port-26.1.2: BE-реестр машин (заполняет tesseract MI-hook)
    }
}
