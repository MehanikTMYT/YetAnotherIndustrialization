package me.luligabi.yet_another_industrialization.common.util;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
public enum EmptyEnergyAccess implements MIEnergyStorage, EnergyAccess {
    INSTANCE;
    public int insert(int amount, TransactionContext transaction) { return 0; }
    public int extract(int amount, TransactionContext transaction) { return 0; }
    public long getAmountAsLong() { return 0; }
    public long getEu() { return 0; }
    public long getCapacity() { return 0; }
    public long getCapacityAsLong() { return 0; }
    public boolean supportsExtraction() { return true; }
    public boolean supportsInsertion() { return false; }
    public boolean canConnect(CableTier tier) { return true; }
}
