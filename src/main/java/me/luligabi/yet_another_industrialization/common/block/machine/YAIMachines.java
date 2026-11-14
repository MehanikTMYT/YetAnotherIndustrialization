package me.luligabi.yet_another_industrialization.common.block.machine;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.compat.rei.machines.SteamMode;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.YAIRecipeTypes;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.DragonSiphonBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.PulseDetonationGeneratorBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlock;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlockEntity;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.GeneratorMachineBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.item_charger.ItemChargerBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.NumismaticGeneratorBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch;
import me.luligabi.yet_another_industrialization.common.block.machine.nuclear_rod_irradiator.NuclearRodIrradiatorBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.misc.ConfigurableMixedStorageMachineBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.misc.MixedHatch;
import me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch.ItemTrashCanHatch;
import me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch.FluidTrashCanHatch;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.HatchMIHookContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineCasingsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MultiblockMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockCraftingMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockSpecialMachinesMIHookContext;

/**
 * port-26.1.2: перенос Kotlin {@code block/machine/YAIMachines.kt} (функции регистрации машин
 * через tesseract MI-hook — вариант А, эталон: IO {@code IOMachines}).
 *
 * <p>Порядок вызова контекстов задаёт {@code TesseractMI.init} (javap -c TesseractMI.lambda$init$0):
 * machineProcessConditions → machineRecipeTypes → machineCasings → hatches → singleBlockSpecial →
 * singleBlockCrafting → multiblockMachines. Кейсины регистрируются ДО машин, поэтому
 * {@code Casings.*} к моменту сборки SHAPE уже заполнены.</p>
 *
 * <p>Машины и хатчи оригинала регистрируются hook-билдерами с исходными ID.
 * GUI, inventory, energy capabilities и динамические формы определены в Java block entities.</p>
 */
public final class YAIMachines {

    // yes, i know.
    public static final String CP_ID = "cryogenic_precipitator";
    public static final String CP_NAME = "Cryogenic Precipitator";

    public static final String SPESB_ID = "steel_plated_end_stone_bricks";

    private YAIMachines() {
    }

    /** Апстрим: {@code fun singleBlockCrafting(hook)}. */
    public static void singleBlockCrafting(SingleBlockCraftingMachinesMIHookContext hook) {
        hook.builder(CP_ID, CP_NAME, YAIRecipeTypes.CRYOGENIC_PRECIPITATOR)
                .electric()
                .builtinModel(CableTier.LV.casing, CP_ID)
                .gui(SteamMode.ELECTRIC_ONLY, gui -> {
                    gui.slots(slots -> {
                        slots.itemInput(39, 27);
                        slots.fluidInput(57, 27);
                        slots.fluidInput(57, 45, () -> MIFluids.CRYOFLUID.asFluid());
                        slots.itemOutputs(103, 27, 2, 1);
                        slots.fluidOutput(103, 45, () -> MIFluids.ARGON.asFluid());
                        slots.fluidOutput(121, 45, () -> MIFluids.HELIUM.asFluid());
                    });
                    gui.progressBar(79, 34, "extract");
                    gui.efficiencyBar(38, 66);
                    gui.energyBar(14, 35);
                })
                .registerMachine();
    }

    /** Апстрим: {@code fun singleBlockSpecial(hook)}. */
    public static void singleBlockSpecial(SingleBlockSpecialMachinesMIHookContext hook) {
        hook.builder(ItemChargerBlockEntity.ID, ItemChargerBlockEntity.NAME, ItemChargerBlockEntity::new)
                .builtinModel(CableTier.LV.casing, ItemChargerBlockEntity.ID)
                .registrator(type -> {
                    ItemChargerBlockEntity.registerEnergyApi(type);
                    MachineBlockEntity.registerItemApi(type);
                }).registerMachine();
        hook.builder(ConfigurableMixedStorageMachineBlockEntity.ID, ConfigurableMixedStorageMachineBlockEntity.NAME,
                        ConfigurableMixedStorageMachineBlockEntity::new)
                .builtinModel(Casings.CONFIGURABLE_MIXED_STORAGE, ConfigurableMixedStorageMachineBlockEntity.ID,
                        model -> model.front(false).active(false))
                .registrator(type -> {
                    MachineBlockEntity.registerItemApi(type);
                    MachineBlockEntity.registerFluidApi(type);
                }).registerMachine();
        registerMixedHatches(hook);
        hook.builder(NumismaticGeneratorBlockEntity.ID, NumismaticGeneratorBlockEntity.NAME, NumismaticGeneratorBlockEntity::new)
                .builtinModel(MachineCasings.STEEL, NumismaticGeneratorBlockEntity.ID)
                .registrator(MachineBlockEntity::registerItemApi)
                .registrator(GeneratorMachineBlockEntity::registerEnergyApi)
                .registerMachine();
        hook.builder(DetonationChamberCasingBlockEntity.ID, DetonationChamberCasingBlockEntity.NAME,
                        DetonationChamberCasingBlockEntity::new)
                .builtinModel(Casings.DETONATION_CHAMBER_CASING, DetonationChamberCasingBlockEntity.ID,
                        model -> model.front(false).side(false).top(false).active(false))
                .creator(DetonationChamberCasingBlock::new)
                .registerMachine();
    }

    /** Апстрим: {@code fun multiblockMachines(hook)}. */
    public static void multiblockMachines(MultiblockMachinesMIHookContext hook) {
        hook.builder(ArboreousGreenhouseBlockEntity.ID, ArboreousGreenhouseBlockEntity.NAME,
                        ArboreousGreenhouseBlockEntity::new)
                .builtinModel(MachineCasings.HEATPROOF, ArboreousGreenhouseBlockEntity.ID)
                .gui(SteamMode.ELECTRIC_ONLY, YAIRecipeTypes.ARBOREOUS_GREENHOUSE, gui -> {
                    gui.slots(slots -> {
                        slots.itemInput(56, 26);
                        slots.fluidInput(56, 44);
                        slots.itemOutputs(102, 26, 2, 2);
                    });
                    gui.progressBar(76, 33, "extract");
                })
                .registerRecipeCategory()
                .registerMachine();

        hook.builder(DragonSiphonBlockEntity.ID, DragonSiphonBlockEntity.NAME, DragonSiphonBlockEntity::new)
                .builtinModel(MachineCasings.STEEL, DragonSiphonBlockEntity.ID)
                .registerMultiblockShape(DragonSiphonBlockEntity.SHAPE)
                .gui(SteamMode.NEITHER, YAIRecipeTypes.DRAGON_EGG_ENERGY_SIPHON, gui -> {
                    gui.slots(slots -> {
                        slots.itemInput(40, 35);
                        slots.fluidInput(58, 35);
                        slots.fluidOutput(104, 35);
                    });
                    gui.progressBar(80, 33, "extract");
                })
                .registerRecipeCategory()
                .registerMachine();

        hook.builder(PulseDetonationGeneratorBlockEntity.ID, PulseDetonationGeneratorBlockEntity.NAME,
                        PulseDetonationGeneratorBlockEntity::new)
                .builtinModel(MachineCasings.TITANIUM_PIPE, PulseDetonationGeneratorBlockEntity.ID)
                .registerMultiblockShape(PulseDetonationGeneratorBlockEntity.SHAPE)
                .gui(SteamMode.NEITHER, YAIRecipeTypes.PULSE_DETONATION_GENERATOR, gui -> {
                    gui.slots(slots -> {
                        slots.itemInput(38, 35);
                        slots.fluidInput(56, 35);
                        slots.fluidOutput(102, 35);
                    });
                    gui.progressBar(77, 33, "yai_explode");
                })
                .registerRecipeCategory()
                .registerMachine();
        hook.builder(FlightPylonBlockEntity.ID, FlightPylonBlockEntity.NAME, FlightPylonBlockEntity::new)
                .builtinModel(MachineCasings.STEEL, FlightPylonBlockEntity.ID).registerMachine();
        hook.builder(LargeStorageUnitBlockEntity.ID, LargeStorageUnitBlockEntity.NAME, LargeStorageUnitBlockEntity::new)
                .builtinModel(Casings.BATTERY_ALLOY_MACHINE_CASING, LargeStorageUnitBlockEntity.ID).registerMachine();
        hook.builder(NuclearRodIrradiatorBlockEntity.ID, NuclearRodIrradiatorBlockEntity.NAME, NuclearRodIrradiatorBlockEntity::new)
                .builtinModel(MachineCasings.NUCLEAR, NuclearRodIrradiatorBlockEntity.ID)
                .registerMultiblockShape(NuclearRodIrradiatorBlockEntity.SHAPE).registerMachine();
    }

    private static void registerMixedHatches(SingleBlockSpecialMachinesMIHookContext hook) {
        registerMixedHatch(hook, "Bronze", "bronze", MachineCasings.BRONZE, 1, 4_000,
                new SlotPositions.Builder().addSlot(62, 40).build(),
                new SlotPositions.Builder().addSlot(98, 40).build(), 166);
        registerMixedHatch(hook, "Steel", "steel", MachineCasings.STEEL, 2, 16_000,
                new SlotPositions.Builder().addSlots(62, 30, 1, 2).build(),
                new SlotPositions.Builder().addSlot(98, 39).build(), 166);
        registerMixedHatch(hook, "Advanced", "advanced", CableTier.MV.casing, 4, 64_000,
                new SlotPositions.Builder().addSlots(53, 30, 2, 2).build(),
                new SlotPositions.Builder().addSlot(107, 39).build(), 166);
        registerMixedHatch(hook, "Turbo", "turbo", CableTier.HV.casing, 9, 256_000,
                new SlotPositions.Builder().addSlots(44, 21, 3, 3).build(),
                new SlotPositions.Builder().addSlot(116, 39).build(), 176);
        registerMixedHatch(hook, "Highly Advanced", "highly_advanced", CableTier.EV.casing, 15, 1_024_000,
                new SlotPositions.Builder().addSlots(26, 28, 5, 3).build(),
                new SlotPositions.Builder().addSlot(134, 46).build(), 176);
    }

    private static void registerMixedHatch(SingleBlockSpecialMachinesMIHookContext hook,
            String englishPrefix, String prefix, MachineCasing casing, int itemSlotAmount, int fluidSlotCapacity,
            SlotPositions itemPositions, SlotPositions fluidPositions, int backgroundHeight) {
        for (boolean input : new boolean[] {true, false}) {
            String machine = prefix + "_mixed_" + (input ? "input" : "output") + "_hatch";
            String englishName = englishPrefix + " Mixed" + (input ? " Input" : " Output") + " Hatch";
            hook.builder(machine, englishName, bep -> {
                List<ConfigurableItemStack> items = new ArrayList<>();
                for (int i = 0; i < itemSlotAmount; i++) items.add(input ?
                        ConfigurableItemStack.standardInputSlot() : ConfigurableItemStack.standardOutputSlot());
                List<ConfigurableFluidStack> fluids = List.of(input ?
                        ConfigurableFluidStack.standardInputSlot(fluidSlotCapacity) :
                        ConfigurableFluidStack.standardOutputSlot(fluidSlotCapacity));
                return new MixedHatch(bep, machine, input, !prefix.equals("bronze"),
                        new MIInventory(items, fluids, itemPositions, fluidPositions), backgroundHeight);
            }).builtinModel(casing, "mixed_hatch", model -> model.front().side().top(false).active(false)
                    .outputTexture(YAIIds.id("block/overlays/output_mixed")))
                    .registrator(type -> {
                        MachineBlockEntity.registerItemApi(type);
                        MachineBlockEntity.registerFluidApi(type);
                    }).registerMachine();
        }
    }

    public static void hatches(HatchMIHookContext hook) {
        hook.builder(LargeStorageUnitBlockEntity.ID, LargeStorageUnitBlockEntity.NAME)
                .special(LargeStorageUnitHatch::new, true)
                .builtinModel(Casings.BATTERY_ALLOY_MACHINE_CASING, "large_storage_unit_hatch")
                .registrator(LargeStorageUnitHatch::registerEnergyApi).registerMachine();
        hook.builder(ItemTrashCanHatch.ID, ItemTrashCanHatch.NAME)
                .special(ItemTrashCanHatch::new)
                .builtinModel(MachineCasings.STEEL, ItemTrashCanHatch.ID)
                .registrator(MachineBlockEntity::registerItemApi).registerMachine();
        hook.builder(FluidTrashCanHatch.ID, FluidTrashCanHatch.NAME)
                .special(FluidTrashCanHatch::new)
                .builtinModel(MachineCasings.STEEL, FluidTrashCanHatch.ID)
                .registrator(MachineBlockEntity::registerFluidApi).registerMachine();
    }

    /** Апстрим: {@code fun machineCasings(hook)}. */
    public static void machineCasings(MachineCasingsMIHookContext hook) {
        Casings.STEEL_PLATED_END_STONE_BRICKS = hook.registerImitateBlock(
                SPESB_ID, YAIBlocks.STEEL_PLATED_END_STONE_BRICKS);

        Casings.BATTERY_ALLOY_MACHINE_CASING = hook.registerCubeAll(
                "battery_casing", "Battery Casing",
                YAIIds.id("block/battery_casing"));

        Casings.DETONATION_CHAMBER_CASING = hook.registerCubeAll(
                DetonationChamberCasingBlockEntity.ID, DetonationChamberCasingBlockEntity.NAME,
                YAIIds.id("block/" + DetonationChamberCasingBlockEntity.ID));

        Casings.CONFIGURABLE_MIXED_STORAGE = hook.registerCubeBottomTop(
                "configurable_mixed_storage", "Configurable Mixed Storage",
                YAIIds.id("block/casing/configurable_mixed_storage/side"),
                YAIIds.id("block/casing/configurable_mixed_storage/side"),
                YAIIds.id("block/casing/configurable_mixed_storage/top"));
    }

    /** Апстрим: {@code object Casings} (кейсины YAI, регистрируются в {@link #machineCasings}). */
    public static final class Casings {
        public static MachineCasing STEEL_PLATED_END_STONE_BRICKS;
        public static MachineCasing BATTERY_ALLOY_MACHINE_CASING;
        public static MachineCasing DETONATION_CHAMBER_CASING;
        public static MachineCasing CONFIGURABLE_MIXED_STORAGE;

        private Casings() {
        }
    }

    /** Апстрим: {@code fun getMachineFromId(id): Block} (лениво вызывается из формы PDG). */
    public static Block getMachineFromId(String id) {
        Block block = BuiltInRegistries.BLOCK.getValue(YAIIds.id(id));
        if (block == null) {
            throw new IllegalStateException("Failed to get YAI! machine with ID " + id);
        }
        return block;
    }
}
