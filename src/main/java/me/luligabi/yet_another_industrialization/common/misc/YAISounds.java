package me.luligabi.yet_another_industrialization.common.misc;
import static me.luligabi.yet_another_industrialization.common.YAIIds.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
public final class YAISounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ID);
    public static final Supplier<SoundEvent> MACHINE_REMOVER_REMOVE = create("machine_remover_remove");
    public static final Supplier<SoundEvent> STORAGE_SLOT_LOCKER_LOCK = create("storage_slot_locker_lock");
    private static Supplier<SoundEvent> create(String name) { return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id(name))); }
    public static void init(IEventBus bus) { SOUNDS.register(bus); }
}
