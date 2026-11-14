package me.luligabi.yet_another_industrialization.common.compat.mi;

import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookEntrypoint;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookListener;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineCasingsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.HatchMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MultiblockMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockCraftingMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockSpecialMachinesMIHookContext;

/**
 * port-26.1.2: перенос Kotlin {@code compat/mi/YAIHookListener.kt} — tesseract MI-hook-слушатель YAI
 * (эталон: IO {@code IOMIHookListener}).
 *
 * <p>Вызывается из {@code TesseractMI.init(YAI.ID)} в конструкторе мода; порядок контекстов:
 * machineProcessConditions → machineRecipeTypes → machineCasings → hatches → singleBlockSpecial →
 * singleBlockCrafting → multiblockMachines (javap -c TesseractMI.lambda$init$0).</p>
 *
 * <p>Не переопределяются (по сравнению с апстримом):</p>
 * <ul>
 *   <li>{@code machineRecipeTypes} — recipe-типы YAI зарегистрированы напрямую в
 *       {@link me.luligabi.yet_another_industrialization.common.YAIRecipeTypes} (этап1 порта);</li>
 *   <li>{@code machineProcessConditions} — условия регистрируются напрямую в YAIRecipeTypes.init;</li>
 *   <li>{@code viewerSetup} (REI-категории YAI) — EMI-only, не переносится;</li>
 *   <li>{@code tooltips} — YAITooltips использует tesseract LangManager, не портирован (остаток).</li>
 * </ul>
 */
@MIHookEntrypoint
public final class YAIHookListener implements MIHookListener {

    @Override
    public void hatches(HatchMIHookContext hook) {
        YAIMachines.hatches(hook);
    }

    @Override
    public void machineCasings(MachineCasingsMIHookContext hook) {
        YAIMachines.machineCasings(hook);
    }

    @Override
    public void singleBlockSpecialMachines(SingleBlockSpecialMachinesMIHookContext hook) {
        YAIMachines.singleBlockSpecial(hook);
    }

    @Override
    public void singleBlockCraftingMachines(SingleBlockCraftingMachinesMIHookContext hook) {
        YAIMachines.singleBlockCrafting(hook);
    }

    @Override
    public void multiblockMachines(MultiblockMachinesMIHookContext hook) {
        YAIMachines.multiblockMachines(hook);
    }
}
