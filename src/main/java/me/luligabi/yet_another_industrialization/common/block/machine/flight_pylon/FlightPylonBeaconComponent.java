package me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon;

import aztech.modern_industrialization.machines.MachineComponent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class FlightPylonBeaconComponent implements MachineComponent {
    private static final String KEY = "FlightPylonBeacon";
    public boolean enabled = true;
    @Override public void writeNbt(ValueOutput output) { output.putBoolean(KEY, enabled); }
    @Override public void readNbt(ValueInput input, boolean upgrading) { enabled = input.getBooleanOr(KEY, false); }
}
