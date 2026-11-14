package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit;

import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import io.netty.buffer.ByteBuf;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public final class LargeStorageUnitGui implements GuiComponentServer<LargeStorageUnitGui.Data, LargeStorageUnitGui.Data> {
    public static final Type<Data, Data> TYPE = new Type<>(YAIIds.id(LargeStorageUnitBlockEntity.ID),
            Data.STREAM_CODEC, Data.STREAM_CODEC);
    private final BooleanSupplier shapeValid;
    private final LongSupplier eu, maxEu, input, output;
    public LargeStorageUnitGui(BooleanSupplier shapeValid, LongSupplier eu, LongSupplier maxEu,
                               LongSupplier input, LongSupplier output) {
        this.shapeValid = shapeValid;
        this.eu = eu;
        this.maxEu = maxEu;
        this.input = input;
        this.output = output;
    }
    @Override public Data getParams() {
        return new Data(shapeValid.getAsBoolean(), eu.getAsLong(), maxEu.getAsLong(), input.getAsLong(), output.getAsLong());
    }
    @Override public Data extractData() { return getParams(); }
    @Override public Type<Data, Data> getType() { return TYPE; }
    public record Data(boolean isShapeValid, long eu, long maxEu, long currentInput, long currentOutput) {
        public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Data::isShapeValid,
                ByteBufCodecs.VAR_LONG, Data::eu,
                ByteBufCodecs.VAR_LONG, Data::maxEu,
                ByteBufCodecs.VAR_LONG, Data::currentInput,
                ByteBufCodecs.VAR_LONG, Data::currentOutput,
                Data::new);
    }
}
