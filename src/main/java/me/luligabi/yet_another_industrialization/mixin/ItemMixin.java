package me.luligabi.yet_another_industrialization.mixin;

import me.luligabi.yet_another_industrialization.common.misc.YAITags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(
        method = "isBarVisible",
        at = @At("HEAD"),
        cancellable = true
    )
    private void yai$forceBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem().builtInRegistryHolder().is(YAITags.LIFESPAN_DURABILITY_TOOLTIP)) {
            cir.setReturnValue(false);
        }
    }
}