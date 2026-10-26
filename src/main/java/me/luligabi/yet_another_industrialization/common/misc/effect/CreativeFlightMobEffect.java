package me.luligabi.yet_another_industrialization.common.misc.effect;
import static me.luligabi.yet_another_industrialization.common.YAIIds.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;
public final class CreativeFlightMobEffect extends MobEffect {
    public CreativeFlightMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xCF06C2);
        addAttributeModifier(NeoForgeMod.CREATIVE_FLIGHT, id("creative_flight"), 1.0, AttributeModifier.Operation.ADD_VALUE);
    }
}
