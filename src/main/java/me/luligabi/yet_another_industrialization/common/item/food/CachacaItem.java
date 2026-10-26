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

public final class CachacaItem extends Item {
    public CachacaItem(Properties properties) { super(properties.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.8f).alwaysEdible().build(),
            Consumable.builder().consumeSeconds(0.8f).animation(ItemUseAnimation.DRINK)
                .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 160, 1), 0.35f)).build())
            .usingConvertsTo(Items.GLASS_BOTTLE).stacksTo(16)); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("text.yet_another_industrialization.cachaca_tooltip")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
