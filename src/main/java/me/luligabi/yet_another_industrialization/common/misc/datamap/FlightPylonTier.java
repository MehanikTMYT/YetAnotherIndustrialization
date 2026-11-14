package me.luligabi.yet_another_industrialization.common.misc.datamap;

import aztech.modern_industrialization.util.MIExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import java.util.Map;

public record FlightPylonTier(double range, long eu, String translationKey, int beaconColor) {
    private static final Codec<Integer> HEX_COLOR_CODEC = Codec.STRING.comapFlatMap(hex -> {
        try { return DataResult.success(Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16)); }
        catch (NumberFormatException exception) { return DataResult.error(() -> "Invalid hex code: " + hex); }
    }, color -> "#%06X".formatted(color));
    public static final Codec<FlightPylonTier> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.doubleRange(1, Double.MAX_VALUE).fieldOf("range").forGetter(FlightPylonTier::range),
            MIExtraCodecs.POSITIVE_LONG.fieldOf("eu").forGetter(FlightPylonTier::eu),
            Codec.STRING.fieldOf("translation_key").forGetter(FlightPylonTier::translationKey),
            HEX_COLOR_CODEC.optionalFieldOf("beacon_color", 0xFFFFFF).forGetter(FlightPylonTier::beaconColor)
    ).apply(i, FlightPylonTier::new));
    public static Map<ResourceKey<Block>, FlightPylonTier> all() {
        return BuiltInRegistries.BLOCK.getDataMap(YAIDataMaps.FLIGHT_PYLON_TIER);
    }
    public static FlightPylonTier getFor(Block block) {
        return block.builtInRegistryHolder().getData(YAIDataMaps.FLIGHT_PYLON_TIER);
    }
    public FlightPylonBlockEntity.Tier toRegisteredTier(ResourceKey<Block> key) {
        return new FlightPylonBlockEntity.Tier(key.identifier(), range, eu, translationKey, beaconColor);
    }
}
