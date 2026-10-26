package me.luligabi.yet_another_industrialization.common.misc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/** Immutable stack component: callers must install replacements with ItemStack.set. */
public record SlotLockerData(ItemResource itemVariant, FluidResource fluidVariant, Mode mode) {
    public static final SlotLockerData EMPTY = new SlotLockerData(ItemResource.EMPTY, FluidResource.EMPTY, Mode.LOCK_ITEM);
    public static final Codec<SlotLockerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemResource.OPTIONAL_CODEC.optionalFieldOf("item", ItemResource.EMPTY).forGetter(SlotLockerData::itemVariant),
        FluidResource.OPTIONAL_CODEC.optionalFieldOf("fluid", FluidResource.EMPTY).forGetter(SlotLockerData::fluidVariant),
        StringRepresentable.fromEnum(Mode::values).optionalFieldOf("mode", Mode.LOCK_ITEM).forGetter(SlotLockerData::mode)
    ).apply(instance, SlotLockerData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SlotLockerData> STREAM_CODEC = StreamCodec.composite(
        ItemResource.STREAM_CODEC, SlotLockerData::itemVariant,
        FluidResource.STREAM_CODEC, SlotLockerData::fluidVariant,
        ByteBufCodecs.idMapper(id -> Mode.values()[id], Mode::ordinal), SlotLockerData::mode,
        SlotLockerData::new);
    public SlotLockerData {
        Objects.requireNonNull(itemVariant);
        Objects.requireNonNull(fluidVariant);
        Objects.requireNonNull(mode);
    }
    public boolean isEmpty() { return equals(EMPTY); }
    public SlotLockerData withItem(ItemResource item) { return new SlotLockerData(item, fluidVariant, mode); }
    public SlotLockerData withFluid(FluidResource fluid) { return new SlotLockerData(itemVariant, fluid, mode); }
    public SlotLockerData withMode(Mode next) { return new SlotLockerData(itemVariant, fluidVariant, next); }

    public enum Mode implements StringRepresentable {
        LOCK_ITEM("lock_item", true, null), LOCK_FLUID("lock_fluid", null, true),
        LOCK_BOTH("lock_both", true, true), UNLOCK_ITEM("unlock_item", false, null),
        UNLOCK_FLUID("unlock_fluid", null, false), UNLOCK_BOTH("unlock_both", false, false);
        private final String id;
        public final Boolean lockItem;
        public final Boolean lockFluid;
        Mode(String id, Boolean lockItem, Boolean lockFluid) {
            this.id = id; this.lockItem = lockItem; this.lockFluid = lockFluid;
        }
        @Override public String getSerializedName() { return id; }
        public MutableComponent text() {
            return Component.translatable("text.yet_another_industrialization.slot_locker_mode_" + id);
        }
        public Mode next() { return values()[(ordinal() + 1) % values().length]; }
    }
}
