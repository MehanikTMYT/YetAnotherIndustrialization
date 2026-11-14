package me.luligabi.yet_another_industrialization.common.block.machine.misc;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines;

import java.util.stream.IntStream;

public final class ConfigurableMixedStorageMachineBlockEntity extends MachineBlockEntity implements Tickable {
    public static final String ID = "configurable_mixed_storage";
    public static final String NAME = "Configurable Mixed Storage";
    private final MIInventory inventory;

    public ConfigurableMixedStorageMachineBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder(YAIIds.id(ID), true).backgroundHeight(162).build(),
                new OrientationComponent.Params(true, true, true));
        inventory = new MIInventory(
                IntStream.range(0, 9).mapToObj(i -> ConfigurableItemStack.standardIOSlot(true)).toList(),
                IntStream.range(0, 9).mapToObj(i -> ConfigurableFluidStack.standardIOSlot(16_000, true)).toList(),
                new SlotPositions.Builder().addSlots(8, 30, 9, 1).build(),
                new SlotPositions.Builder().addSlots(8, 48, 9, 1).build());
        registerGuiComponent(new AutoExtract(orientation));
        registerComponents(inventory);
    }

    @Override public MIInventory getInventory() { return inventory; }

    @Override public MachineModelClientData getMachineModelData() {
        var data = new MachineModelClientData(YAIMachines.Casings.CONFIGURABLE_MIXED_STORAGE);
        orientation.writeModelData(data);
        return data;
    }

    @Override public void tick() {
        if (level == null || level.isClientSide()) return;
        if (orientation.extractItems) inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
        if (orientation.extractFluids) inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection);
    }
}
