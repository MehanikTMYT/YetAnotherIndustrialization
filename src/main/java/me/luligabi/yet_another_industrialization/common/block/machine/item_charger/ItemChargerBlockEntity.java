package me.luligabi.yet_another_industrialization.common.block.machine.item_charger;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.CableTierHolder;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.*;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import java.util.List;

/** Item charging, casing upgrades, redstone control and automatic charged-item output. */
public final class ItemChargerBlockEntity extends MachineBlockEntity
        implements EnergyComponentHolder, CableTierHolder, Tickable {
    public static final String ID = "item_charger";
    public static final String NAME = "Item Charger";
    private final RedstoneControlComponent redstoneControl = new RedstoneControlComponent();
    private final CasingComponent casing = new CasingComponent();
    private final IsActiveComponent isActive = new IsActiveComponent();
    private final EnergyComponent energy = new EnergyComponent(this, casing::getEuCapacity);
    private final MIEnergyStorage insertable = energy.buildInsertable(casing::canInsertEu);
    private final MIEnergyStorage extractable = energy.buildExtractable(tier -> true);
    private final MachineInventoryComponent inventory = new MachineInventoryComponent(
            List.of(ConfigurableItemStack.standardInputSlot()), List.of(ConfigurableItemStack.standardOutputSlot()),
            List.of(), List.of(), new SlotPositions.Builder().addSlot(56, 35).addSlot(102, 35).build(), SlotPositions.empty());

    public ItemChargerBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder(YAIIds.id(ID), true).build(),
                new OrientationComponent.Params(true, true, false));
        registerComponents(redstoneControl, casing, isActive, energy, inventory);
        registerGuiComponent(new EnergyBar(new EnergyBar.Params(18, 30), energy::getEu, energy::getCapacity));
        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl).withCasing(casing));
        registerGuiComponent(new AutoExtract(orientation));
        registerGuiComponent(new ProgressBar(new ProgressBar.Params(77, 33, "yai_charge", true), this::getProgress));
    }

    public static void registerEnergyApi(BlockEntityType<?> type) {
        MICapabilities.onEvent(event -> event.registerBlockEntity(EnergyApi.SIDED, type,
                (be, side) -> ((ItemChargerBlockEntity) be).insertable));
    }

    @Override public void tick() {
        if (level == null || level.isClientSide()) return;
        if (orientation.extractItems) inventory.inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
        if (!redstoneControl.doAllowNormalOperation(this)) {
            isActive.updateActive(false, this);
            return;
        }
        ConfigurableItemStack input = inventory.getItemInputs().getFirst();
        ItemStack stack = input.toStack();
        EnergyHandler itemEnergy = energyOf(stack);
        if (itemEnergy != null && EnergyHandlerUtil.isFull(itemEnergy)) {
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = inventory.inventory.itemStorage.insert(input.getVariant(), input.getAmount(), tx,
                        ConfigurableItemStack::canPipesExtract, false);
                if (inserted > 0) {
                    input.updateSnapshots(tx);
                    input.decrement(inserted);
                    if (input.getAmount() == 0) input.setKey(ItemResource.EMPTY);
                    tx.commit();
                }
            }
            // Refresh after moving the completed stack; never write an old charged copy back into the input.
            stack = input.toStack();
            itemEnergy = energyOf(stack);
        }
        boolean charging = itemEnergy != null && EnergyHandlerUtil.move(extractable, itemEnergy,
                (int) Math.min(Integer.MAX_VALUE, getCableTier().getMaxTransfer()), null) > 0;
        if (charging) {
            input.setKey(ItemResource.of(stack));
            input.setAmount(stack.getCount());
        }
        isActive.updateActive(charging, this);
        setChanged();
    }

    private static EnergyHandler energyOf(ItemStack stack) {
        return stack.isEmpty() ? null : ItemAccess.forStack(stack).getCapability(EnergyApi.ITEM);
    }
    private float getProgress() {
        EnergyHandler storage = energyOf(inventory.getItemInputs().getFirst().toStack());
        return storage == null || storage.getCapacityAsLong() == 0 ? 0 :
                (float) storage.getAmountAsLong() / storage.getCapacityAsLong();
    }
    @Override public InteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        InteractionResult result = super.useItemOn(player, hand, face);
        if (!result.consumesAction()) result = redstoneControl.onUse(this, player, hand);
        if (!result.consumesAction()) result = casing.onUse(this, player, hand);
        return result;
    }
    @Override public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData(casing.getCasing());
        orientation.writeModelData(data);
        data.isActive = isActive.isActive;
        return data;
    }
    @Override public MIInventory getInventory() { return inventory.inventory; }
    @Override public EnergyComponent getEnergyComponent() { return energy; }
    @Override public CableTier getCableTier() { return casing.getCableTier(); }
}
