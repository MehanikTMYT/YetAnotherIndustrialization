package me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon;

import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.machines.multiblocks.*;
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.TextHelper;
import aztech.modern_industrialization.util.Tickable;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedActiveShapeComponent;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection;
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox;
import me.luligabi.yet_another_industrialization.common.misc.datamap.FlightPylonTier;
import me.luligabi.yet_another_industrialization.common.misc.effect.YAIEffects;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Faithful port of 3641da7: tiered flight effects, energy cost, redstone and beacon toggle. */
public class FlightPylonBlockEntity extends MultiblockMachineBlockEntity implements Tickable, EnergyListComponentHolder {
    public static final String ID = "flight_pylon", NAME = "Flight Pylon";
    public static List<Tier> TIERS = List.of();
    private static ShapeTemplate[] SHAPE_TEMPLATES = new ShapeTemplate[0];
    private static final String[][] LAYERS = {
        {"_____", "_###_", "_###_", "_###_", "_____"},
        {"_____", "__@__", "_@@@_", "__@__", "_____"},
        {"__@__", "_____", "@___@", "_____", "__@__"},
        {"_____", "_@_@_", "_____", "_@_@_", "_____"},
        {"_____", "_@_@_", "__@__", "_@_@_", "_____"},
        {"_____", "_____", "__@__", "_____", "_____"}
    };
    private static final HatchFlags HATCHES = new HatchFlags.Builder().with(HatchTypes.ENERGY_INPUT).build();
    private static final SimpleMember CASING_MEMBER = SimpleMember.forBlock(() -> MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING).asBlock());
    private final SuppliedActiveShapeComponent activeTier = new SuppliedActiveShapeComponent(() -> SHAPE_TEMPLATES);
    private final RedstoneControlComponent redstoneControl = new RedstoneControlComponent();
    public final IsActiveComponent isActive = new IsActiveComponent();
    public final FlightPylonBeaconComponent beaconComponent = new FlightPylonBeaconComponent();
    private final List<EnergyComponent> energyInputs = new ArrayList<>();

    public FlightPylonBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder(YAIIds.id(ID), false).backgroundHeight(156).build(),
                new OrientationComponent.Params(false, false, false));
        registerComponents(activeTier, isActive, redstoneControl, beaconComponent);
        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl));
        registerGuiComponent(new ModularMultiblockGui(40, gui -> {
            gui.add(statusComponent(), isShapeValid() ? WHITE : RED);
            if (!isActive.isActive || TIERS.isEmpty()) return;
            Tier tier = getTier();
            gui.add(YAI.text().flightPylonRange((int) tier.range()));
            gui.add(YAI.text().flightPylonEnergy(TextHelper.getEuTextTick(tier.eu())));
        }, List::of));
        registerGuiComponent(new SuppliedShapeSelection(new ShapeSelection.Behavior() {
            @Override public void handleClick(int line, int delta) { activeTier.incrementShape(FlightPylonBlockEntity.this, delta); }
            @Override public int getCurrentIndex(int line) { return activeTier.getActiveShape(); }
        }, () -> new ShapeSelection.LineInfo(TIERS.stream().map(Tier::getDisplayName).toList(), true)));
        registerGuiComponent(new ToggleCheckbox(value -> {
            beaconComponent.enabled = value;
            setChanged();
            sync();
        }, () -> beaconComponent.enabled, List.of(YAI.text().flightPylonBeaconToggle())));
    }

    public record Tier(Identifier blockId, double range, long eu, String translationKey, int beaconColor) {
        public Component getDisplayName() { return Component.translatable(translationKey); }
    }
    public static void initTiers() {
        List<Tier> tiers = new ArrayList<>();
        FlightPylonTier.all().forEach((block, tier) -> tiers.add(tier.toRegisteredTier(block)));
        tiers.sort(Comparator.comparingDouble(Tier::range));
        TIERS = List.copyOf(tiers);
        SHAPE_TEMPLATES = new ShapeTemplate[TIERS.size()];
        int[] layers = {0, 1, 2, 3, 3, 4, 5, 5, 5};
        for (int i = 0; i < TIERS.size(); i++) {
            ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.STEEL);
            SimpleMember tierMember = SimpleMember.forBlockId(TIERS.get(i).blockId());
            for (int y = 0; y < layers.length; y++) {
                String[] pattern = LAYERS[layers[y]];
                for (int z = 0; z < pattern.length; z++) for (int x = 0; x < pattern[z].length(); x++) {
                    char c = pattern[z].charAt(x);
                    if (c == '#') builder.add(x - 2, y, z - 1, CASING_MEMBER, HATCHES);
                    else if (c == '@') builder.add(x - 2, y, z - 1, tierMember);
                }
            }
            SHAPE_TEMPLATES[i] = builder.build();
        }
        // MI fleet's EMI MultiblockCategory consumes this internal legacy-named shared registry.
        // This introduces no external recipe-viewer dependency.
        ReiMachineRecipes.multiblockShapes.removeIf(shape -> shape.machine().equals(YAIIds.id(ID)));
        for (int i = 0; i < SHAPE_TEMPLATES.length; i++)
            ReiMachineRecipes.registerMultiblockShape(YAIIds.id(ID), SHAPE_TEMPLATES[i], Integer.toString(i));
    }
    @Override public void tick() {
        if (level == null || level.isClientSide()) return;
        if (TIERS.isEmpty()) { isActive.updateActive(false, this); return; }
        link();
        if (!shapeValid.shapeValid || !redstoneControl.doAllowNormalOperation(this)) {
            isActive.updateActive(false, this); return;
        }
        boolean active = consumeEu(Simulation.SIMULATE);
        if (active) {
            if (level.getGameTime() % 120L == 0) applyEffect();
            consumeEu(Simulation.ACT);
        }
        isActive.updateActive(active, this);
    }
    @Override protected void onRematch(ShapeMatcher matcher) {
        if (!matcher.isMatchSuccessful()) return;
        energyInputs.clear();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) hatch.appendEnergyInputs(energyInputs);
    }
    @Override protected InteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        InteractionResult result = super.useItemOn(player, hand, face);
        return result.consumesAction() ? result : redstoneControl.onUse(this, player, hand);
    }
    @Override public MIInventory getInventory() { return MIInventory.EMPTY; }
    @Override public List<EnergyComponent> getEnergyComponents() { return energyInputs; }
    @Override public ShapeTemplate getActiveShape() {
        return SHAPE_TEMPLATES.length == 0 ? null : SHAPE_TEMPLATES[tierIndex()];
    }
    @Override public MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive);
    }
    private int tierIndex() { return Math.max(0, Math.min(activeTier.getActiveShape(), TIERS.size() - 1)); }
    public Tier getTier() { return TIERS.get(tierIndex()); }
    private boolean consumeEu(Simulation simulation) {
        long amount = getTier().eu(), total = 0;
        for (EnergyComponent energy : energyInputs) total += energy.consumeEu(amount - total, simulation);
        return total == amount;
    }
    private void applyEffect() {
        AABB box = new AABB(worldPosition.relative(orientation.facingDirection.getOpposite()))
                .inflate(getTier().range()).expandTowards(0, level.getHeight(), 0);
        for (Player player : level.getEntitiesOfClass(Player.class, box))
            player.addEffect(new MobEffectInstance(YAIEffects.CREATIVE_FLIGHT, 160, 0, true, true));
    }
    private Component statusComponent() {
        if (!isShapeValid()) return MIText.MultiblockShapeInvalid.text();
        return (isActive.isActive ? MIText.MultiblockStatusActive : MIText.MultiblockShapeValid).text();
    }
}
