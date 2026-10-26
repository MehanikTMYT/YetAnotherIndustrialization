package me.luligabi.yet_another_industrialization.common.util;
import java.util.function.Supplier;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
/** Both directions share the component's transaction journal, including nested rollback. */
public final class EnergyComponentStorage implements MIEnergyStorage {
    private final Supplier<EnergyComponent> component;
    public EnergyComponentStorage(Supplier<EnergyComponent> component) { this.component = component; }
    public int insert(int amount, TransactionContext transaction) {
        return component.get().buildInsertable(tier -> true).insert(amount, transaction);
    }
    public int extract(int amount, TransactionContext transaction) {
        return component.get().buildExtractable(tier -> true).extract(amount, transaction);
    }
    public long getAmountAsLong() { return component.get().getEu(); }
    public long getCapacityAsLong() { return component.get().getCapacity(); }
    public boolean canConnect(CableTier tier) { return true; }
}
