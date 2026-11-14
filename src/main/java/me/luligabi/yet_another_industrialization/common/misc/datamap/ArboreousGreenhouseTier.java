package me.luligabi.yet_another_industrialization.common.misc.datamap;

import aztech.modern_industrialization.util.MIExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;

/**
 * port-26.1.2: перенос Kotlin {@code common/misc/datamap/ArboreousGreenhouseTier.kt} (апстрим 1.21.1).
 *
 * <p>Значения data-map'а {@code data_maps/block/arboreous_greenhouse_tier.json} (ключ — блок или тег
 * почвы, значение — тир теплицы). Теговые ключи NeoForge разворачивает во все блоки тега, поэтому
 * в {@link #all()} ключи — реальные {@code ResourceKey<Block>} зарегистрированных блоков.</p>
 *
 * <p>Факт API (javap MI 2.6.0-26.1.2-fleet01): {@code MIExtraCodecs.optionalFieldAlwaysWrite(Codec, String, T)}
 * присутствует; {@code Registry.getDataMap} в NeoForge 26.1.2 возвращает {@code Map<ResourceKey<T>, A>}
 * (источник neoforge-26.1.2.126-sources: IRegistryExtension#getDataMap) — как в EI
 * (LargeElectricFurnaceTier.getAll).</p>
 */
public record ArboreousGreenhouseTier(Identifier id, Identifier icon, String translationKey,
                                     Optional<FluidByIdInput> fluid,
                                     Optional<FluidByIdInput> nutrientFluid,
                                     int sortOrder) {

    /** Апстрим: {@code optionalFieldOf("sort_order", lastCustomIndex)} на момент создания кодека (100). */
    private static final int DEFAULT_SORT_ORDER = 100;

    public static final Codec<ArboreousGreenhouseTier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(ArboreousGreenhouseTier::id),
                    Identifier.CODEC.fieldOf("icon").forGetter(ArboreousGreenhouseTier::icon),
                    Codec.STRING.fieldOf("translation_key").forGetter(ArboreousGreenhouseTier::translationKey),
                    FluidByIdInput.CODEC.optionalFieldOf("fluid").forGetter(ArboreousGreenhouseTier::fluid),
                    FluidByIdInput.CODEC.optionalFieldOf("nutrient_fluid").forGetter(ArboreousGreenhouseTier::nutrientFluid),
                    Codec.INT.optionalFieldOf("sort_order", DEFAULT_SORT_ORDER).forGetter(ArboreousGreenhouseTier::sortOrder)
            ).apply(instance, ArboreousGreenhouseTier::new));

    /** Все записи data-map'а: ключ — ResourceKey блока (теги уже развёрнуты NeoForge). */
    public static Map<net.minecraft.resources.ResourceKey<Block>, ArboreousGreenhouseTier> all() {
        return BuiltInRegistries.BLOCK.getDataMap(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER);
    }

    /** Первый тир с указанным {@code id} (апстрим: {@code all().values.find { it.id == id }}). */
    public static ArboreousGreenhouseTier get(Identifier tierId) {
        for (ArboreousGreenhouseTier tier : all().values()) {
            if (tier.id().equals(tierId)) {
                return tier;
            }
        }
        return null;
    }

    /**
     * Поток/жидкость-ингредиент тира.
     * {@code addToRecipeInput} апстрима (datagen-хелпер, MIMachineRecipeBuilder) в порт не переносился —
     * datagen YAI ещё не портирован (см. отчёт).
     */
    public record FluidByIdInput(Identifier fluid, int amount, float probability) {

        public static final Codec<FluidByIdInput> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("fluid").forGetter(FluidByIdInput::fluid),
                        MIExtraCodecs.optionalFieldAlwaysWrite(Codec.intRange(0, Integer.MAX_VALUE), "amount", 1)
                                .forGetter(FluidByIdInput::amount),
                        MIExtraCodecs.FLOAT_01.optionalFieldOf("probability", 1f).forGetter(FluidByIdInput::probability)
                ).apply(instance, FluidByIdInput::new));
    }
}
