package me.luligabi.yet_another_industrialization.client;

import net.swedz.tesseract.config.annotation.ConfigComment;
import net.swedz.tesseract.config.annotation.ConfigKey;
import net.swedz.tesseract.config.annotation.SubSection;

public interface YAIClientConfig {

    @ConfigKey("multiblock_model")
    @SubSection
    MultiblockModel multiblockModel();

    @ConfigKey("flight_pylon")
    @SubSection
    FlightPylon flightPylon();

    interface MultiblockModel {

        @ConfigKey("disableModelCache")
        @ConfigComment("Disable model cache")
        default boolean disableModelCache() { return false; }

        @ConfigKey("minimalQuads")
        @ConfigComment("Use minimal quads for rendering")
        default boolean minimalQuads() { return false; }

    }

    interface FlightPylon {

        @ConfigKey("disableBeacon")
        @ConfigComment("Disable beacon rendering regardless of pylon's state")
        default boolean disableBeacon() { return false; }

    }

}
