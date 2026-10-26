package me.luligabi.yet_another_industrialization.common.misc.effect;
import static me.luligabi.yet_another_industrialization.common.YAIIds.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class YAIEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ID);
    public static final DeferredHolder<MobEffect, CreativeFlightMobEffect> CREATIVE_FLIGHT = EFFECTS.register("creative_flight", CreativeFlightMobEffect::new);
    public static void init(IEventBus bus) { EFFECTS.register(bus); }
}
