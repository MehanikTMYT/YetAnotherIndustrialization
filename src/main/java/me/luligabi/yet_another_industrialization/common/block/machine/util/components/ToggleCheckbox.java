package me.luligabi.yet_another_industrialization.common.block.machine.util.components;

import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ToggleCheckbox implements GuiComponentServer<ToggleCheckbox.ParamData, Boolean> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ParamData> PARAM_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ParamData::value,
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), ParamData::tooltip,
            ParamData::new);
    public static final Type<ParamData, Boolean> TYPE = new Type<>(
            YAIIds.id("toggle_checkbox"), PARAM_STREAM_CODEC, ByteBufCodecs.BOOL);
    public final Consumer<Boolean> action;
    public final Supplier<Boolean> value;
    public final List<Component> tooltip;

    public ToggleCheckbox(Consumer<Boolean> action, Supplier<Boolean> value, List<Component> tooltip) {
        this.action = action;
        this.value = value;
        this.tooltip = List.copyOf(tooltip);
    }
    @Override public ParamData getParams() { return new ParamData(value.get(), tooltip); }
    @Override public Boolean extractData() { return value.get(); }
    @Override public Type<ParamData, Boolean> getType() { return TYPE; }

    public record ParamData(boolean value, List<Component> tooltip) {}
}
