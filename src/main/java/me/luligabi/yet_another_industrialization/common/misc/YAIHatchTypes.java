package me.luligabi.yet_another_industrialization.common.misc;

import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

public class YAIHatchTypes {


    public static final HatchType MIXED_INPUT = HatchTypes.register(YAIIds.id("mixed_input"), YAI.TEXT.lang().mixedInputHatch());
    public static final HatchType MIXED_OUTPUT = HatchTypes.register(YAIIds.id("mixed_output"), YAI.TEXT.lang().mixedOutputHatch());

    private YAIHatchTypes() {}
}
