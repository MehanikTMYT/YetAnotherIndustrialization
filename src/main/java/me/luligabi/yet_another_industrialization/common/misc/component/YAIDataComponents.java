package me.luligabi.yet_another_industrialization.common.misc.component;

import com.mojang.serialization.Codec;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class YAIDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, YAIIds.ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SlotLockerData>> SLOT_LOCKER_DATA =
        create("slot_locker_data", SlotLockerData.CODEC, SlotLockerData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GOGGLES_ENABLED =
        create("goggles_enabled", Codec.BOOL, ByteBufCodecs.BOOL);
    private YAIDataComponents() {}
    public static void init(IEventBus bus) { COMPONENTS.register(bus); }
    private static <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> create(
            String name, Codec<D> codec, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
        return COMPONENTS.registerComponentType(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec));
    }
}
