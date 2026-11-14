package me.luligabi.yet_another_industrialization.common.compat.mi;

import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import me.luligabi.yet_another_industrialization.common.YAIRecipeTypes;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookEntrypoint;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookRegistry;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

/**
 * port-26.1.2: перенос Kotlin {@code compat/mi/YAIHookRegistry.kt} — tesseract MI-hook-реестр YAI
 * (эталон: IO {@code IOMIHookRegistry}, EI — аналогично; оба работают с MI 2.6.0-fleet01 +
 * tesseract-api-neoforge 1.13.7-26.1.2-fleet.1, smoke errors=0).
 *
 * <p>Отдаёт hook'у наши DeferredRegister'ы: блоки/предметы/BE машин регистрируются в
 * {@link YAIBlocks#BLOCKS}/{@link YAIBlocks#BLOCK_ENTITIES}/{@link YAIItems#ITEMS} теми же id,
 * что и раньше (стабы соответствующих машин удалены, чтобы не дублировать ключ).</p>
 *
 * <p>Recipe-типы/сериалайзеры НЕ создаются через hook ({@code machineRecipeTypes} не переопределён):
 * они уже зарегистрированы напрямую в {@link YAIRecipeTypes} — реестры отдаём те же самые,
 * чтобы {@code hook.create} при желании писал в них же.</p>
 *
 * <p>{@code onBlockRegister/onItemRegister} — заглушки: апстрим складывал холдеры для
 * кастомного creative-таба; в порте таба пока нет (см. отчёт, остаток).</p>
 */
@MIHookEntrypoint
public final class YAIHookRegistry implements MIHookRegistry {

    /** Апстрим: {@code YAICreativeTab.Order.MACHINES = SortOrder(5)}. */
    private static final SortOrder MACHINES = new SortOrder(5);

    @Override
    public DeferredRegister.Blocks blockRegistry() {
        return YAIBlocks.BLOCKS;
    }

    @Override
    public DeferredRegister<BlockEntityType<?>> blockEntityRegistry() {
        return YAIBlocks.BLOCK_ENTITIES;
    }

    @Override
    public DeferredRegister.Items itemRegistry() {
        return YAIItems.ITEMS;
    }

    @Override
    public DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry() {
        return YAIRecipeTypes.RECIPE_SERIALIZERS;
    }

    @Override
    public DeferredRegister<RecipeType<?>> recipeTypeRegistry() {
        return YAIRecipeTypes.RECIPE_TYPES;
    }

    @Override
    public void onBlockRegister(BlockHolder blockHolder) {
        // апстрим: YAIBlocks.Registry.include(holder) — нужно только для creative-таба (не портирован)
    }

    @Override
    public void onBlockEntityRegister(BlockEntityType<?> blockEntityType) {
        // апстрим: пусто
    }

    @Override
    public void onItemRegister(ItemHolder itemHolder) {
        // апстрим: YAIItems.Registry.include(holder) — нужно только для creative-таба (не портирован)
    }

    @Override
    public void onMachineRecipeTypeRegister(MachineRecipeType machineRecipeType) {
        // апстрим: пусто
    }

    @Override
    public SortOrder sortOrderMachines() {
        return MACHINES;
    }
}
