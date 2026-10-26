package me.luligabi.yet_another_industrialization.common.item.tools;
import java.util.function.Consumer;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;

public final class IndustrialistsGogglesItem extends Item {
    public IndustrialistsGogglesItem(Properties properties) {
        super(properties.stacksTo(1).equippable(EquipmentSlot.HEAD)
            .component(YAIDataComponents.GOGGLES_ENABLED.get(), true));
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        boolean enabled = stack.getOrDefault(YAIDataComponents.GOGGLES_ENABLED.get(), true);
        tooltip.accept(Component.translatable("text.yet_another_industrialization.enabled_prefix",
            CommonComponents.optionStatus(enabled).plainCopy().withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)));
    }
    /** Null means there are no goggles in the supported vanilla head slot. */
    public static Boolean toggleMode(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(stack.getItem() instanceof IndustrialistsGogglesItem)) return null;
        boolean enabled = !stack.getOrDefault(YAIDataComponents.GOGGLES_ENABLED.get(), true);
        stack.set(YAIDataComponents.GOGGLES_ENABLED.get(), enabled);
        return enabled;
    }
}
