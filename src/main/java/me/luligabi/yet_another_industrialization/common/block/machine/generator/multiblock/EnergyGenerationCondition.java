package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock;

import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import aztech.modern_industrialization.util.MIExtraCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * port-26.1.2: перенос Kotlin EnergyGenerationCondition (апстрим YAIMachines.init).
 * Условие: у мультиблока достаточно незанятой ёмкости энергии для {@code amount} EU.
 */
public record EnergyGenerationCondition(long amount) implements MachineProcessCondition {

    public static final MapCodec<EnergyGenerationCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    MIExtraCodecs.POSITIVE_LONG.fieldOf("amount").forGetter(EnergyGenerationCondition::amount)
            ).apply(instance, EnergyGenerationCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyGenerationCondition> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    EnergyGenerationCondition::amount,
                    EnergyGenerationCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        if (!(context.getBlockEntity() instanceof EnergyListComponentHolder holder)) {
            return false;
        }
        long remainingSpace = 0L;
        for (var component : holder.getEnergyComponents()) {
            remainingSpace += component.getCapacity() - component.getEu();
            if (remainingSpace >= amount) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendDescription(java.util.List<Component> list) {
        list.add(Component.translatable("yet_another_industrialization.tooltip.energy_generation", amount));
    }

    @Override
    public ItemStack icon() {
        return new ItemStack(YAIItems.ENERGY_ZAP.get());
    }

    @Override
    public MapCodec<? extends MachineProcessCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec() {
        return STREAM_CODEC;
    }
}
