package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse;

import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * port-26.1.2: перенос Kotlin {@code arboreous_greenhouse/SaplingComponent.kt}.
 *
 * <p>Клиентская компонента: хранит предмет/модель текущего рецепта теплицы (для отображения
 * саженца в GUI/модели). Факт API26 (javap MI 2.6.0-26.1.2-fleet01):
 * {@code MachineComponent.ClientOnly#writeClientNbt(ValueOutput)} / {@code #readClientNbt(ValueInput)}
 * (CompoundTag → ValueOutput/ValueInput, ловушки AGENTS.md).</p>
 */
public class SaplingComponent implements MachineComponent.ClientOnly {

    private static final String ITEM_KEY = "ArboreousGreenhouseSapling";
    private static final String RECIPE_KEY = "ArboreousGreenhouseRecipe";

    private Identifier model;
    private Item item;

    public Identifier getModel() {
        return model;
    }

    public Item getItem() {
        return item;
    }

    public void update(CrafterComponent crafter, ArboreousGreenhouseBlockEntity be) {
        RecipeHolder<MachineRecipe> holder = ((CrafterComponentAccessor) crafter).getActiveRecipe();
        MachineRecipe recipe = holder == null ? null : holder.value();
        if (recipe == null) {
            boolean changed = false;
            if (item != null) {
                item = null;
                changed = true;
            }
            if (model != null) {
                model = null;
                changed = true;
            }
            if (changed) {
                be.sync(false);
            }
            return;
        }
        if (recipe.itemInputs.isEmpty()) {
            return;
        }
        // апстрим: recipe.itemInputs[0].inputItems[0] (без проверки — здесь добавлена защита от пустого Ingredient)
        if (recipe.itemInputs.get(0).getInputItems().isEmpty()) {
            return;
        }
        Item newItem = recipe.itemInputs.get(0).getInputItems().get(0);
        if (item != newItem) { // new recipe
            item = newItem;
            model = null;
            if (recipe.conditions != null) {
                model = recipe.conditions.stream()
                        .filter(ArboreousGreenhouseTierCondition.class::isInstance)
                        .map(ArboreousGreenhouseTierCondition.class::cast)
                        .findFirst()
                        .map(ArboreousGreenhouseTierCondition::model)
                        .orElse(null);
            }
            be.sync(false);
        }
    }

    public void reset(ArboreousGreenhouseBlockEntity be) {
        item = null;
        model = null;
        be.sync(false);
    }

    @Override
    public void writeClientNbt(ValueOutput output) {
        if (item != null && item != Items.AIR) {
            output.putString(ITEM_KEY, BuiltInRegistries.ITEM.getKey(item).toString());
        }
        if (model != null) {
            output.putString(RECIPE_KEY, model.toString());
        }
    }

    @Override
    public void readClientNbt(ValueInput input) {
        String id = input.getStringOr(ITEM_KEY, "");
        if (!id.isEmpty()) {
            BuiltInRegistries.ITEM.getOptional(Identifier.parse(id)).ifPresent(loaded -> item = loaded);
        }
        Identifier parsed = Identifier.tryParse(input.getStringOr(RECIPE_KEY, ""));
        if (parsed != null) {
            model = parsed;
        }
    }
}
