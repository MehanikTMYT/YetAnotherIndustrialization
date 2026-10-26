package me.luligabi.yet_another_industrialization.common.item.misc;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
/** Preserve the upstream prohibition on enchanting, including books at an anvil. */
public final class DemonCoreItem extends Item {
    public DemonCoreItem(Properties properties) { super(properties.durability(320)); }
    @Override public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) { return false; }
    @Override public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) { return false; }
}
