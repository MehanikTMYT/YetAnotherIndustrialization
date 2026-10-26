package me.luligabi.yet_another_industrialization.common.item.food;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public final class UltradenseMetalBallBurgerItem extends Item {
    public UltradenseMetalBallBurgerItem(Properties properties) { super(properties.food(new FoodProperties.Builder().nutrition(5).saturationModifier(1f).build(),
            Consumable.builder().onConsume(new ApplyStatusEffectsConsumeEffect(
                new MobEffectInstance(MobEffects.SLOWNESS, 160, 2), 0.1f)).build())); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("text.yet_another_industrialization.ultradense_metal_ball_burger_tooltip")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
