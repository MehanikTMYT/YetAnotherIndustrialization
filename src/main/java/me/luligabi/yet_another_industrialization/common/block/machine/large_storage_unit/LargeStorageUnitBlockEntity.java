package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.machines.multiblocks.*;
import aztech.modern_industrialization.util.Tickable;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedActiveShapeComponent;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection;
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes;
import me.luligabi.yet_another_industrialization.common.misc.datamap.LargeStorageUnitTier;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Tiered multiblock battery, translated from the complete 3641da7 original. */
public final class LargeStorageUnitBlockEntity extends MultiblockMachineBlockEntity
        implements Tickable, EnergyListComponentHolder {
    public static final String ID = "large_storage_unit";
    public static final String NAME = "Large Storage Unit";
    public static List<Tier> TIERS = List.of();
    private static ShapeTemplate[] SHAPE_TEMPLATES = new ShapeTemplate[0];
    private static final List<String> BOTTOM_TOP_PATTERN = List.of("#####", "#@@@#", "#@@@#", "#@@@#", "#####");
    private static final List<String> CORE_EDGE_PATTERN = List.of("#@@@#", "@HxH@", "@xxx@", "@HxH@", "#@@@#");
    private static final List<String> CORE_MIDDLE_PATTERN = List.of("#@@@#", "@xxx@", "@xxx@", "@xxx@", "#@@@#");
    private static final HatchFlags HATCHES = new HatchFlags.Builder().with(
            YAIHatchTypes.LARGE_STORAGE_UNIT_INPUT, YAIHatchTypes.LARGE_STORAGE_UNIT_OUTPUT).build();
    private static final SimpleMember CASING_MEMBER = SimpleMember.forBlock(() -> YAIBlocks.BATTERY_CASING.get());
    private final SuppliedActiveShapeComponent activeTier = new SuppliedActiveShapeComponent(() -> SHAPE_TEMPLATES);
    private final ChargingSlotComponent chargingSlot = new ChargingSlotComponent();
    public final EnergyComponent energy = new EnergyComponent(this, () -> getTier() == null ? 0L : getTier().capacity());
    public final LSUEnergyStorage insertable = new LSUEnergyStorage(true);
    public final LSUEnergyStorage extractable = new LSUEnergyStorage(false);
    private long oldEu;

    public LargeStorageUnitBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder(YAIIds.id(ID), false).backgroundHeight(156).build(),
                new OrientationComponent.Params(false, false, false));
        registerComponents(activeTier, chargingSlot, energy);
        registerGuiComponent(new LargeStorageUnitGui(() -> shapeValid.shapeValid,
                energy::getEu, energy::getCapacity, insertable::getCurrentAmount, extractable::getCurrentAmount));
        registerGuiComponent(new SuppliedShapeSelection(new ShapeSelection.Behavior() {
            @Override public void handleClick(int line, int delta) {
                activeTier.incrementShape(LargeStorageUnitBlockEntity.this, delta);
            }
            @Override public int getCurrentIndex(int line) { return activeTier.getActiveShape(); }
        }, this::getTierInfo));
        registerGuiComponent(new ChargingSlot(this, chargingSlot));
    }

    public record Tier(Identifier blockId, long capacity, CableTier cableTier, String translationKey) {
        public SimpleMember blockMember() { return SimpleMember.forBlockId(blockId); }
        public SimpleMember hullMember() { return SimpleMember.forBlockId(LargeStorageUnitTier.getHull(blockId, cableTier)); }
        public Component getDisplayName() { return Component.translatable(translationKey); }
    }

    public static void initTiers() {
        List<Tier> tiers = new ArrayList<>();
        LargeStorageUnitTier.all().forEach((block, tier) -> tiers.add(tier.toRegisteredTier(block)));
        tiers.sort(Comparator.comparingLong(Tier::capacity));
        TIERS = List.copyOf(tiers);
        SHAPE_TEMPLATES = new ShapeTemplate[TIERS.size()];
        for (int i = 0; i < TIERS.size(); i++) {
            Tier tier = TIERS.get(i);
            var builder = new ShapeTemplate.Builder(YAIMachines.Casings.BATTERY_ALLOY_MACHINE_CASING);
            addLayer(builder, -1, BOTTOM_TOP_PATTERN, tier);
            addLayer(builder, 0, CORE_EDGE_PATTERN, tier);
            addLayer(builder, 1, CORE_MIDDLE_PATTERN, tier);
            addLayer(builder, 2, CORE_EDGE_PATTERN, tier);
            addLayer(builder, 3, BOTTOM_TOP_PATTERN, tier);
            SHAPE_TEMPLATES[i] = builder.build();
        }
        // javap MI26 MultiblockCategory.buildRecipes reads this legacy-named MI data registry.
        // It serves fleet EMI's viewer abstraction and has no external REI dependency.
        ReiMachineRecipes.multiblockShapes.removeIf(shape -> shape.machine().equals(YAIIds.id(ID)));
        for (int i = 0; i < SHAPE_TEMPLATES.length; i++) {
            ReiMachineRecipes.registerMultiblockShape(YAIIds.id(ID), SHAPE_TEMPLATES[i], Integer.toString(i));
        }
    }

    private static void addLayer(ShapeTemplate.Builder builder, int y, List<String> pattern, Tier tier) {
        SimpleMember battery = tier.blockMember();
        SimpleMember hull = tier.hullMember();
        for (int z = 0; z < pattern.size(); z++) {
            String row = pattern.get(z);
            for (int x = 0; x < row.length(); x++) {
                SimpleMember member = switch (row.charAt(x)) {
                    case '#' -> CASING_MEMBER;
                    case '@' -> YAIMultiblockHelper.GLASS_MEMBER;
                    case 'x' -> battery;
                    case 'H' -> hull;
                    default -> null;
                };
                if (member == null) continue;
                if (member == CASING_MEMBER) builder.add(x - 2, y, z, member, HATCHES);
                else builder.add(x - 2, y, z, member);
            }
        }
    }

    @Override public void tick() {
        if (level == null || level.isClientSide() || SHAPE_TEMPLATES.length == 0) return;
        link();
        setChanged();
        insertable.currentAmount = 0;
        extractable.currentAmount = 0;
        if (energy.getEu() != oldEu) {
            oldEu = energy.getEu();
            sync(false);
        }
        chargingSlot.chargeItem(extractable);
    }
    @Override public void onRematch(ShapeMatcher matcher) {
        if (!matcher.isMatchSuccessful()) return;
        invalidateCapabilities();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof LargeStorageUnitHatch batteryHatch) batteryHatch.setController(this);
        }
    }
    @Override public InteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        EnergyHandler item = ItemAccess.forPlayerInteraction(player, hand).getCapability(EnergyApi.ITEM);
        if (item == null) return super.useItemOn(player, hand, face);
        if (!player.level().isClientSide()) {
            // The 26 API handles stack updates transactionally through ItemAccess.
            boolean charged = transferRepeated(extractable, item);
            if (!charged) transferRepeated(item, insertable);
        }
        return InteractionResult.SUCCESS;
    }
    static boolean transferRepeated(EnergyHandler from, EnergyHandler to) {
        boolean moved = false;
        for (int i = 0; i < 10000; i++) {
            if (EnergyHandlerUtil.move(from, to, Integer.MAX_VALUE, null) == 0) break;
            moved = true;
        }
        return moved;
    }
    @Override public ShapeTemplate getActiveShape() {
        return SHAPE_TEMPLATES.length == 0 ? null : SHAPE_TEMPLATES[tierIndex()];
    }
    @Override public ShapeTemplate getBigShape() { return SHAPE_TEMPLATES.length == 0 ? null : SHAPE_TEMPLATES[0]; }
    @Override public MIInventory getInventory() { return MIInventory.EMPTY; }
    @Override public List<EnergyComponent> getEnergyComponents() { return shapeValid.shapeValid ? List.of(energy) : List.of(); }
    @Override public MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(shapeValid.shapeValid);
    }
    @Override public boolean hasComparatorOutput() { return true; }
    @Override public int getComparatorOutput() {
        return energy.getCapacity() == 0 ? 0 : Mth.floor((double) energy.getEu() / energy.getCapacity() * 14) + (energy.getEu() > 0 ? 1 : 0);
    }
    private int tierIndex() { return Math.max(0, Math.min(activeTier.getActiveShape(), TIERS.size() - 1)); }
    private Tier getTier() { return TIERS.isEmpty() ? null : TIERS.get(tierIndex()); }
    private ShapeSelection.LineInfo getTierInfo() {
        return new ShapeSelection.LineInfo(TIERS.stream().map(Tier::getDisplayName).toList(), true);
    }
    private boolean canTransfer(CableTier cable) { return getTier() != null && cable.eu <= getTier().cableTier().eu; }

    /** Directional energy view. Both the energy component and I/O counter roll back together. */
    public final class LSUEnergyStorage extends SnapshotJournal<Long> implements MIEnergyStorage {
        private final boolean input;
        private final MIEnergyStorage delegate;
        private long currentAmount;
        private LSUEnergyStorage(boolean input) {
            this.input = input;
            delegate = input ? energy.buildInsertable(LargeStorageUnitBlockEntity.this::canTransfer)
                    : energy.buildExtractable(LargeStorageUnitBlockEntity.this::canTransfer);
        }
        public long getCurrentAmount() { return currentAmount; }
        @Override protected Long createSnapshot() { return currentAmount; }
        @Override protected void revertToSnapshot(Long snapshot) { currentAmount = snapshot; }
        @Override public int insert(int amount, TransactionContext transaction) {
            if (!input) return 0;
            int moved = delegate.insert(amount, transaction);
            updateSnapshots(transaction);
            currentAmount += moved;
            return moved;
        }
        @Override public int extract(int amount, TransactionContext transaction) {
            if (input) return 0;
            int moved = delegate.extract(amount, transaction);
            updateSnapshots(transaction);
            currentAmount += moved;
            return moved;
        }
        @Override public long getAmountAsLong() { return energy.getEu(); }
        @Override public long getCapacityAsLong() { return energy.getCapacity(); }
        @Override public boolean supportsInsertion() { return input; }
        @Override public boolean supportsExtraction() { return !input; }
        @Override public boolean canConnect(CableTier cable) { return canTransfer(cable); }
    }
}
