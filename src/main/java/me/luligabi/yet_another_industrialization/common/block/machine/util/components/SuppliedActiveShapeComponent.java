package me.luligabi.yet_another_industrialization.common.block.machine.util.components;

import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Supplier;

/**
 * port-26.1.2: перенос Kotlin {@code SuppliedActiveShapeComponent} —
 * {@link aztech.modern_industrialization.machines.components.ActiveShapeComponent}, у которого
 * массив шаблонов форм передаётся поставщиком (тиры из data-map'а, см. ArboreousGreenhouseBlockEntity).
 *
 * <p>Факт API26 (javap MI 2.6.0-26.1.2-fleet01): {@code MachineComponent#writeNbt(ValueOutput)} /
 * {@code #readNbt(ValueInput, boolean)} (NBT-тегов больше нет — ValueOutput/ValueInput).</p>
 */
public class SuppliedActiveShapeComponent implements MachineComponent {

    private final Supplier<ShapeTemplate[]> shapeTemplates;

    private int activeShape = 0;

    public SuppliedActiveShapeComponent(Supplier<ShapeTemplate[]> shapeTemplates) {
        this.shapeTemplates = shapeTemplates;
    }

    public int getActiveShape() {
        return activeShape;
    }

    public void incrementShape(MultiblockMachineBlockEntity machine, int delta) {
        int newSize = shapeTemplates.get().length;
        int newShape = Math.max(0, Math.min(activeShape + delta, newSize - 1));
        setShape(machine, newShape);
    }

    public void setShape(MultiblockMachineBlockEntity machine, int newShape) {
        if (newShape != activeShape) {
            activeShape = newShape;
            machine.setChanged();
            machine.unlink();
            machine.sync(false);
        }
    }

    @Override
    public void writeNbt(ValueOutput output) {
        output.putInt("activeShape", activeShape);
    }

    @Override
    public void readNbt(ValueInput input, boolean isUpgradingMachine) {
        // апстрим: coerceAtMost(size - 1)
        activeShape = Math.min(input.getIntOr("activeShape", 0), shapeTemplates.get().length - 1);
    }
}
