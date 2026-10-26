package me.luligabi.yet_another_industrialization.common.item.food;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;


public final class AISlopItem extends Item {
    public AISlopItem(Properties properties) { super(properties.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.4f).build())); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("text.yet_another_industrialization.ai_slop_tooltip")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
