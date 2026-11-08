package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import me.luligabi.yet_another_industrialization.common.item.tools.IndustrialistsGogglesItem;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import me.luligabi.yet_another_industrialization.common.misc.proxy.YAIModSlotProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = MultiblockMachineBER.class, remap = false)
public abstract class MultiblockMachineBERMixin {

    @Unique
    private static Boolean yet_another_industrialization$wrenchCache = null;

    @Unique
    private static int yet_another_industrialization$wrenchCacheCooldown = 0;

    @Inject(method = "isHoldingWrench", at = @At("TAIL"), cancellable = true)
    private static void yet_another_industrialization$isHoldingWrench(CallbackInfoReturnable<Boolean> cir) {
        if (yet_another_industrialization$wrenchCache != null) {
            if (--yet_another_industrialization$wrenchCacheCooldown > 0) {
                cir.setReturnValue(yet_another_industrialization$wrenchCache);
                return;
            }
            yet_another_industrialization$wrenchCache = null;
        }

        boolean original = cir.getReturnValueZ();
        boolean result = yet_another_industrialization$getResult(original);

        yet_another_industrialization$wrenchCache = result;
        yet_another_industrialization$wrenchCacheCooldown = 25;

        cir.setReturnValue(result);
    }

    @Unique
    private static boolean yet_another_industrialization$getResult(boolean original) {
        final List<ItemStack> possibleGoggles = YAIModSlotProxy.getHeadItems(
                Minecraft.getInstance().player,
                stack -> stack.getItem() instanceof IndustrialistsGogglesItem
        );
        if (possibleGoggles.isEmpty()) return original;

        return possibleGoggles.stream().anyMatch(stack -> stack.getOrDefault(YAIDataComponents.GOGGLES_ENABLED.get(), false));
    }

}