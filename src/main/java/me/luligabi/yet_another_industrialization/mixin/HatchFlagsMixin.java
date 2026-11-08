package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = HatchFlags.class, remap = false)
public abstract class HatchFlagsMixin {

    @Final
    @Shadow
    private Set<HatchType> allowed;

    @Inject(method = "allows", at = @At("HEAD"), cancellable = true)
    public void yet_another_industrialization_allows(HatchType type, CallbackInfoReturnable<Boolean> cir) {
        if (type == YAIHatchTypes.MIXED_INPUT &&
                (allowed.contains(HatchTypes.ITEM_INPUT) || allowed.contains(HatchTypes.FLUID_INPUT))) {
            cir.setReturnValue(true);
        }

        if (type == YAIHatchTypes.MIXED_OUTPUT &&
                (allowed.contains(HatchTypes.ITEM_OUTPUT) || allowed.contains(HatchTypes.FLUID_OUTPUT))) {
            cir.setReturnValue(true);
        }
    }

}