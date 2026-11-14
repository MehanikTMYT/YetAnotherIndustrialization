package me.luligabi.yet_another_industrialization.common.misc.datamap;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Set;

/**
 * port-26.1.2: перенос Kotlin {@code common/misc/datamap/YAIDataMaps.kt}.
 * Эталон API26 — EI {@code EIDataMaps} (compile0, smoke errors=0): тот же
 * {@code DataMapType.builder(...)} + {@code synced(codec, true)} + {@code RegisterDataMapTypesEvent}.
 *
 * Все data-map’ы оригинала синхронизируются клиенту вместе с codec.
 */
public final class YAIDataMaps {

    private static final Set<DataMapType<?, ?>> DATA_MAPS = Sets.newHashSet();

    public static final DataMapType<Block, ArboreousGreenhouseTier> ARBOREOUS_GREENHOUSE_TIER =
            create("arboreous_greenhouse_tier", Registries.BLOCK, ArboreousGreenhouseTier.CODEC, true);
    public static final DataMapType<Block, FlightPylonTier> FLIGHT_PYLON_TIER =
            create("flight_pylon_tier", Registries.BLOCK, FlightPylonTier.CODEC, true);
    public static final DataMapType<Block, LargeStorageUnitTier> LARGE_STORAGE_UNIT_TIER =
            create("large_storage_unit_tier", Registries.BLOCK, LargeStorageUnitTier.CODEC, true);
    public static final DataMapType<Item, IrradiatorNeutronSource> IRRADIATOR_NEUTRON_SOURCE =
            create("irradiator_neutron_source", Registries.ITEM, IrradiatorNeutronSource.CODEC, true);
    public static final DataMapType<Item, NumismaticGeneratorCurrency> NUMISMATIC_GENERATOR_CURRENCY =
            create("numismatic_generator_currency", Registries.ITEM, NumismaticGeneratorCurrency.CODEC, true);

    private static <R, T> DataMapType<R, T> create(String name, ResourceKey<Registry<R>> registry,
                                                   Codec<T> codec, boolean sync) {
        // порядок generic у DataMapType.builder: <T, R> Builder<T, R>; build() -> DataMapType<R, T>
        var builder = DataMapType.builder(YAIIds.id(name), registry, codec);
        if (sync) {
            builder = builder.synced(codec, true);
        }
        DataMapType<R, T> type = builder.build();
        DATA_MAPS.add(type);
        return type;
    }

    public static void init(RegisterDataMapTypesEvent event) {
        DATA_MAPS.forEach(event::register);
    }

    private YAIDataMaps() {
    }
}
