package me.luligabi.yet_another_industrialization.common;

import me.luligabi.yet_another_industrialization.client.SlotLockerClient;
import me.luligabi.yet_another_industrialization.client.YAIMachineClient;
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity;
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import me.luligabi.yet_another_industrialization.common.misc.YAICreativeTab;
import me.luligabi.yet_another_industrialization.common.misc.YAISounds;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps;
import me.luligabi.yet_another_industrialization.common.misc.effect.YAIEffects;
import me.luligabi.yet_another_industrialization.common.misc.network.YAIPackets;
import me.luligabi.yet_another_industrialization.common.util.YAIText;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.tesseract.neoforge.compat.mi.TesseractMI;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHooks;
import net.swedz.tesseract.neoforge.lang.LangManager;

@Mod(YAIIds.ID)
public class YAI {

    public static final String ID = YAIIds.ID;

    // port-26.1.2: tesseract ConfigManager убран из рантайма (см. YAIConfig) — дефолты апстрима (YAIConfig.kt)
    public static final YAIConfig CONFIG = YAIConfig.DEFAULT;

    private static YAIText TEXT;

    public static YAIText text() {
        return TEXT;
    }

    public YAI(IEventBus modEventBus, ModContainer container) {
        setupText();
        modEventBus.addListener(net.neoforged.neoforge.data.event.GatherDataEvent.Client.class, YAIClientAssets::gather);

        // port-26.1.2: tesseract MI-hook — запускает YAIHookListener/YAIHookRegistry
        // (@MIHookEntrypoint): кейсины → хатчи → single-block → multiblock-машины.
        // Порядок как в апстриме (YAI.kt): TesseractMI.init ДО init(bus) наших DeferredRegister,
        // чтобы hook успел добавить записи в BLOCKS/BLOCK_ENTITIES/ITEMS до attach.
        TesseractMI.init(ID);

        // Registry/data-component/sound/effect/packet registration — common (server+client).
        YAIBlocks.init(modEventBus);
        YAIItems.init(modEventBus);
        YAIFluids.init(modEventBus);
        YAIDataComponents.init(modEventBus);
        YAISounds.init(modEventBus);
        YAIEffects.init(modEventBus);
        YAIPackets.init(modEventBus);
        YAIRecipeTypes.init(modEventBus); // port-26.1.2: recipe-типы + process-условия (прямая регистрация, см. YAIRecipeTypes)
        YAICreativeTab.init(modEventBus); // port-26.1.2: перенос апстримной креатив-вкладки

        // port-26.1.2: отложенные hook-задачи (registerMultiblockShape, registerImitateBlock →
        // registeredCasings.put и др.): fleet-tesseract НЕ вызывает MIHooks.executeEnqueuedTasks()
        // нигде (ловушка AGENTS.md 8; фикс — как в EI: после всех construction/common-фаз и ДО
        // первого resource-reload с моделями, иначе кейсины не регистрируются → NPE в CasingModels
        // на клиенте и нерабочие multiblock-формы).
        modEventBus.addListener(FMLLoadCompleteEvent.class,
                event -> MIHooks.executeEnqueuedTasks());

        // port-26.1.2: data-map'ы тиров (ArboreousGreenhouseTier) + пересчёт форм теплицы
        // после загрузки датапаков (эталон: EI EIDataMaps + DataMapsUpdatedEvent → initTiers).
        modEventBus.addListener(RegisterDataMapTypesEvent.class, YAIDataMaps::init);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DataMapsUpdatedEvent.class,
                event -> event.ifRegistry(Registries.BLOCK,
                        registry -> {
                            ArboreousGreenhouseBlockEntity.initTiers();
                            FlightPylonBlockEntity.initTiers();
                            LargeStorageUnitBlockEntity.initTiers();
                        }));

        // Client-only registration (tooltips, input handling) — guard so dedicated server does not load client classes.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            SlotLockerClient.init(modEventBus);
            YAIMachineClient.init(modEventBus);
        }
    }

    // port-26.1.2: tesseract LangManager (эталон EI.setupText) — YAIText это @LangKey-интерфейс,
    // инстанс создаётся прокси через build().load().lang(). builtinColorStyles регистрирует имена
    // цветов ChatFormatting (gray, red, ...); gray_italic/highlight/green_neutron/machine_remover — свои.
    private static void setupText() {
        var instance = new LangManager(ID)
                .builtinColorStyles()
                .style("gray_italic", Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true))
                // цвет как EITextStyles.HIGHLIGHT_STYLE у EI
                .style("highlight", Style.EMPTY.withColor(TextColor.fromRgb(0xFFDE7D)).withItalic(false))
                .style("green_neutron", TextHelper.NEUTRONS)
                // был top-level val MACHINE_REMOVER_STYLE в Kotlin YAIUtil.kt
                .style("machine_remover", Style.EMPTY.withColor(TextColor.fromRgb(0xD84D2C)).withItalic(false))
                .builtinParsers()
                .build(YAIText.class)
                .load();
        TEXT = instance.lang();
    }
}
