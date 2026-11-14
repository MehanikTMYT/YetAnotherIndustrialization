package me.luligabi.yet_another_industrialization.common.misc.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import me.luligabi.yet_another_industrialization.mixin.InventoryMixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// синк/порт 26.1.2: убрана зависимость от tesseract Proxy/Proxies (Curios/Accessories не портируются,
// слоты только ванильные). Оставлен vanilla-фоллбэк.
public class YAIModSlotProxy {

    public static List<ItemStack> getAllItems(Player player, Predicate<ItemStack> filter) {
        var inventory = player.getInventory();
        InventoryMixin inv = (InventoryMixin) (Object) inventory;
        var items = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.yai$getArmor().size(); i++) items.add(inv.yai$getArmor().get(i));
        for (int i = 0; i < inv.yai$getItems().size(); i++) items.add(inv.yai$getItems().get(i));
        items.addAll(inv.yai$getOffhand());
        return items;
    }

    public boolean isLoaded() {
        return false;
    }

    public List<ItemStack> getContents(Player player, Predicate<ItemStack> filter) {
        return List.of();
    }

    public boolean hasContents(Player player, Predicate<ItemStack> filter) {
        return false;
    }

    public static List<ItemStack> getAllItems(Player player) {
        var inventory = player.getInventory();
        InventoryMixin inv = (InventoryMixin) (Object) inventory;
        var items = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.yai$getArmor().size(); i++) items.add(inv.yai$getArmor().get(i));
        for (int i = 0; i < inv.yai$getItems().size(); i++) items.add(inv.yai$getItems().get(i));
        items.addAll(inv.yai$getOffhand());
        return items;
    }

    public static List<ItemStack> getHeadItems(Player player, Predicate<ItemStack> filter) {
        var inventory = player.getInventory();
        InventoryMixin inv = (InventoryMixin) (Object) inventory;
        var items = new ArrayList<ItemStack>();
        var armorItem = inv.yai$getArmor().get(3);
        if (filter.test(armorItem)) items.add(armorItem);
        return items;
    }

    public static boolean hasHeadItem(Player player, Predicate<ItemStack> filter) {
        InventoryMixin inv = (InventoryMixin) (Object) player.getInventory();
        return filter.test(inv.yai$getArmor().get(3));
    }
}
