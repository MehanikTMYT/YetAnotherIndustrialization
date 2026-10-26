package me.luligabi.yet_another_industrialization.common.item;

import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.item.food.*;
import me.luligabi.yet_another_industrialization.common.item.misc.*;
import me.luligabi.yet_another_industrialization.common.item.tools.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Direct registrations for converted items. Diagnoser and remover are still pending. */
public final class YAIItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(YAIIds.ID);
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
    public static final DeferredItem<Item> ENERGY_ZAP = ITEMS.registerSimpleItem("energy_zap");
    public static final DeferredItem<StorageSlotLockerItem> STORAGE_SLOT_LOCKER = ITEMS.registerItem("storage_slot_locker", StorageSlotLockerItem::new);
    private YAIItems() {}
    public static void init(IEventBus bus) { ITEMS.register(bus); }
}
