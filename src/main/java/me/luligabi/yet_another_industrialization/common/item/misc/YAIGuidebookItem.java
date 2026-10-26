package me.luligabi.yet_another_industrialization.common.item.misc;
import guideme.GuidesCommon;
import me.luligabi.yet_another_industrialization.common.compat.guideme.YAIGuide;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
public final class YAIGuidebookItem extends Item {
    public YAIGuidebookItem(Properties properties) { super(properties); }
    @Override public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide()) GuidesCommon.openGuide(user, YAIGuide.ID);
        return InteractionResult.CONSUME;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("text.yet_another_industrialization.guidebook_tooltip")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
