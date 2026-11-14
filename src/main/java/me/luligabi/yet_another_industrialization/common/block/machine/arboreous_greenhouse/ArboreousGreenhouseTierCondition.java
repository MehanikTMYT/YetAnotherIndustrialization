package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

/**
 * port-26.1.2: перенос Kotlin {@code arboreous_greenhouse/ArboreousGreenhouseTierCondition.kt}.
 *
 * <p><b>Восстановлено</b> (бриф, этап2): проверка
 * {@code TIERS[activeShape].id == tierId} — теперь, когда BE теплицы перенесён,
 * условие снова решает, можно ли выполнять рецепт на текущем тире почвы.</p>
 *
 * <p>Факт API26: {@code MachineProcessCondition#canProcessRecipe(Context, MachineRecipe)},
 * {@code Context#getBlockEntity()}; icon — {@code BuiltInRegistries.BLOCK/ITEM.getOptional(Identifier)}
 * (в26 возвращает Optional, а не nullable).</p>
 */
public record ArboreousGreenhouseTierCondition(Identifier tierId, Identifier model) implements MachineProcessCondition {

    public static final MapCodec<ArboreousGreenhouseTierCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("tier_id").forGetter(ArboreousGreenhouseTierCondition::tierId),
                    Identifier.CODEC.fieldOf("model").forGetter(ArboreousGreenhouseTierCondition::model)
            ).apply(instance, ArboreousGreenhouseTierCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArboreousGreenhouseTierCondition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC, ArboreousGreenhouseTierCondition::tierId,
                    Identifier.STREAM_CODEC, ArboreousGreenhouseTierCondition::model,
                    ArboreousGreenhouseTierCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        if (!(context.getBlockEntity() instanceof ArboreousGreenhouseBlockEntity greenhouse)) {
            return false;
        }
        // апстрим: TIERS.getOrNull(activeSoil.activeShape)?.id == tierId
        List<ArboreousGreenhouseBlockEntity.Tier> tiers = ArboreousGreenhouseBlockEntity.tiers();
        int activeShape = greenhouse.getActiveSoilIndex();
        if (activeShape < 0 || activeShape >= tiers.size()) {
            return false;
        }
        return tiers.get(activeShape).id().equals(tierId);
    }

    @Override
    public ItemStack icon() {
        // апстрим: icon из data-map'а тира; сначала блок, затем предмет (иначе EMPTY)
        ArboreousGreenhouseTier tier = ArboreousGreenhouseTier.get(tierId);
        if (tier == null) {
            return ItemStack.EMPTY;
        }
        Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(tier.icon());
        if (block.isPresent()) {
            return new ItemStack(block.get());
        }
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(tier.icon());
        if (item.isPresent()) {
            return new ItemStack(item.get());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendDescription(java.util.List<Component> list) {
        // апстрим: YAI.TEXT.arboreousGreenhouseTierTooltip(Component.translatable(tierName));
        // ключ генерации LangManager = text.<modid>.arboreous_greenhouse_tier_tooltip ("Requires %s soil")
        ArboreousGreenhouseTier tier = ArboreousGreenhouseTier.get(tierId);
        if (tier == null) {
            return;
        }
        list.add(Component.translatable(
                "text.yet_another_industrialization.arboreous_greenhouse_tier_tooltip",
                Component.translatable(tier.translationKey())));
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
