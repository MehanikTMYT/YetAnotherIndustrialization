package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.emi;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket;
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
public final class StorageSlotLockerDragDropHandler implements EmiDragDropHandler<Screen> {
    private static boolean accepts(Slot slot, EmiStack ingredient) {
        if (!slot.isActive() || !(slot.getItem().getItem() instanceof YAIDraggable tool)) return false;
        if (ingredient.getKey() instanceof Item) return tool.dragItem(slot.getItem(), ItemResource.of(ingredient.getItemStack()), true);
        if (ingredient.getKey() instanceof Fluid fluid)
            return tool.dragFluid(slot.getItem(), FluidResource.of(fluid, ingredient.getComponentChanges()), true);
        return false;
    }
    @Override public boolean dropStack(Screen screen, EmiIngredient ingredient, int mouseX, int mouseY) {
        if (!(screen instanceof AbstractContainerScreen<?> container) || screen instanceof CreativeModeInventoryScreen
                || ingredient.getEmiStacks().isEmpty()) return false;
        var resource = ingredient.getEmiStacks().getFirst();
        for (int i = 0; i < container.getMenu().slots.size(); i++) {
            var slot = container.getMenu().slots.get(i);
            int x = container.getGuiLeft() + slot.x, y = container.getGuiTop() + slot.y;
            if (mouseX < x || mouseX >= x + 16 || mouseY < y || mouseY >= y + 16 || !accepts(slot, resource)) continue;
            if (resource.getKey() instanceof Item)
                ClientPacketDistributor.sendToServer(new SlotLockerDragPacket(container.getMenu().containerId, i, ItemResource.of(resource.getItemStack())));
            else if (resource.getKey() instanceof Fluid fluid)
                ClientPacketDistributor.sendToServer(new SlotLockerDragPacket(container.getMenu().containerId, i, FluidResource.of(fluid, resource.getComponentChanges())));
            return true;
        }
        return false;
    }
    @Override public void render(Screen screen, EmiIngredient ingredient, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!(screen instanceof AbstractContainerScreen<?> container) || screen instanceof CreativeModeInventoryScreen
                || ingredient.getEmiStacks().isEmpty()) return;
        for (var slot : container.getMenu().slots) {
            if (!accepts(slot, ingredient.getEmiStacks().getFirst())) continue;
            int x = container.getGuiLeft() + slot.x, y = container.getGuiTop() + slot.y;
            graphics.fill(x, y, x + 16, y + 16, 0x8822BB33);
        }
    }
}
