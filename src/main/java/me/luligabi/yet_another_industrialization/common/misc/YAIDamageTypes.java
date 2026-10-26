package me.luligabi.yet_another_industrialization.common.misc;
import static me.luligabi.yet_another_industrialization.common.YAIIds.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
public final class YAIDamageTypes {
    public static final ResourceKey<DamageType> PULSE_DETONATION_GENERATOR =
        ResourceKey.create(Registries.DAMAGE_TYPE, id("pulse_detonation_generator"));
    public static DamageSource pdg(Level level) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(PULSE_DETONATION_GENERATOR));
    }
}
