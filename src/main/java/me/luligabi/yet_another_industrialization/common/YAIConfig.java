package me.luligabi.yet_another_industrialization.common;

import net.swedz.tesseract.config.annotation.ConfigComment;
import net.swedz.tesseract.config.annotation.ConfigKey;
import net.swedz.tesseract.config.annotation.Range;
import net.swedz.tesseract.config.annotation.SubSection;

public interface YAIConfig {

    @ConfigKey("machine_diagnoser")
    @SubSection
    MachineDiagnoser machineDiagnoser();

    interface MachineDiagnoser {

        @ConfigKey("cooldown_ticks")
        @ConfigComment("Ticks of cooldown after using a Machine Diagnoser. Default: 0 (disabled)")
        @Range.Integer(min = 0, max = Integer.MAX_VALUE)
        default int cooldownTicks() { return 0; }

    }

    @ConfigKey("machine_remover")
    @SubSection
    MachineRemover machineRemover();

    interface MachineRemover {

        @ConfigKey("cooldown_ticks")
        @ConfigComment("Ticks of cooldown after using a Machine Remover. Default: 0 (disabled)")
        @Range.Integer(min = 0, max = Integer.MAX_VALUE)
        default int cooldownTicks() { return 0; }

        @ConfigKey("capacity")
        @ConfigComment("Total capacity for Machine Removers. Default: 1048576")
        @Range.Long(min = 1, max = Long.MAX_VALUE)
        default long capacity() { return 1048576L; }

        @ConfigKey("single_block_remove_cost")
        @ConfigComment("Energy consumed when removing a Single Block Machine. Default: 5")
        @Range.Long(min = 1, max = Long.MAX_VALUE)
        default long singleBlockRemoveCost() { return 5L; }

        @ConfigKey("multiblock_base_remove_cost")
        @ConfigComment(
            """
            Base Energy consumed when removing a Multiblock Machine.
            Total consumption is determined by this + (multiblock_block_remove_cost * block count)
            Default: 100
            """
        )
        @Range.Long(min = 1, max = Long.MAX_VALUE)
        default long multiblockBaseRemoveCost() { return 100L; }

        @ConfigKey("multiblock_block_remove_cost")
        @ConfigComment(
            """
            Base Energy consumed when removing a Multiblock Machine.
            Total consumption is determined by multiblock_base_remove_cost + (this * block count)
            Default: 25
            """
        )
        @Range.Long(min = 1, max = Long.MAX_VALUE)
        default long multiblockBlockRemoveCost() { return 25L; }


        @ConfigKey("multiblock_block_max_size")
        @ConfigComment(
            """
            Maximum size of a removable Multiblock Machine
            Any Multiblock with more blocks than this cannot be removed
            Default: 1000000
            """
        )
        @Range.Long(min = 1, max = Long.MAX_VALUE)
        default long multiblockBlockMaxSize() { return 1000000L; }

    }

    @ConfigKey("villager_trades")
    @ConfigComment("Whether some early YAI! content will be available through Villager trades")
    default boolean villagerTrades() { return true; }

}
