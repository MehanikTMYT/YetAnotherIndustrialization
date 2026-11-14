package me.luligabi.yet_another_industrialization.client.component;

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.inventory.BackgroundRenderedSlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.gui.GuiComponent.MenuFacade;
import aztech.modern_industrialization.util.Rectangle;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.ChargingSlot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public final class ChargingSlotClient extends GuiComponentClient<Unit, Unit> {
    public ChargingSlotClient(Unit params, Unit data) { super(params, data); }
    @Override public void setupMenu(MenuFacade menu) { menu.addSlotToMenu(new ClientSlot(), SlotGroup.CONFIGURABLE_STACKS); }
    private static final class ClientSlot extends Slot implements BackgroundRenderedSlot {
        private ClientSlot() { super(new SimpleContainer(1), 0, ChargingSlot.SLOT_X, ChargingSlot.SLOT_Y); }
        @Override public boolean mayPlace(ItemStack stack) { return ChargingSlot.mayPlace(stack); }
        @Override public Identifier getBackgroundAtlasLocation() { return YAIIds.id("textures/gui/container/slot_atlas.png"); }
    }
    @Override public ClientComponentRenderer createRenderer(MachineScreen screen) {
        return new ClientComponentRenderer() {
            private Rectangle box() { return new Rectangle(screen.x() + ChargingSlot.SLOT_X, screen.y() + ChargingSlot.SLOT_Y, 29, 32); }
            @Override public void addExtraBoxes(List<Rectangle> boxes, int left, int top) { boxes.add(box()); }
            @Override public void extractBackground(GuiGraphicsExtractor gui, int left, int top) {
                Rectangle box = box();
                gui.blit(RenderPipelines.GUI_TEXTURED, MachineScreen.BACKGROUND, box.x() - 8, box.y() - 8, 0, 0, box.w(), box.h() - 4, 256, 256);
                gui.blit(RenderPipelines.GUI_TEXTURED, MachineScreen.BACKGROUND, box.x() - 8, box.y() - 8 + box.h() - 4, 0, 252, box.w(), 4, 256, 256);
            }
            @Override public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphicsExtractor gui,
                                                    int x, int y, int cursorX, int cursorY) {
                if (screen.getFocusedSlot() instanceof ClientSlot slot && !slot.hasItem()) {
                    gui.setTooltipForNextFrame(YAI.text().chargingSlotTooltip(), cursorX, cursorY);
                    return true;
                }
                return false;
            }
        };
    }
}
