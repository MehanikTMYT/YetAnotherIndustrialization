package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.emi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
@EmiEntrypoint
public final class YAIEmiPlugin implements EmiPlugin {
    @Override public void register(EmiRegistry registry) {
        registry.addGenericDragDropHandler(new StorageSlotLockerDragDropHandler());
    }
}
