package me.luligabi.yet_another_industrialization;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import me.luligabi.yet_another_industrialization.common.util.EnergyComponentStorage;
import me.luligabi.yet_another_industrialization.common.util.EmptyEnergyAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;
public final class FoundationChecks {
    private static int checks;
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    private static final class Owner extends BlockEntity {
        int changes;
        Owner() { super(null, BlockPos.ZERO, null); }
        @Override public boolean isValidBlockState(BlockState state) { return true; }
        @Override public void setChanged() { changes++; }
    }
    public static void main(String[] args) {
        Owner owner = new Owner();
        EnergyComponent component = new EnergyComponent(owner, 5_000_000_000L);
        EnergyComponentStorage storage = new EnergyComponentStorage(() -> component);
        check(storage.getCapacityAsLong() == 5_000_000_000L, "64-bit capacity");
        try (var tx = Transaction.openRoot()) {
            check(storage.insert(100, tx) == 100, "insert");
            check(storage.getAmountAsLong() == 100, "pending insert visible");
        }
        check(storage.getAmountAsLong() == 0 && owner.changes == 0, "aborted insert rollback");
        try (var tx = Transaction.openRoot()) { storage.insert(200, tx); tx.commit(); }
        check(storage.getAmountAsLong() == 200 && owner.changes == 1, "committed insert saves owner");
        try (var root = Transaction.openRoot()) {
            storage.insert(100, root);
            try (var nested = Transaction.open(root)) { storage.extract(50, nested); }
            check(storage.getAmountAsLong() == 300, "nested rollback");
            try (var nested = Transaction.open(root)) { storage.extract(50, nested); nested.commit(); }
            check(storage.getAmountAsLong() == 250, "nested commit visible");
        }
        check(storage.getAmountAsLong() == 200, "root rollback undoes committed child");
        try (var tx = Transaction.openRoot()) { check(storage.extract(1000, tx) == 200, "extraction bound"); tx.commit(); }
        check(storage.getAmountAsLong() == 0, "committed extraction");
        try (var tx = Transaction.openRoot()) {
            storage.insert(Integer.MAX_VALUE, tx); storage.insert(Integer.MAX_VALUE, tx);
            check(storage.getAmountAsLong() == 4_294_967_294L, "no signed int overflow");
            check(storage.insert(Integer.MAX_VALUE, tx) == 705_032_706, "capacity bound");
            tx.commit();
        }
        check(storage.getAmountAsLong() == 5_000_000_000L, "large stored amount");
        try (var tx = Transaction.openRoot()) {
            boolean rejected = false;
            try { storage.insert(-1, tx); } catch (IllegalArgumentException expected) { rejected = true; }
            check(rejected, "reject negative insertion");
        }
        try (var tx = Transaction.openRoot()) {
            check(EmptyEnergyAccess.INSTANCE.insert(100, tx) == 0, "empty insertion");
            check(EmptyEnergyAccess.INSTANCE.extract(100, tx) == 0, "empty extraction");
        }
        check(EmptyEnergyAccess.INSTANCE.supportsExtraction() && !EmptyEnergyAccess.INSTANCE.supportsInsertion(), "empty cable connection direction");
        System.out.println("YAI_FOUNDATION_PASS checks=" + checks);
    }

    public static void checkComponents(net.minecraft.core.RegistryAccess registries) {
        var ops = registries.createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE);
        var empty = me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData.EMPTY;
        var codec = me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData.CODEC;
        check(codec.parse(ops, new com.google.gson.JsonObject()).getOrThrow().equals(empty), "legacy missing fields defaults");
        var original = empty.withItem(net.neoforged.neoforge.transfer.item.ItemResource.of(net.minecraft.world.item.Items.DIAMOND))
            .withFluid(net.neoforged.neoforge.transfer.fluid.FluidResource.of(net.minecraft.world.level.material.Fluids.WATER));
        check(empty.isEmpty(), "shared default is immutable");
        for (var mode : me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData.Mode.values()) {
            var data = original.withMode(mode);
            var json = codec.encodeStart(ops, data).getOrThrow();
            check(codec.parse(ops, json).getOrThrow().equals(data), "persistent round trip " + mode);
            var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), registries);
            try {
                var stream = me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData.STREAM_CODEC;
                stream.encode(buffer, data);
                check(stream.decode(buffer).equals(data), "network round trip " + mode);
                check(buffer.readableBytes() == 0, "network consumes full payload");
            } finally { buffer.release(); }
        }
        var mode = empty.mode();
        for (int i = 0; i < 6; i++) mode = mode.next();
        check(mode == empty.mode(), "mode cycles after six steps");
        var json = codec.encodeStart(ops, empty).getOrThrow();
        check(codec.parse(ops, json).getOrThrow().equals(empty), "empty resources round trip");
        System.out.println("YAI_COMPONENTS_PASS totalChecks=" + checks);
    }
}
