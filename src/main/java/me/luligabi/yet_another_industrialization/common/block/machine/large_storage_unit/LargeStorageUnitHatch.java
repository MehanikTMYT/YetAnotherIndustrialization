package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import java.util.List;

public final class LargeStorageUnitHatch extends HatchBlockEntity implements EnergyListComponentHolder {
    public static final String ID_INPUT = "large_storage_unit_input_hatch";
    public static final String ID_OUTPUT = "large_storage_unit_output_hatch";
    private final boolean input;
    private LargeStorageUnitBlockEntity controller;
    public LargeStorageUnitHatch(BEP bep, boolean input, Identifier blockId) {
        super(bep, new MachineGuiParameters.Builder(blockId, false).build(), new OrientationComponent.Params(false, false, false));
        this.input = input;
    }
    public static void registerEnergyApi(BlockEntityType<?> type) {
        MICapabilities.onEvent(event -> event.registerBlockEntity(EnergyApi.SIDED, type,
                (be, side) -> ((LargeStorageUnitHatch) be).getStorage()));
    }
    @Override public InteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        var item = ItemAccess.forPlayerInteraction(player, hand).getCapability(EnergyApi.ITEM);
        if (item == null || controller == null) return super.useItemOn(player, hand, face);
        if (!player.level().isClientSide()) {
            if (input) LargeStorageUnitBlockEntity.transferRepeated(item, getStorage());
            else LargeStorageUnitBlockEntity.transferRepeated(getStorage(), item);
        }
        return InteractionResult.SUCCESS;
    }
    @Override public List<EnergyComponent> getEnergyComponents() { return controller == null ? List.of() : List.of(controller.energy); }
    @Override public void openMenu(ServerPlayer player) {
        if (controller != null) controller.openMenu(player);
        else player.sendOverlayMessage(YAI.text().noLargeStorageUnit());
    }
    @Override public HatchType getHatchType() {
        return input ? YAIHatchTypes.LARGE_STORAGE_UNIT_INPUT : YAIHatchTypes.LARGE_STORAGE_UNIT_OUTPUT;
    }
    @Override public void unlink() {
        super.unlink();
        controller = null;
        invalidateCapabilities();
    }
    @Override public int getComparatorOutput() {
        MIEnergyStorage storage = getStorage();
        if (storage.getCapacityAsLong() == 0) return 0;
        return Mth.floor((double) storage.getAmountAsLong() / storage.getCapacityAsLong() * 14) + (storage.getAmountAsLong() > 0 ? 1 : 0);
    }
    @Override public boolean hasComparatorOutput() { return true; }
    @Override public boolean upgradesToSteel() { return false; }
    @Override public MIInventory getInventory() { return MIInventory.EMPTY; }
    public void setController(LargeStorageUnitBlockEntity controller) {
        this.controller = controller;
        invalidateCapabilities();
    }
    private MIEnergyStorage getStorage() {
        return controller == null ? EnergyApi.EMPTY : input ? controller.insertable : controller.extractable;
    }
}
