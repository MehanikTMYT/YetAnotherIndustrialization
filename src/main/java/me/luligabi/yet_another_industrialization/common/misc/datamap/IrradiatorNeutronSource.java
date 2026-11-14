package me.luligabi.yet_another_industrialization.common.misc.datamap;

import aztech.modern_industrialization.util.MIExtraCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.luligabi.yet_another_industrialization.common.YAI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import java.util.Map;
import java.util.Optional;

public record IrradiatorNeutronSource(int irradiation, long eu,
        Optional<Either<ResourceKey<Item>, TagKey<Item>>> restrictedTo, Type type,
        float probability, int probabilityCheckCooldown) {
    public static final Codec<Either<ResourceKey<Item>, TagKey<Item>>> RESTRICTED_TO_CODEC =
            NeoForgeExtraCodecs.xor(ResourceKey.codec(Registries.ITEM).fieldOf("item"),
                    TagKey.codec(Registries.ITEM).fieldOf("tag")).codec();
    public static final Codec<IrradiatorNeutronSource> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("irradiation").forGetter(IrradiatorNeutronSource::irradiation),
            MIExtraCodecs.POSITIVE_LONG.fieldOf("eu").forGetter(IrradiatorNeutronSource::eu),
            RESTRICTED_TO_CODEC.optionalFieldOf("restricted_to").forGetter(IrradiatorNeutronSource::restrictedTo),
            Type.CODEC.fieldOf("type").forGetter(IrradiatorNeutronSource::type),
            MIExtraCodecs.FLOAT_01.optionalFieldOf("probability", 0f).forGetter(IrradiatorNeutronSource::probability),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("probability_check_cooldown", 100).forGetter(IrradiatorNeutronSource::probabilityCheckCooldown)
    ).apply(i, IrradiatorNeutronSource::new));
    public static Map<ResourceKey<Item>, IrradiatorNeutronSource> all() {
        return BuiltInRegistries.ITEM.getDataMap(YAIDataMaps.IRRADIATOR_NEUTRON_SOURCE);
    }
    public static IrradiatorNeutronSource getFor(Item item) {
        return item.builtInRegistryHolder().getData(YAIDataMaps.IRRADIATOR_NEUTRON_SOURCE);
    }
    public enum Type implements StringRepresentable {
        NONE("none", false), CONSUMPTION("consumption", true), LIFESPAN("lifespan", true);
        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;
        public final boolean usesItem;
        Type(String name, boolean usesItem) { this.name = name; this.usesItem = usesItem; }
        @Override public String getSerializedName() { return name; }
        public Component component() { return switch (this) {
            case NONE -> YAI.text().irradiatorNeutronSourceTypeNone();
            case CONSUMPTION -> YAI.text().irradiatorNeutronSourceTypeConsumption();
            case LIFESPAN -> YAI.text().irradiatorNeutronSourceTypeLifespan();
        }; }
        public Component description(String chance, String time) { return switch (this) {
            case NONE -> YAI.text().irradiatorNeutronSourceTypeNoneDescription(chance, time);
            case CONSUMPTION -> YAI.text().irradiatorNeutronSourceTypeConsumptionDescription(chance, time);
            case LIFESPAN -> YAI.text().irradiatorNeutronSourceTypeLifespanDescription(chance, time);
        }; }
    }
}
