package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber;

import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiFunction;

/**
 * port-26.1.2: перенос Kotlin {@code pdg/chamber/DetonationChamberCasingBlock.kt}.
 *
 * <p>Факт API26 (javap MI 2.6.0-26.1.2-fleet01 + neoForm): в 26.1.2 нет {@code ItemInteractionResult}
 * (ловушка AGENTS.md 10) — {@code MachineBlock#useItemOn(ItemStack, BlockState, Level, BlockPos,
 * Player, InteractionHand, BlockHitResult)} и {@code #useWithoutItem(...)} возвращают
 * {@code InteractionResult}; {@code PASS} = прежний {@code PASS_TO_DEFAULT_BLOCK_INTERACTION}.</p>
 */
public class DetonationChamberCasingBlock extends MachineBlock {

    public DetonationChamberCasingBlock(
            BiFunction<BlockPos, BlockState, ? extends MachineBlockEntity> blockEntityConstructor,
            Properties properties) {
        super(blockEntityConstructor, properties.strength(50f, 1200f));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack handStack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
}
