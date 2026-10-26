package me.luligabi.yet_another_industrialization.common.compat.guideme;
import aztech.modern_industrialization.guidebook.MultiblockShapeCompiler;
import guideme.Guide;
import guideme.scene.element.SceneElementTagCompiler;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import net.minecraft.resources.Identifier;
public final class YAIGuide {
    public static final Identifier ID = YAIIds.id("guide");
    public static final Guide GUIDE = Guide.builder(ID).folder("yai_guidebook")
        .extension(SceneElementTagCompiler.EXTENSION_POINT, new MultiblockShapeCompiler()).build();
    private YAIGuide() {}
}
