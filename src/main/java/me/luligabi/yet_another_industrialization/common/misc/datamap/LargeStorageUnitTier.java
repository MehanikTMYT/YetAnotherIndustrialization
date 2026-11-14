package me.luligabi.yet_another_industrialization.common.misc.datamap;

import aztech.modern_industrialization.MIBlockKeys;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.util.MIExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import java.util.Map;

public record LargeStorageUnitTier(long capacity, String cableTier, String translationKey) {
    private static final Codec<String> CABLE_TIER_CODEC = Codec.STRING.flatXmap(name ->
            parseTier(name) != null ? DataResult.success(name) : DataResult.error(() -> "Unknown cable tier: " + name), DataResult::success);
    public static final Codec<LargeStorageUnitTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MIExtraCodecs.POSITIVE_LONG.fieldOf("capacity").forGetter(LargeStorageUnitTier::capacity),
            CABLE_TIER_CODEC.fieldOf("cable_tier").forGetter(LargeStorageUnitTier::cableTier),
            Codec.STRING.fieldOf("translation_key").forGetter(LargeStorageUnitTier::translationKey)
    ).apply(instance, LargeStorageUnitTier::new));
    public static Map<ResourceKey<Block>, LargeStorageUnitTier> all() {
        return BuiltInRegistries.BLOCK.getDataMap(YAIDataMaps.LARGE_STORAGE_UNIT_TIER);
    }
    public static LargeStorageUnitTier getFor(Block block) {
        return block.builtInRegistryHolder().getData(YAIDataMaps.LARGE_STORAGE_UNIT_TIER);
    }
    private static CableTier parseTier(String name) {
        if (name.equals("*")) return CableTier.allTiers().stream().max(java.util.Comparator.comparingLong(tier -> tier.eu)).orElse(null);
        return CableTier.allTiers().stream().filter(tier -> tier.name.equals(name)).findFirst().orElse(null);
    }
    public static Identifier getHull(Identifier block, CableTier tier) {
        if (tier.name.equals("lv")) return MIBlockKeys.BASIC_MACHINE_HULL.identifier();
        return tier.itemKey == null ? block : tier.itemKey;
    }
    public LargeStorageUnitBlockEntity.Tier toRegisteredTier(ResourceKey<Block> key) {
        CableTier tier = parseTier(cableTier);
        if (tier == null) throw new IllegalStateException("Unknown cable tier: " + cableTier);
        return new LargeStorageUnitBlockEntity.Tier(key.identifier(), capacity, tier, translationKey);
    }
}
