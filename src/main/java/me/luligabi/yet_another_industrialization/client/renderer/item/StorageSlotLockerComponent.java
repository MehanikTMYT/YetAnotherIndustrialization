package me.luligabi.yet_another_industrialization.client.renderer.item;
import aztech.modern_industrialization.client.util.RenderHelper;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.item.tools.StorageSlotLockerItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
public record StorageSlotLockerComponent(StorageSlotLockerItem.TooltipData data) implements ClientTooltipComponent {
    private static final Identifier TEXTURE = YAIIds.id("textures/gui/container/slot_atlas.png");
    @Override public int getWidth(Font font) { return 40; }
    @Override public int getHeight(Font font) { return 22; }
    public static int textureY(boolean empty, Boolean lock) {
        return empty ? 0 : lock == null ? 54 : lock ? 18 : 36;
    }
    @Override public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        if (!data.itemVariant().isEmpty())
            RenderHelper.renderAndDecorateItem(graphics, font, data.itemVariant().toStack(1), x + 1, y + 1);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 18,
            textureY(data.itemVariant().isEmpty(), data.mode().lockItem), 18, 18, 256, 256);
        if (!data.fluidVariant().isEmpty())
            RenderHelper.drawFluidInGui(graphics, data.fluidVariant(), x + 23, y + 1);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 22, y, 36,
            textureY(data.fluidVariant().isEmpty(), data.mode().lockFluid), 18, 18, 256, 256);
    }
}
