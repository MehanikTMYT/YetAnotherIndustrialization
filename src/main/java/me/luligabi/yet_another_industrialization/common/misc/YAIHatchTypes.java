package me.luligabi.yet_another_industrialization.common.misc;

import net.minecraft.network.chat.Component;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;

public class YAIHatchTypes {

    public static final HatchType MIXED_INPUT = HatchTypes.register(YAIIds.id("mixed_input"), Component.literal("Mixed Input Hatch"));
    public static final HatchType MIXED_OUTPUT = HatchTypes.register(YAIIds.id("mixed_output"), Component.literal("Mixed Output Hatch"));

    public static final HatchType LARGE_STORAGE_UNIT_INPUT = HatchTypes.register(YAIIds.id("large_storage_unit_input"), Component.literal("Large Storage Unit Input Hatch"));
    public static final HatchType LARGE_STORAGE_UNIT_OUTPUT = HatchTypes.register(YAIIds.id("large_storage_unit_output"), Component.literal("Large Storage Unit Output Hatch"));

    private YAIHatchTypes() {}
}
