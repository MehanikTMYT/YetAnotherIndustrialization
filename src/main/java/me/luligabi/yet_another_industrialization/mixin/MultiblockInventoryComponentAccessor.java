package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(value = MultiblockInventoryComponent.class, remap = false)
public interface MultiblockInventoryComponentAccessor {

    @Invoker("rebuildList")
    <T, Stack extends aztech.modern_industrialization.inventory.AbstractConfigurableStack<T, ? extends net.neoforged.neoforge.transfer.resource.DataComponentHolderResource<T>>> void invokeRebuildList(
            List<HatchBlockEntity> sortedHatches, List<Stack> stacks, BiConsumer<HatchBlockEntity, List<Stack>> appender);

}
