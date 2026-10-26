package me.luligabi.yet_another_industrialization.common;
import net.minecraft.resources.Identifier;
/** Shared identifiers, usable before the mod's lifecycle initialization. */
public final class YAIIds {
    public static final String ID = "yet_another_industrialization";
    private YAIIds() {}
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ID, path); }
}
