package me.luligabi.yet_another_industrialization.common.util;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
public interface YAIDraggable {
    boolean dragItem(ItemStack stack, ItemResource resource, boolean simulation);
    boolean dragFluid(ItemStack stack, FluidResource resource, boolean simulation);
}
