package me.luligabi.yet_another_industrialization.common.misc.datamap;

import aztech.modern_industrialization.util.MIExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import java.util.Map;
import net.minecraft.resources.ResourceKey;

public record NumismaticGeneratorCurrency(long euPerItem) {
    public static final Codec<NumismaticGeneratorCurrency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MIExtraCodecs.POSITIVE_LONG.fieldOf("eu_per_item").forGetter(NumismaticGeneratorCurrency::euPerItem)
    ).apply(instance, NumismaticGeneratorCurrency::new));
    public static Map<ResourceKey<Item>, NumismaticGeneratorCurrency> all() {
        return BuiltInRegistries.ITEM.getDataMap(YAIDataMaps.NUMISMATIC_GENERATOR_CURRENCY);
    }
    public static long getEu(Item item) {
        NumismaticGeneratorCurrency currency = item.builtInRegistryHolder().getData(YAIDataMaps.NUMISMATIC_GENERATOR_CURRENCY);
        return currency == null ? 0 : currency.euPerItem;
    }
}
