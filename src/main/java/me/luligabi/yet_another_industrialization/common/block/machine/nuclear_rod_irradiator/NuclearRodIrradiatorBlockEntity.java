package me.luligabi.yet_another_industrialization.common.block.machine.nuclear_rod_irradiator;

import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch;
import aztech.modern_industrialization.machines.components.*;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.machines.multiblocks.*;
import aztech.modern_industrialization.nuclear.NuclearFuel;
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.TextHelper;
import aztech.modern_industrialization.util.Tickable;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper;
import me.luligabi.yet_another_industrialization.common.misc.datamap.IrradiatorNeutronSource;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/** Port of 3641da7; neutron product replacement uses the NeoForge transaction journal. */
public class NuclearRodIrradiatorBlockEntity extends MultiblockMachineBlockEntity
        implements Tickable, MultiblockInventoryComponentHolder, EnergyListComponentHolder {
    public static final String ID = "nuclear_rod_irradiator", NAME = "Nuclear Rod Irradiator";
    private static final SimpleMember CASING = SimpleMember.forBlock(() -> MIMaterials.NUCLEAR_ALLOY.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    private static final SimpleMember CASING_PIPE = SimpleMember.forBlock(() -> MIMaterials.NUCLEAR_ALLOY.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());
    private static final HatchFlags NUCLEAR_HATCH = new HatchFlags.Builder().with(HatchTypes.NUCLEAR_ITEM).build();
    private static final HatchFlags ITEM_HATCH = new HatchFlags.Builder().with(HatchTypes.ITEM_INPUT).build();
    private static final HatchFlags ENERGY_HATCH = new HatchFlags.Builder().with(HatchTypes.ENERGY_INPUT).build();
    private static final List<String> BOTTOM = List.of("_000_", "00000", "00000", "00000", "_000_");
    private static final List<String> MIDDLE = List.of("#####", "#@@@#", "#@@@#", "#@@@#", "#####");
    private static final List<String> TOP = List.of("_000_", "0xxx0", "0xox0", "0xxx0", "_000_");
    public static final ShapeTemplate SHAPE = createShape();
    private static final IrradiatorNeutronSource DEFAULT_SOURCE = new IrradiatorNeutronSource(
            2048, 4096L, Optional.empty(), IrradiatorNeutronSource.Type.NONE, 0f, 1);
    private static ShapeTemplate createShape() {
        ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.NUCLEAR);
        List<List<String>> patterns = List.of(BOTTOM, MIDDLE, MIDDLE, MIDDLE, TOP);
        for (int i = 0; i < patterns.size(); i++) {
            YAIMultiblockHelper.addLayer(builder, i - 2, i, patterns,
                    (c, y) -> c == '@' ? CASING_PIPE : "#xo0".indexOf(c) >= 0 ? CASING : null,
                    (c, y) -> switch (c) { case '0' -> ENERGY_HATCH; case 'x' -> NUCLEAR_HATCH;
                        case 'o' -> ITEM_HATCH; default -> null; }, -2);
        }
        return builder.build();
    }
    private final ActiveShapeComponent activeShape = new ActiveShapeComponent(new ShapeTemplate[]{SHAPE});
    private final MultiblockInventoryComponent inventory = new MultiblockInventoryComponent();
    private final IsActiveComponent isActive = new IsActiveComponent();
    private final RedstoneControlComponent redstoneControl = new RedstoneControlComponent();
    private final List<EnergyComponent> energyInputs = new ArrayList<>();
    private final List<NuclearHatch> nuclearHatches = new ArrayList<>();
    private IrradiatorNeutronSource neutronSource;
    private boolean forceSourceRefresh;

    public NuclearRodIrradiatorBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder(YAIIds.id(ID), false).backgroundHeight(156).build(),
                new OrientationComponent.Params(false, false, false));
        registerComponents(activeShape, isActive, redstoneControl);
        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl));
        registerGuiComponent(new ModularMultiblockGui(40, content -> {
            content.add(statusComponent(), isShapeValid() ? WHITE : RED);
            if (neutronSource != null) {
                var amount = TextHelper.getAmount((double) neutronSource.irradiation());
                content.add(YAI.text().irradiatorNeutronSourceIrradiation(amount.digit(), amount.unit()));
                content.add(YAI.text().irradiatorNeutronSourceType(neutronSource.type().component()));
            }
        }, List::of));
    }
    @Override public void tick() {
        if (level == null || level.isClientSide()) return;
        link();
        if (!shapeValid.shapeValid || !redstoneControl.doAllowNormalOperation(this)) {
            isActive.updateActive(false, this); return;
        }
        if (forceSourceRefresh || level.getGameTime() % 60L == 0) {
            neutronSource = getNeutronSource(); forceSourceRefresh = false;
        }
        if (neutronSource == null) { isActive.updateActive(false, this); return; }
        boolean active = false;
        if (consumeEu(Simulation.SIMULATE)) {
            active = absorb();
            consumeEu(Simulation.ACT);
        }
        isActive.updateActive(active, this);
    }
    @Override protected void onRematch(ShapeMatcher matcher) {
        if (!matcher.isMatchSuccessful()) return;
        inventory.rebuild(matcher); energyInputs.clear(); nuclearHatches.clear();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            hatch.appendEnergyInputs(energyInputs);
            if (hatch instanceof NuclearHatch nuclear) nuclearHatches.add(nuclear);
        }
    }
    @Override protected InteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        InteractionResult result = super.useItemOn(player, hand, face);
        return result.consumesAction() ? result : redstoneControl.onUse(this, player, hand);
    }
    @Override public MultiblockInventoryComponent getMultiblockInventoryComponent() { return inventory; }
    @Override public MIInventory getInventory() { return MIInventory.EMPTY; }
    @Override public List<EnergyComponent> getEnergyComponents() { return energyInputs; }
    @Override public ShapeTemplate getActiveShape() { return SHAPE; }
    @Override public MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive);
    }
    private boolean consumeEu(Simulation simulation) {
        return neutronSource != null && consumeEu(neutronSource.eu(), simulation);
    }
    private boolean consumeEu(long cost, Simulation simulation) {
        long total = 0;
        for (EnergyComponent energy : energyInputs) total += energy.consumeEu(cost - total, simulation);
        return total == cost;
    }
    private boolean absorb() {
        if (neutronSource == null || nuclearHatches.isEmpty()) return false;
        IrradiatorNeutronSource source = neutronSource;
        boolean active = false;
        for (NuclearHatch hatch : nuclearHatches) {
            if (!(hatch.getVariant() instanceof ItemResource resource) || !(resource.getItem() instanceof NuclearFuel fuel)) continue;
            if (source.restrictedTo().isPresent() && !source.restrictedTo().get().map(
                    key -> fuel.builtInRegistryHolder().is(key), tag -> fuel.builtInRegistryHolder().is(tag))) continue;
            active = true;
            ItemStack stack = resource.toStack((int) hatch.getVariantAmount());
            fuel.setRemainingDesintegrations(stack, Math.max(0, fuel.getRemainingDesintegrations(stack) - source.irradiation()));
            ConfigurableItemStack input = hatch.getInventory().getItemStacks().getFirst();
            if (fuel.getRemainingDesintegrations(stack) == 0) {
                try (Transaction tx = Transaction.openRoot()) {
                    input.updateSnapshots(tx); input.setAmount(0); input.setKey(ItemResource.EMPTY);
                    ItemResource product = fuel.getNeutronProduct();
                    if (product == null || hatch.getInventory().itemStorage.insert(product, fuel.getNeutronProductAmount(), tx,
                            ConfigurableItemStack::canPipesExtract, true) == fuel.getNeutronProductAmount()) tx.commit();
                }
            } else input.setKey(ItemResource.of(stack));
        }
        if (level.getGameTime() % source.probabilityCheckCooldown() == 0 && source.type().usesItem
                && ThreadLocalRandom.current().nextFloat() < source.probability()) {
            boolean keep = false;
            for (ConfigurableItemStack input : inventory.getItemInputs()) {
                if (input.isEmpty() || input.getAmount() == 0) continue;
                switch (source.type()) {
                    case CONSUMPTION -> input.decrement(1);
                    case LIFESPAN -> {
                        ItemStack updated = input.toStack();
                        updated.hurtAndBreak(1, (ServerLevel) level, (ServerPlayer) null, item -> {});
                        input.setKey(ItemResource.of(updated)); input.setAmount(updated.getCount());
                    }
                    case NONE -> { }
                }
                keep = !input.getResource().isEmpty() && input.getAmount() != 0;
                break;
            }
            if (!keep) { neutronSource = null; forceSourceRefresh = true; }
        }
        return active;
    }
    private IrradiatorNeutronSource getNeutronSource() {
        if (IrradiatorNeutronSource.all().isEmpty()) return DEFAULT_SOURCE;
        for (ConfigurableItemStack input : inventory.getItemInputs()) {
            if (!input.isEmpty() && input.getAmount() != 0) return IrradiatorNeutronSource.getFor(input.getResource().getItem());
        }
        return null;
    }
    private Component statusComponent() {
        if (!isShapeValid()) return MIText.MultiblockShapeInvalid.text();
        return (isActive.isActive ? MIText.MultiblockStatusActive : MIText.MultiblockShapeValid).text();
    }
}
