package me.luligabi.yet_another_industrialization.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.tesseract.neoforge.lang.annotation.LangKey;
import net.swedz.tesseract.neoforge.lang.annotation.Parsed;
import net.swedz.tesseract.neoforge.lang.annotation.WithStyle;

@SuppressWarnings("unused")
public interface YAIText {

    /** Machine Diagnoser */
    @WithStyle("gray")
    @LangKey(text = {"%s's Diagnosis (hover for details):\n"})
    MutableComponent diagnoserTitle(@WithStyle("highlight") Component machine);

    @WithStyle("green_neutron")
    @LangKey(text = {"No issues found with %s"})
    MutableComponent diagnoserTitleNone(@WithStyle("highlight") Component machine);

    @LangKey(text = {"Missing Steam"})
    MutableComponent diagnoserNoSteam();
    @LangKey(text = {"This machine requires steam to function"})
    MutableComponent diagnoserNoSteamDescription();

    @LangKey(text = {"Missing Energy"})
    MutableComponent diagnoserNoEnergy();
    @LangKey(text = {"This machine requires energy to function"})
    MutableComponent diagnoserNoEnergyDescription();
    @LangKey(text = {"This machine might require energy to function"})
    MutableComponent diagnoserNoEnergyWarningDescription();

    @LangKey(text = {"Missing Energy Input"})
    MutableComponent diagnoserNoEnergyInput();
    @LangKey(text = {"This machine should have an energy input hatch."})
    MutableComponent diagnoserNoEnergyInputDescription();

    @LangKey(text = {"Missing Energy Output"})
    MutableComponent diagnoserNoEnergyOutput();
    @LangKey(text = {"This machine should have an energy output hatch."})
    MutableComponent diagnoserNoEnergyOutputDescription();

    @LangKey(text = {"No available recipes"})
    MutableComponent diagnoserNoRecipe();
    @LangKey(text = {"There are no available recipes using the current ingredients. Check if you're missing something."})
    MutableComponent diagnoserNoRecipeDescription();

    @LangKey(text = {"Recipe available, but not usable"})
    MutableComponent diagnoserBannedRecipe();
    @LangKey(text = {"A recipe is available, but unusable. This most likely means that: \n\n- You need more Upgrades to use this recipe\n- The recipe requires a higher multiblock tier (i.e. Electric Blast Furnace's Kanthal Coils)"})
    MutableComponent diagnoserBannedRecipeDescription();

    @LangKey(text = {"Can't take Item Input"})
    MutableComponent diagnoserCantTakeItemInput();
    @LangKey(text = {"This machine either has nowhere to take items from or they're insufficient to repeat the current recipe."})
    MutableComponent diagnoserCantTakeItemInputDescription();

    @LangKey(text = {"Can't take Fluid Input"})
    MutableComponent diagnoserCantTakeFluidInput();
    @LangKey(text = {"The machine either has nowhere to take fluids from or they're insufficient to repeat the current recipe."})
    MutableComponent diagnoserCantTakeFluidInputDescription();

    @LangKey(text = {"Can't put Item Output"})
    MutableComponent diagnoserCantPutItemOutput();
    @LangKey(text = {"The machine has nowhere to put item outputs in. This might mean that: \n\n- There's no available slot\n- The slot's capacity isn't enough to store the output"})
    MutableComponent diagnoserCantPutItemOutputDescription();

    @LangKey(text = {"Can't put Fluid Output"})
    MutableComponent diagnoserCantPutFluidOutput();
    @LangKey(text = {"The machine has nowhere to put fluids outputs in. This might mean that: \n\n- There's no available slot\n- The slot's capacity isn't enough to store the output"})
    MutableComponent diagnoserCantPutFluidOutputDescription();

    @LangKey(text = {"Unmet Recipe Conditions"})
    MutableComponent diagnoserUnmetCondition();
    @LangKey(text = {"The current recipe has condition(s) that are not being met."})
    MutableComponent diagnoserUnmetConditionDescription();

    @LangKey(text = {"Invalid Multiblock Shape"})
    MutableComponent diagnoserInvalidMultiblockShape();
    @LangKey(text = {"The multiblock is misbuilt. Hold a Wrench to preview the correct shape. Also check:\n\n- If the multiblock has tiers, select the right one on the GUI;\n- Some multiblocks, like the Pressurizer, are picky about where each hatch type is placed."})
    MutableComponent diagnoserInvalidMultiblockShapeDescription();

    @LangKey(text = {"Redstone Blocked"})
    MutableComponent diagnoserRedstoneBlocked();
    @LangKey(text = {"The machine's Redstone Control Module requires the opposite signal to allow operation.\nInvert the signal or remove the module to proceed."})
    MutableComponent diagnoserRedstoneBlockedDescription();

    @LangKey(text = {"Missing Large Tank Hatches"})
    MutableComponent diagnoserNoLargeTankHatch();
    @LangKey(text = {"Large Tanks require Large Tank Hatches"})
    MutableComponent diagnoserNoLargeTankHatchDescription();

    @LangKey(text = {"Unsupported machine"})
    MutableComponent diagnoserUnsupported();
    @LangKey(text = {"Sorry, I can't diagnose any of this machine's specific behaviors :(\n\nI really need to beat Statech Industry..."})
    MutableComponent diagnoserUnsupportedDescription();

    @WithStyle("gray")
    @LangKey(text = {"Diagnoses common issues with machines"})
    MutableComponent diagnoserTooltip1();

    /** Machine Remover */
    @WithStyle("machine_remover")
    @LangKey(text = {"Insufficient Energy"})
    MutableComponent machineRemoverInsufficientEnergy();

    @WithStyle("machine_remover")
    @LangKey(text = {"Too large to remove!"})
    MutableComponent machineRemoverTooLarge();

    @WithStyle("machine_remover")
    @LangKey(text = {"Sorry, can't remove this type of machine :("})
    MutableComponent machineRemoverBanned();

    @WithStyle("gray")
    @LangKey(text = {"Removes machines in a single click"})
    MutableComponent machineRemoverTooltip1();

    @WithStyle("gray")
    @LangKey(text = {"Single Block Remove Cost: %s"})
    MutableComponent machineRemoverTooltip2(@WithStyle("highlight") Component cost);

    @WithStyle("gray")
    @LangKey(text = {"Multiblock Remove Cost: %s"})
    MutableComponent machineRemoverTooltip3(@WithStyle("highlight") String cost);

    /** Slot Locker */
    @WithStyle("gray")
    @LangKey(text = {"Set Mode to %s"})
    MutableComponent slotLockerModeChange(@WithStyle("highlight") Component mode);

    @WithStyle("gray")
    @LangKey(text = {"Mode: %s"})
    MutableComponent slotLockerModePrefix(@WithStyle("highlight") Component mode);

    @LangKey(text = {"Lock Item Slots"})
    MutableComponent slotLockerModeLockItem();

    @LangKey(text = {"Lock Fluid Slots"})
    MutableComponent slotLockerModeLockFluid();

    @LangKey(text = {"Lock Both"})
    MutableComponent slotLockerModeLockBoth();

    @LangKey(text = {"Unlock Item Slots"})
    MutableComponent slotLockerModeUnlockItem();

    @LangKey(text = {"Unlock Fluid Slots"})
    MutableComponent slotLockerModeUnlockFluid();

    @LangKey(text = {"Unlock Both"})
    MutableComponent slotLockerModeUnlockBoth();

    @WithStyle("gray")
    @LangKey(text = {"%s | %s"})
    MutableComponent slotLockerTooltip1(Component item, Component fluid);

    @WithStyle("gray")
    @LangKey(text = {"Drag an item and a fluid from your recipe viewer to select them as the desired lock"})
    MutableComponent slotLockerTooltip2();

    @LangKey(text = {"Sneak + Scroll"})
    MutableComponent slotLockerTooltip3Instruction();

    @WithStyle("gray")
    @LangKey(text = {"%s to change mode"})
    MutableComponent slotLockerTooltip3Suffix(@WithStyle("highlight") Component key);

    /** Industrialist's Goggles */
    @WithStyle("gray")
    @LangKey(text = {"Previews multiblock shapes without the need to hold a wrench"})
    MutableComponent gogglesTooltip1();

    @WithStyle("gray")
    @LangKey(text = {"Press %s to toggle"})
    MutableComponent gogglesTooltip2(
        @Parsed("keybind") @WithStyle("highlight") String keybind
    );

    /** Arboreous Greenhouse */
    @WithStyle("gray")
    @LangKey(text = {"Requires %s soil"})
    MutableComponent arboreousGreenhouseTierTooltip(
        @WithStyle("highlight") Component soil
    );

    /** Numismatic Generator */
    @LangKey(text = {"Numismatic Currencies"})
    MutableComponent numismaticCurrencies();

    @LangKey(text = {"EU per item: %d"})
    MutableComponent euPerItem(long eu);

    /** Dragon Egg Siphon */
    @WithStyle("gray")
    @LangKey(text = {"Generates %s"})
    MutableComponent energyGenerationTooltip(
        @WithStyle("highlight") Component energy
    );

    /** Flight Pylon */
    @WithStyle("gray")
    @LangKey(text = {"Click to toggle beacon visibility."})
    MutableComponent flightPylonBeaconToggle();

    @LangKey(text = {"Range: %d Blocks"})
    MutableComponent flightPylonRange(int range);

    @LangKey(text = {"Consumes: %s"})
    MutableComponent flightPylonEnergy(Component eu);

    @LangKey(text = {"Flight Pylon Tiers"})
    MutableComponent flightPylonTiers();

    @LangKey(text = {"Tiny"})
    MutableComponent flightPylonTierTiny();

    @LangKey(text = {"Small"})
    MutableComponent flightPylonTierSmall();

    @LangKey(text = {"Medium"})
    MutableComponent flightPylonTierMedium();

    @LangKey(text = {"Large"})
    MutableComponent flightPylonTierLarge();

    @LangKey(text = {"Huge"})
    MutableComponent flightPylonTierHuge();



    /** Large Storage Unit */
    @LangKey(text = {"%s / %s %sEU (%s)"})
    MutableComponent largeStorageUnitCapacity(
        String eu,
        String maxEu,
        String unit,
        Component percentage
    );

    @LangKey(text = {"I/O: %s"})
    MutableComponent largeStorageUnitEnergyIO(
        Component eu
    );

    @WithStyle("red")
    @LangKey(text = {"Not linked to a Large Storage Unit"})
    MutableComponent noLargeStorageUnit();

    @WithStyle("gray")
    @LangKey(text = {"Place an item to charge here!"})
    MutableComponent chargingSlotTooltip();

    @LangKey(text = {"Large Storage Unit Tiers"})
    MutableComponent largeStorageUnitTiers();

    @LangKey(text = {"Capacity: %s %sEU"})
    MutableComponent largeStorageUnitTierCapacity(String digit, String unit);

    @WithStyle("gray")
    @LangKey(text = {"Can I/O to its voltage and lower"})
    MutableComponent largeStorageUnitTooltip1();

    @WithStyle("gray")
    @LangKey(text = {"i.e. At HV, it's capable of interacting with HV, MV, and LV!"})
    MutableComponent largeStorageUnitTooltip2();

    @LangKey(text = {"Ultimate"})
    MutableComponent largeStorageUnitUltimateTier();

    /** Nuclear Rod Irradiator */
    @WithStyle("gray")
    @LangKey(text = {"Depletes nuclear fuel rods at the cost of energy and a neutron source"})
    MutableComponent nuclearRodIrradiatorTooltip();

    @WithStyle("gray")
    @LangKey(text = {"Lifespan: %s / %s"})
    MutableComponent alwaysVisibleLifespan(
        @WithStyle("highlight") int remaining,
        @WithStyle("highlight") int total
    );

    @LangKey(text = {"Irradiator Neutron Sources"})
    MutableComponent irradiatorNeutronSources();

    @LangKey(text = {"Irradiation: %s%s N/t"})
    MutableComponent irradiatorNeutronSourceIrradiation(
        String amount,
        String unit
    );

    @LangKey(text = {"Type: %s"})
    MutableComponent irradiatorNeutronSourceType(
        Component type
    );

    @LangKey(text = {"None"})
    MutableComponent irradiatorNeutronSourceTypeNone();

    @LangKey(text = {"Source item is required, but will never be touched by machine."})
    MutableComponent irradiatorNeutronSourceTypeNoneDescription(
        @WithStyle("highlight") String chance,
        @WithStyle("highlight") String time
    );

    @LangKey(text = {"Consumption"})
    MutableComponent irradiatorNeutronSourceTypeConsumption();

    @LangKey(text = {"Source item has a %s chance to be consumed every %s seconds"})
    MutableComponent irradiatorNeutronSourceTypeConsumptionDescription(
        @WithStyle("highlight") String chance,
        @WithStyle("highlight") String time
    );

    @LangKey(text = {"Lifespan"})
    MutableComponent irradiatorNeutronSourceTypeLifespan();

    @LangKey(text = {"Source item's lifespan has a %s chance to decrease every %s seconds"})
    MutableComponent irradiatorNeutronSourceTypeLifespanDescription(
        @WithStyle("highlight") String chance,
        @WithStyle("highlight") String time
    );

    @LangKey(text = {"Only irradiates %s"})
    MutableComponent irradiatorNeutronSourceRestrictedIrradiation(
        @WithStyle("highlight") Component irradiated
    );

    @LangKey(text = {"Irradiate %s absorption every tick"})
    MutableComponent irradiatorNeutronSourceIrradiates(
        @WithStyle("highlight") String absorption
    );

    /** Hatch Names */
    @LangKey(text = {"Mixed Input Hatch"})
    MutableComponent mixedInputHatch();
    @LangKey(text = {"Mixed Output Hatch"})
    MutableComponent mixedOutputHatch();

    /** Misc. Item tooltips */
    @WithStyle("gray_italic")
    @LangKey(text = {"Pronounced as \"YAY\"!"})
    MutableComponent guidebookTooltip();

    @WithStyle("gray_italic")
    @LangKey(text = {"The Brazilian Spirit"})
    MutableComponent cachacaTooltip();

    @WithStyle("gray_italic")
    @LangKey(text = {"Vibe cooking is my passion"})
    MutableComponent aiSlopTooltip();

    @WithStyle("gray_italic")
    @LangKey(text = {"The buns are barely capable of holding it all together"})
    MutableComponent ultradenseMetalBallBurgerTooltip();

    @LangKey(text = {"Sneak + Right-click"}, key = "sneak_rc_activate_1")
    MutableComponent sneakRCActivate1();

    @WithStyle("gray")
    @LangKey(text = {"%s to activate"}, key = "sneak_rc_activate_2")
    MutableComponent sneakRCActivate2(@WithStyle("highlight") Component key);

    @WithStyle("gray")
    @LangKey(text = {"Enabled: %s"})
    MutableComponent enabledPrefix(Component state);

    @LangKey(text = {"Input: %s"})
    MutableComponent input(
        Component input
    );

    @LangKey(text = {"Output: %s"})
    MutableComponent output(
        Component output
    );

    @WithStyle("gray")
    @LangKey(text = {"%s | %s"})
    MutableComponent enabledPrefixAlt(
        @WithStyle("highlight") Component name,
        Component state
    );

    @WithStyle("gray")
    @LangKey(text = {"None"})
    MutableComponent none();

}
