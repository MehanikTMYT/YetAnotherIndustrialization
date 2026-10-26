package me.luligabi.yet_another_industrialization.common.misc;
import static me.luligabi.yet_another_industrialization.common.YAIIds.*;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.biome.Biome;
public final class YAITags {
    public static final TagKey<Item> MI_GUIDE_BOOKS = commonTag(Registries.ITEM, "modern_industrialization/guide_books");
    public static final TagKey<Item> MI_FUEL_RODS_URANIUM = item("fuel_rods/uranium");
    public static final TagKey<Item> MI_FUEL_RODS_LE_URANIUM = item("fuel_rods/le_uranium");
    public static final TagKey<Item> MI_FUEL_RODS_HE_URANIUM = item("fuel_rods/he_uranium");
    public static final TagKey<Item> MI_FUEL_RODS_LE_MOX = item("fuel_rods/le_mox");
    public static final TagKey<Item> MI_FUEL_RODS_HE_MOX = item("fuel_rods/he_mox");
    public static final TagKey<Item> LIFESPAN_DURABILITY_TOOLTIP = item("lifespan_durability_tooltip");
    public static final TagKey<Block> GRASS_SOILS = yaiTag(Registries.BLOCK, "grass_soils");
    public static final TagKey<Block> NETHERRACK_SOILS = yaiTag(Registries.BLOCK, "netherrack_soils");
    public static final TagKey<Block> MYCELLIUMS = commonTag(Registries.BLOCK, "myceliums");
    public static final TagKey<Biome> SCORCHING_LIQUID_AIR_BIOMES = commonTag(Registries.BIOME, "scorching_liquid_air_biomes");
    public static final TagKey<Biome> GELID_LIQUID_AIR_BIOMES = commonTag(Registries.BIOME, "gelid_liquid_air_biomes");
    public static final TagKey<BlockEntityType<?>> MACHINE_REMOVER_BANNED = yaiTag(Registries.BLOCK_ENTITY_TYPE, "machine_remover_banned");
    public static final Map<TagKey<Item>, String> FUEL_ROD_TRANSLATIONS = Map.of(
        MI_FUEL_RODS_URANIUM, "Uranium Fuel Rods", MI_FUEL_RODS_LE_URANIUM, "LE Uranium Fuel Rods",
        MI_FUEL_RODS_HE_URANIUM, "HE Uranium Fuel Rods", MI_FUEL_RODS_LE_MOX, "LE Mox Fuel Rods",
        MI_FUEL_RODS_HE_MOX, "HE Mox Fuel Rods");
    private static TagKey<Item> item(String path) { return yaiTag(Registries.ITEM, path); }
    private static <T> TagKey<T> yaiTag(ResourceKey<? extends Registry<T>> registry, String path) { return TagKey.create(registry, id(path)); }
    private static <T> TagKey<T> commonTag(ResourceKey<? extends Registry<T>> registry, String path) { return TagKey.create(registry, Identifier.fromNamespaceAndPath("c", path)); }
}
