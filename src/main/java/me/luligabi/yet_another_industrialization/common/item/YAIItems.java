package me.luligabi.yet_another_industrialization.common.item;

import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import me.luligabi.yet_another_industrialization.common.item.food.*;
import me.luligabi.yet_another_industrialization.common.item.misc.*;
import me.luligabi.yet_another_industrialization.common.item.tools.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import me.luligabi.yet_another_industrialization.common.item.tools.MachineDiagnoserItem;
import me.luligabi.yet_another_industrialization.common.item.tools.MachineRemoverItem;

/** Direct registrations for converted items. */
public final class YAIItems {
    public static final DeferredRegister.Items ITEMS = new DeferredRegister.Items(YAIIds.ID) {
        @Override
        public <I extends Item> DeferredItem<I> registerItem(String name,
                java.util.function.Function<Item.Properties, ? extends I> factory,
                java.util.function.Supplier<Item.Properties> properties) {
            return super.registerItem(name, factory, () -> {
                var result = properties.get();
                // MI-hook machines also use this register, bypassing registerSimpleBlockItem.
                if (YAIBlocks.BLOCKS.getEntries().stream().anyMatch(block -> block.getId().getPath().equals(name))) {
                    result.useBlockDescriptionPrefix();
                }
                return result;
            });
        }
    };

    // --- converted items ---
    public static final DeferredItem<MachineDiagnoserItem> MACHINE_DIAGNOSER = ITEMS.registerItem("machine_diagnoser", MachineDiagnoserItem::new);
    public static final DeferredItem<MachineRemoverItem> MACHINE_REMOVER = ITEMS.registerItem("machine_remover", MachineRemoverItem::new);
    public static final DeferredItem<YAIGuidebookItem> GUIDEBOOK = ITEMS.registerItem("guidebook", YAIGuidebookItem::new);
    public static final DeferredItem<IndustrialistsGogglesItem> INDUSTRIALISTS_GOGGLES =
        ITEMS.registerItem("industrialists_goggles", IndustrialistsGogglesItem::new);
    public static final DeferredItem<Item> DRAGON_EGG_SIPHON_CATALYST = ITEMS.registerSimpleItem("dragon_egg_siphon_catalyst");
    public static final DeferredItem<Item> TEMPPROOF_AIR_INTAKE =
        ITEMS.registerSimpleItem("tempproof_air_intake", properties -> properties.stacksTo(1));
    public static final DeferredItem<DemonCoreItem> DEMON_CORE = ITEMS.registerItem("demon_core", DemonCoreItem::new);
    public static final DeferredItem<CachacaItem> CACHACA = ITEMS.registerItem("cachaca", CachacaItem::new);
    public static final DeferredItem<AISlopItem> AI_SLOP = ITEMS.registerItem("ai_slop", AISlopItem::new);
    public static final DeferredItem<UltradenseMetalBallBurgerItem> ULTRADENSE_METAL_BALL_BURGER =
        ITEMS.registerItem("ultradense_metal_ball_burger", UltradenseMetalBallBurgerItem::new);
    // port-26.1.2: предмет-материал beryllium_large_plate — в апстриме существовал только
    // через ассеты/рецепты (кода-создателя не было), регистрируем как простой предмет
    public static final DeferredItem<Item> BERYLLIUM_LARGE_PLATE = ITEMS.registerSimpleItem("beryllium_large_plate");

    public static final DeferredItem<Item> ENERGY_ZAP = ITEMS.registerSimpleItem("energy_zap");
    public static final DeferredItem<StorageSlotLockerItem> STORAGE_SLOT_LOCKER = ITEMS.registerItem("storage_slot_locker", StorageSlotLockerItem::new);

    // --- block items (стабы // port-26.1.2: базовый BlockItem, без кастомного поведения) ---
    public static final DeferredItem<BlockItem> CADMIUM_BLOCK =
        ITEMS.registerSimpleBlockItem("cadmium_block", YAIBlocks.CADMIUM_BLOCK, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> DEEP_SOIL =
        ITEMS.registerSimpleBlockItem("deepsoil", YAIBlocks.DEEP_SOIL, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> ECHO_SOIL =
        ITEMS.registerSimpleBlockItem("echo_soil", YAIBlocks.ECHO_SOIL, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> GRASS_BLOCK =
        ITEMS.registerSimpleBlockItem("grass_block", YAIBlocks.GRASS_BLOCK, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> MYCELIUM =
        ITEMS.registerSimpleBlockItem("mycelium", YAIBlocks.MYCELIUM, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> NETHERRACK =
        ITEMS.registerSimpleBlockItem("netherrack", YAIBlocks.NETHERRACK, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> SAND =
        ITEMS.registerSimpleBlockItem("sand", YAIBlocks.SAND, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> END_STONE =
        ITEMS.registerSimpleBlockItem("end_stone", YAIBlocks.END_STONE, properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> SINGULARITY_BLOCK =
        ITEMS.registerSimpleBlockItem("singularity_block", YAIBlocks.SINGULARITY_BLOCK, properties -> properties.useBlockDescriptionPrefix());

    // --- machine block items ---
    // port-26.1.2: предметы машин и хатчей регистрирует tesseract MI-hook (YAIHookRegistry
    // отдаёт ITEMS в hook-билдер) — стабы удалены, чтобы не дублировать id предмета.
    public static final DeferredItem<BlockItem> BATTERY_CASING =
        ITEMS.registerSimpleBlockItem("battery_casing", YAIBlocks.BATTERY_CASING, properties -> properties.useBlockDescriptionPrefix());

    // --- hatch block items ---
    public static final DeferredItem<BlockItem> STEEL_PLATED_END_STONE_BRICKS =
        ITEMS.registerSimpleBlockItem("steel_plated_end_stone_bricks", YAIBlocks.STEEL_PLATED_END_STONE_BRICKS, properties -> properties.useBlockDescriptionPrefix());

    private YAIItems() {}
    public static void init(IEventBus bus) { ITEMS.register(bus); }
}
