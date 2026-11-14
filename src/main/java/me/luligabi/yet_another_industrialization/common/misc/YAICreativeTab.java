package me.luligabi.yet_another_industrialization.common.misc;

import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;
import java.util.List;

/**
 * port-26.1.2: перенос апстримного YAICreativeTab.kt.
 *
 * Апстрим сортировал по tesseract SortOrder (ItemHolder) — в порте предметы обычные
 * DeferredItem, поэтому сортировка по registry-идентификатору (стабильный порядок).
 * Скрыт только ENERGY_ZAP (апстрим: Order.HIDDEN).
 */
public final class YAICreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, YAI.ID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB =
            CREATIVE_MODE_TABS.register(YAI.ID, () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + YAI.ID + "." + YAI.ID))
                    .icon(() -> new ItemStack(YAIItems.MACHINE_REMOVER.get()))
                    .displayItems((params, output) -> {
                        List<String> hidden = List.of(YAI.ID + ":energy_zap"); // апстрим Order.HIDDEN
                        YAIItems.ITEMS.getEntries().stream()
                                .map(holder -> holder.get())
                                .filter(item -> !hidden.contains(
                                        BuiltInRegistries.ITEM.getKey(item).toString()))
                                .sorted(Comparator.comparing(
                                        item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                                .forEach(output::accept);
                    })
                    .build());

    private YAICreativeTab() {
    }

    public static void init(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
