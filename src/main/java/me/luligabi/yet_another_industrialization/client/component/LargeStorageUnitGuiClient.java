package me.luligabi.yet_another_industrialization.client.component;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.TextHelper;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitGui;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import java.util.List;
import java.util.Optional;

public final class LargeStorageUnitGuiClient extends GuiComponentClient<LargeStorageUnitGui.Data, LargeStorageUnitGui.Data> {
    private static final Identifier TEXTURE = YAIIds.id("textures/gui/container/large_storage_unit_gui.png");
    private static final int X = 5, Y = 16, WIDTH = 166, HEIGHT = 43;
    public LargeStorageUnitGuiClient(LargeStorageUnitGui.Data params, LargeStorageUnitGui.Data data) { super(params, data); }
    @Override public ClientComponentRenderer createRenderer(MachineScreen screen) {
        return new ClientComponentRenderer() {
            @Override public void extractBackground(GuiGraphicsExtractor gui, int x, int y) {
                Font font = Minecraft.getInstance().font;
                gui.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + X, y + Y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
                gui.text(font, (data.isShapeValid() ? MIText.MultiblockStatusActive : MIText.MultiblockShapeInvalid).text(),
                        x + 10, y + 23, data.isShapeValid() ? 0xFFFFFFFF : 0xFFFF0000, false);
                gui.text(font, capacityText(font), x + 10, y + 34, 0xFFFFFFFF, false);
                gui.text(font, YAI.text().largeStorageUnitEnergyIO(coloredEu(data.currentInput() - data.currentOutput(), true)),
                        x + 10, y + 45, 0xFFFFFFFF, false);
            }
            @Override public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphicsExtractor gui,
                                                    int x, int y, int cursorX, int cursorY) {
                if (cursorX >= x + X && cursorX < x + X + WIDTH && cursorY >= y + Y && cursorY < y + Y + HEIGHT) {
                    List<Component> tooltip = List.of(MIText.EuMaxed.text(data.eu(), data.maxEu(), ""),
                            YAI.text().input(coloredEu(data.currentInput(), false)),
                            YAI.text().output(coloredEu(-data.currentOutput(), false)));
                    gui.setTooltipForNextFrame(font, tooltip, Optional.empty(), cursorX, cursorY);
                }
                return true;
            }
        };
    }
    private FormattedCharSequence capacityText(Font font) {
        var amount = TextHelper.getMaxedAmount(data.eu(), data.maxEu());
        Component title = YAI.text().largeStorageUnitCapacity(amount.digit(), amount.maxDigit(), amount.unit(),
                MITooltips.RATIO_PERCENTAGE_PARSER.parse(data.maxEu() == 0 ? 0.0 : (double) data.eu() / data.maxEu()));
        int maxWidth = X + WIDTH - 12;
        if (font.width(title) > maxWidth) {
            return Language.getInstance().getVisualOrder(FormattedText.composite(
                    font.substrByWidth(title, maxWidth - font.width("...%)")), FormattedText.of("...%)")));
        }
        return title.getVisualOrderText();
    }
    private static Component coloredEu(long eu, boolean abbreviated) {
        var amount = TextHelper.getAmount(eu);
        String text = abbreviated ? amount.digit() + " " + amount.unit() + "EU" : eu + " EU";
        return Component.literal((eu > 0 ? "+" : "") + text).withStyle(eu > 0 ? ChatFormatting.GREEN : eu < 0 ? ChatFormatting.RED : ChatFormatting.WHITE);
    }
}
