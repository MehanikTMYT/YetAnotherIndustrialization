package me.luligabi.yet_another_industrialization.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.swedz.tesseract.neoforge.lang.LangInstance;
import net.swedz.tesseract.neoforge.lang.LangManager;
import net.swedz.tesseract.config.ConfigManager;
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess;
import me.luligabi.yet_another_industrialization.common.util.YAIText;

@Mod(YAIIds.ID)
public class YAI {

    public static final String ID = YAIIds.ID;
    public static final LangInstance<YAIText> TEXT;
    public static final YAIConfig CONFIG;
    
    static {
        LangManager manager = new LangManager(ID);
        TEXT = manager.build(YAIText.class);
        
        ModConfigFileAccess file = new ModConfigFileAccess(net.neoforged.fml.ModList.get().getModContainerById(ID).orElseThrow(), net.neoforged.fml.config.ModConfig.Type.COMMON);
        CONFIG = new ConfigManager(file).build(YAIConfig.class).load().config();
    }

    public YAI(IEventBus modEventBus, ModContainer container) {
        
    }
}
