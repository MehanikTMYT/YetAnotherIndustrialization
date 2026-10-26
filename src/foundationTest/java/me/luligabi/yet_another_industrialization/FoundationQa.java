package me.luligabi.yet_another_industrialization;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
@Mod("yai_foundation_qa")
public final class FoundationQa {
    public FoundationQa() {
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            try { FoundationChecks.main(new String[0]); FoundationChecks.checkComponents(event.getServer().registryAccess()); }
            catch (Throwable failure) { failure.printStackTrace(); System.out.println("YAI_FOUNDATION_FAIL"); }
            finally { event.getServer().halt(false); }
        });
    }
}
