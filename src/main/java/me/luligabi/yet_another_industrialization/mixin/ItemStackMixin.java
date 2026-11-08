package me.luligabi.yet_another_industrialization.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.misc.YAITags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Redirect(
            method = "getTooltipLines",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z")
            ),
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0)
    )
    private boolean yai$suppressDurabilityTooltip(List<Component> list, Object component) {
        ItemStack self = (ItemStack)(Object) this;
        if (self.getItem().builtInRegistryHolder().is(YAITags.LIFESPAN_DURABILITY_TOOLTIP)) {
            return false;
        }
        return list.add((Component) component);
    }

    @WrapOperation(
            method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z", ordinal = 0)
    )
    private boolean yai$addLifespanBeforeAdvanced(
            TooltipFlag flag,
            Operation<Boolean> original,
            @Local List<Component> list
    ) {
        ItemStack self = (ItemStack)(Object) this;
        if (self.getItem().builtInRegistryHolder().is(YAITags.LIFESPAN_DURABILITY_TOOLTIP)) {
            list.add(YAI.TEXT.lang().alwaysVisibleLifespan(
                    self.getMaxDamage() - self.getDamageValue(),
                    self.getMaxDamage()
            ));
        }
        return original.call(flag);
    }
}