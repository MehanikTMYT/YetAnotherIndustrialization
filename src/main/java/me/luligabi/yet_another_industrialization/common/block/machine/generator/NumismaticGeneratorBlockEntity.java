package me.luligabi.yet_another_industrialization.common.block.machine.generator;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.GeneratorMachineBlockEntity;
import aztech.modern_industrialization.machines.components.FluidItemConsumerComponent;
import me.luligabi.yet_another_industrialization.common.misc.datamap.NumismaticGeneratorCurrency;
import net.minecraft.world.item.Item;
import java.util.List;

public final class NumismaticGeneratorBlockEntity extends GeneratorMachineBlockEntity {
    public static final String ID = "numismatic_generator";
    public static final String NAME = "Numismatic Generator";
    private static final FluidItemConsumerComponent.EUProductionMap<Item> PRODUCTION_MAP =
            new FluidItemConsumerComponent.EUProductionMap<>() {
                @Override public long getEuProduction(Item item) { return NumismaticGeneratorCurrency.getEu(item); }
                @Override public FluidItemConsumerComponent.NumberOfFuel getNumberOfFuel() {
                    return FluidItemConsumerComponent.NumberOfFuel.MANY;
                }
                @Override public List<Item> getAllAccepted() { return List.of(); }
            };
    public NumismaticGeneratorBlockEntity(BEP bep) {
        super(bep, ID, true, CableTier.LV, 1_048_576L, 0,
                new FluidItemConsumerComponent(1_048_576, 1.0, PRODUCTION_MAP,
                        FluidItemConsumerComponent.EUProductionMap.empty(), true));
    }
}
