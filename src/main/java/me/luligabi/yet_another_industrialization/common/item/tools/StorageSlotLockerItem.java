package me.luligabi.yet_another_industrialization.common.item.tools;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.inventory.*;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import me.luligabi.yet_another_industrialization.common.misc.YAISounds;
import me.luligabi.yet_another_industrialization.common.misc.component.*;
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.DataComponentHolderResource;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class StorageSlotLockerItem extends Item implements YAIDraggable {
    public StorageSlotLockerItem(Properties properties) {
        super(properties.stacksTo(1).component(YAIDataComponents.SLOT_LOCKER_DATA.get(), SlotLockerData.EMPTY)
            .component(MIComponents.ENERGY.get(), 0L));
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.FAIL;
        var data = context.getItemInHand().get(YAIDataComponents.SLOT_LOCKER_DATA.get());
        if (data == null || !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MachineBlockEntity machine))
            return InteractionResult.PASS;
        if (machine.getInventory() == MIInventory.EMPTY) return InteractionResult.PASS;
        if (!applyLocks(machine.getInventory(), data)) return InteractionResult.FAIL;
        machine.setChanged();
        context.getLevel().playSound(null, context.getClickedPos(), YAISounds.STORAGE_SLOT_LOCKER_LOCK.get(),
            SoundSource.PLAYERS, 1, 1);
        return InteractionResult.SUCCESS;
    }
    public static boolean applyLocks(MIInventory inventory, SlotLockerData data) {
        boolean items = toggle(inventory.getItemStacks(), data.mode().lockItem, data.itemVariant());
        boolean fluids = toggle(inventory.getFluidStacks(), data.mode().lockFluid, data.fluidVariant());
        return items || fluids;
    }
    private static <T, R extends DataComponentHolderResource<T>> boolean toggle(
            List<? extends AbstractConfigurableStack<T, R>> slots, Boolean enable, R resource) {
        if (enable == null || (enable && resource.isEmpty())) return false;
        boolean changed = false;
        for (var slot : slots) {
            if (!slot.canPlayerLock() || slot.isMachineLocked()) continue;
            if (enable) {
                if (!slot.isEmpty() || slot.getLockedInstance() != null) continue;
                changed |= slot.playerLock(resource.value(), Simulation.ACT);
            } else if (slot.isPlayerLocked() && slot.getLockedInstance() == resource.value()) {
                slot.togglePlayerLock();
                changed = true;
            }
        }
        return changed;
    }
    @Override public boolean dragItem(ItemStack stack, ItemResource resource, boolean simulation) {
        var data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get());
        if (data == null) return false;
        if (!simulation) stack.set(YAIDataComponents.SLOT_LOCKER_DATA.get(), data.withItem(resource));
        return true;
    }
    @Override public boolean dragFluid(ItemStack stack, FluidResource resource, boolean simulation) {
        var data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get());
        if (data == null) return false;
        if (!simulation) stack.set(YAIDataComponents.SLOT_LOCKER_DATA.get(), data.withFluid(resource));
        return true;
    }
    @Override public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        var data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get());
        return data == null ? Optional.empty() : Optional.of(new TooltipData(data.itemVariant(), data.fluidVariant(), data.mode()));
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        var data = stack.get(YAIDataComponents.SLOT_LOCKER_DATA.get());
        if (data == null) return;
        if (flag.hasShiftDown() || flag.shouldDisplayAllInformation()) {
            Component item = data.itemVariant().isEmpty() ? none() : data.itemVariant().toStack(1).getHoverName().copy().withColor(0xFF8040);
            Component fluid = data.fluidVariant().isEmpty() ? none() : data.fluidVariant().toStack(1).getHoverName().copy().withColor(0x3946DB);
            tooltip.accept(Component.translatable("text.yet_another_industrialization.slot_locker_tooltip_1", item, fluid));
        }
        tooltip.accept(Component.translatable("text.yet_another_industrialization.slot_locker_mode_prefix", data.mode().text())
            .withStyle(ChatFormatting.GRAY));
    }
    private static Component none() { return Component.translatable("text.yet_another_industrialization.none").withStyle(ChatFormatting.GRAY); }
    public record TooltipData(ItemResource itemVariant, FluidResource fluidVariant, SlotLockerData.Mode mode) implements TooltipComponent {}
}
