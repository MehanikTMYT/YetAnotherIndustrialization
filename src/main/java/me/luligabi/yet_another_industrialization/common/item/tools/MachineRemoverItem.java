package me.luligabi.yet_another_industrialization.common.item.tools;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.misc.YAISounds;
import me.luligabi.yet_another_industrialization.common.misc.YAITags;
import me.luligabi.yet_another_industrialization.mixin.MultiblockMachineBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class MachineRemoverItem extends Item implements ISimpleEnergyItem {

    // port-26.1.2: был top-level val MACHINE_REMOVER_STYLE в Kotlin YAIUtil.kt (Style with color0xD84D2C)
    private static final Style MACHINE_REMOVER_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xD84D2C)).withItalic(false);

    public MachineRemoverItem(Properties properties) {
        super(properties.stacksTo(1).component(MIComponents.ENERGY, 0L));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) return InteractionResult.FAIL;

        if (ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof MachineBlockEntity machine) {
            if (machine.getType().builtInRegistryHolder().is(YAITags.MACHINE_REMOVER_BANNED)) {
                // port-26.1.2: YAIText.X.text() → прокси-инстанс YAI.text().machineRemoverBanned()
                ctx.getPlayer().sendOverlayMessage(YAI.text().machineRemoverBanned().withStyle(MACHINE_REMOVER_STYLE)); // port-26.1.2: displayClientMessage удалён
                return InteractionResult.SUCCESS;
            }

            boolean success;
            if (machine instanceof MultiblockMachineBlockEntity multiblock) {
                success = removeMultiblock(ctx.getItemInHand(), ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), multiblock);
            } else {
                success = removeSingleMachine(ctx.getItemInHand(), ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), machine);
            }

            if (success) {
                ctx.getLevel().playSound(null, ctx.getClickedPos(), YAISounds.MACHINE_REMOVER_REMOVE.get(), SoundSource.PLAYERS, 1f, 1f);
                int cooldown = YAI.CONFIG.machineRemover().cooldownTicks();
                // port-26.1.2: addCooldown в26 принимает ItemStack, а не Item
                if (cooldown > 0) ctx.getPlayer().getCooldowns().addCooldown(ctx.getItemInHand(), cooldown);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private boolean removeMultiblock(ItemStack stack, Player player, Level level, BlockPos pos, MultiblockMachineBlockEntity controller) {
        if (!controller.isShapeValid()) {
            return removeSingleMachine(stack, player, level, pos, controller);
        }

        // port-26.1.2: ShapeMatcher.getPositions() возвращает Set<BlockPos>
        Set<BlockPos> members = ((MultiblockMachineBlockEntityAccessor) controller).getShapeMatcher().getPositions();
        // port-26.1.2: апстрим3641da7 использует конфиг multiblockBlockMaxSize (default1000000), не128
        if (members.size() > YAI.CONFIG.machineRemover().multiblockBlockMaxSize()) {
            player.sendOverlayMessage(YAI.text().machineRemoverTooLarge().withStyle(MACHINE_REMOVER_STYLE)); // port-26.1.2: displayClientMessage удалён
            return false;
        }

        long cost = YAI.CONFIG.machineRemover().multiblockBaseRemoveCost() + ((long) members.size() * YAI.CONFIG.machineRemover().multiblockBlockRemoveCost());
        if (tryUseEnergy(stack, cost)) {
            for (BlockPos memberPos : members) {
                removeBlock(level, player, memberPos, pos, null);
            }
            removeBlock(level, player, pos, pos, controller);
            return true;
        } else {
            player.sendOverlayMessage(YAI.text().machineRemoverInsufficientEnergy().withStyle(MACHINE_REMOVER_STYLE)); // port-26.1.2: displayClientMessage удалён
            return false;
        }
    }

    private boolean removeSingleMachine(ItemStack stack, Player player, Level level, BlockPos pos, MachineBlockEntity machine) {
        if (tryUseEnergy(stack, YAI.CONFIG.machineRemover().singleBlockRemoveCost())) {
            removeBlock(level, player, pos, pos, machine);
            return true;
        } else {
            player.sendOverlayMessage(YAI.text().machineRemoverInsufficientEnergy().withStyle(MACHINE_REMOVER_STYLE)); // port-26.1.2: displayClientMessage удалён
            return false;
        }
    }

    private void removeBlock(Level level, Player player, BlockPos blockPos, BlockPos dropPos, MachineBlockEntity machine) {
        level.getBlockState(blockPos).getBlock().playerDestroy(level, player, dropPos, level.getBlockState(blockPos), machine, ItemStack.EMPTY);
        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !stack.has(DataComponents.CUSTOM_DATA);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) (getStoredEnergy(stack) / (double) YAI.CONFIG.machineRemover().capacity() * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF0000;
    }

    @Override
    // port-26.1.2: ISimpleEnergyItem.getEnergyComponent() возвращает DataComponentType<Long> (GrandPower3.0)
    public DataComponentType<Long> getEnergyComponent() {
        return MIComponents.ENERGY.get();
    }

    @Override
    public long getEnergyCapacity(ItemStack stack) {
        return YAI.CONFIG.machineRemover().capacity();
    }

    @Override
    public long getEnergyMaxInput(ItemStack stack) {
        return YAI.CONFIG.machineRemover().capacity();
    }

    @Override
    public long getEnergyMaxOutput(ItemStack stack) {
        return 0L;
    }
}
