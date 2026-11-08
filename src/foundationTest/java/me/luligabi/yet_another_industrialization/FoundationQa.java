package me.luligabi.yet_another_industrialization;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
@Mod("yai_foundation_qa")
public final class FoundationQa {
    public FoundationQa(net.neoforged.bus.api.IEventBus bus) {
        me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents.init(bus);
        ItemChecks.register(bus);
        me.luligabi.yet_another_industrialization.common.misc.network.YAIPackets.init(bus);
        me.luligabi.yet_another_industrialization.common.misc.YAISounds.init(bus);
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            if (!event.getServer().isDedicatedServer()) return;
            try { FoundationChecks.main(new String[0]); FoundationChecks.checkComponents(event.getServer().registryAccess()); ItemChecks.run(event.getServer()); LockerChecks.run(event.getServer()); }
            catch (Throwable failure) { failure.printStackTrace(); System.out.println("YAI_FOUNDATION_FAIL"); }
            finally { event.getServer().halt(false); }
        });
    }
}
