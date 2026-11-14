package me.luligabi.yet_another_industrialization.client.component;

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.Rectangle;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox;
import me.luligabi.yet_another_industrialization.common.misc.network.ToggleCheckboxPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public final class ToggleCheckboxClient extends GuiComponentClient<ToggleCheckbox.ParamData, Boolean> {
    public ToggleCheckboxClient(ToggleCheckbox.ParamData params, Boolean data) { super(params, data); }

    @Override public ClientComponentRenderer createRenderer(MachineScreen screen) {
        return new ClientComponentRenderer() {
            @Override public void addButtons(ButtonContainer container) {
                container.addButton(-24, 73, 20, 20,
                        syncId -> ClientPacketDistributor.sendToServer(new ToggleCheckboxPacket(syncId, !data)),
                        params::tooltip,
                        (machineScreen, button, gui, mouseX, mouseY, delta) -> {
                            boolean hovered = mouseX >= button.getX() && mouseX < button.getX() + 20
                                    && mouseY >= button.getY() && mouseY < button.getY() + 20;
                            // Vanilla Checkbox sprite fields are private in 26; use their verified identifiers.
                            String sprite = data ? (hovered ? "checkbox_selected_highlighted" : "checkbox_selected")
                                    : (hovered ? "checkbox_highlighted" : "checkbox");
                            gui.blitSprite(RenderPipelines.GUI_TEXTURED,
                                    Identifier.withDefaultNamespace("widget/" + sprite), button.getX(), button.getY(), 20, 20);
                        });
            }
            private Rectangle box(int left, int top) { return new Rectangle(left - 23, top + 74, 31, 34); }
            @Override public void addExtraBoxes(List<Rectangle> rectangles, int left, int top) {
                rectangles.add(box(left, top));
            }
            @Override public void extractBackground(GuiGraphicsExtractor gui, int left, int top) {
                var box = box(left, top);
                gui.blit(RenderPipelines.GUI_TEXTURED, MachineScreen.BACKGROUND,
                        box.x() - 8, box.y() - 8, 0, 0, box.w(), box.h() - 4, 256, 256);
                gui.blit(RenderPipelines.GUI_TEXTURED, MachineScreen.BACKGROUND,
                        box.x() - 8, box.y() - 8 + box.h() - 4, 0, 252, box.w(), 4, 256, 256);
            }
        };
    }
}
