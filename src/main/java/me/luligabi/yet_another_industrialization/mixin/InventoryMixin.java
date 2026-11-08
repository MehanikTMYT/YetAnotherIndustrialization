package me.luligabi.yet_another_industrialization.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Inventory.class)
public interface InventoryMixin {
    @Accessor("items")
    NonNullList<ItemStack> yai$getItems();

    @Accessor("armor")
    NonNullList<ItemStack> yai$getArmor();

    @Accessor("offhand")
    NonNullList<ItemStack> yai$getOffhand();
}
