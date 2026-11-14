package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.YAIRecipeTypes;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.YAIGeneratorMultiblockBlockEntity;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlockEntity;
import me.luligabi.yet_another_industrialization.common.misc.YAIDamageTypes;
import me.luligabi.yet_another_industrialization.mixin.ShapeMatcherAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * port-26.1.2: перенос Kotlin {@code pdg/PulseDetonationGeneratorBlockEntity.kt}.
 *
 * <p>Факты API26:</p>
 * <ul>
 *   <li>паттерн 11 слоёв + controllerXOffset=-5 — дословно из апстрима;</li>
 *   <li>{@code ShapeMatcher#simpleMembers} — world-координаты (javap -c: ключи конвертируются
 *       {@code toWorldPos} в конструкторе), поэтому {@code level.getBlockEntity(pos)} корректен;</li>
 *   <li>{@code Level#explode(Entity, DamageSource, ExplosionDamageCalculator, double,double,double,
 *       float, boolean, ExplosionInteraction, ParticleOptions, ParticleOptions, WeightedList,
 *       Holder)} — в 26.1.2 это абстрактный13-параметровый метод (neoForm Level.java:652);
 *       прежнего 11-параметрового перегружения с частицами/звуком больше нет;</li>
 *   <li>блок-частицы взрыва: {@code Level.DEFAULT_EXPLOSION_BLOCK_PARTICLES} — private, поэтому
 *       собран идентичный {@code WeightedList} (POOF 0.5/1.0 + SMOKE 1.0/1.0 — neoForm Level.java:104).</li>
 * </ul>
 */
public class PulseDetonationGeneratorBlockEntity extends YAIGeneratorMultiblockBlockEntity {

    public static final String ID = "pulse_detonation_generator";
    public static final String NAME = "Pulse Detonation Generator";

    private static final SimpleMember CASING = SimpleMember.forBlock(
            () -> MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());

    private static final SimpleMember CASING_PIPE = SimpleMember.forBlock(
            () -> MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());

    private static final SimpleMember CORE = SimpleMember.forBlock(
            () -> YAIMachines.getMachineFromId(DetonationChamberCasingBlockEntity.ID));

    private static final List<String> LAYER_0 = List.of(
            "___#xxx#___",
            "__#_____#__",
            "_#_______#_",
            "#_________#",
            "x____@____x",
            "x___@_@___x",
            "x____@____x",
            "#_________#",
            "_#_______#_",
            "__#_____#__",
            "___#xxx#___"
    );

    private static final List<String> LAYER_1 = List.of(
            "_____x_____",
            "___________",
            "___________",
            "___________",
            "___________",
            "x____@____x",
            "___________",
            "___________",
            "___________",
            "___________",
            "_____x_____"
    );

    private static final List<String> LAYER_2 = List.of(
            "_____#_____",
            "___________",
            "___________",
            "___________",
            "___________",
            "#_________#",
            "___________",
            "___________",
            "___________",
            "___________",
            "_____#_____"
    );

    private static final List<String> LAYER_3 = List.of(
            "___________",
            "_____#_____",
            "___________",
            "___________",
            "___________",
            "_#_______#_",
            "___________",
            "___________",
            "___________",
            "_____#_____",
            "___________"
    );

    private static final List<String> LAYER_4 = List.of(
            "___________",
            "___________",
            "_____#_____",
            "___________",
            "___________",
            "__#_____#__",
            "___________",
            "_____#_____",
            "___________",
            "___________",
            "___________"
    );

    private static final List<String> LAYER_5 = List.of(
            "___________",
            "___________",
            "___________",
            "_____#_____",
            "_____x_____",
            "___#xxx#___",
            "_____x_____",
            "_____#_____",
            "___________",
            "___________",
            "___________"
    );

    /** Индексы в PATTERN соответствуют слоям апстримного {@code addLayer(y, patternIndex)}. */
    private static final List<List<String>> PATTERN = List.of(
            LAYER_5, LAYER_4, LAYER_3, LAYER_2, LAYER_1,
            LAYER_0,
            LAYER_1, LAYER_2, LAYER_3, LAYER_4, LAYER_5
    );

    private static final HatchFlags HATCHES = new HatchFlags.Builder()
            .with(HatchTypes.ITEM_INPUT,
                    HatchTypes.FLUID_INPUT, HatchTypes.FLUID_OUTPUT,
                    HatchTypes.ENERGY_OUTPUT)
            .build();

    private static final int CONTROLLER_X_OFFSET = -5;

    public static final ShapeTemplate SHAPE = buildShape();

    private static final WeightedList<ExplosionParticleInfo> BLOCK_PARTICLES =
            WeightedList.<ExplosionParticleInfo>builder()
                    .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
                    .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
                    .build();

    private static final ExplosionDamageCalculator EXPLOSION_CALCULATOR = new ExplosionDamageCalculator() {
        @Override
        public boolean shouldBlockExplode(net.minecraft.world.level.Explosion explosion,
                                          net.minecraft.world.level.BlockGetter reader,
                                          BlockPos pos, BlockState state, float power) {
            return false;
        }

        @Override
        public boolean shouldDamageEntity(net.minecraft.world.level.Explosion explosion,
                                          net.minecraft.world.entity.Entity entity) {
            return true;
        }
    };

    private final List<DetonationChamberCasingBlockEntity> chambers = new ArrayList<>();

    public PulseDetonationGeneratorBlockEntity(BEP bep) {
        // апстрим: arrayOf(SHAPE) — одна форма в массиве
        super(bep, YAIIds.id(ID), new ShapeTemplate[]{SHAPE});
    }

    private static ShapeTemplate buildShape() {
        ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.TITANIUM_PIPE);
        for (int y = -5; y <= 5; y++) {
            int patternIndex = y + 5; // y=-5 -> LAYER_5 (idx0) ... y=0 -> LAYER_0 (idx5) ... y=5 -> LAYER_5 (idx10)
            YAIMultiblockHelper.addLayer(builder, y, patternIndex, PATTERN,
                    PulseDetonationGeneratorBlockEntity::material,
                    PulseDetonationGeneratorBlockEntity::hatch,
                    CONTROLLER_X_OFFSET);
        }
        return builder.build();
    }

    private static SimpleMember material(char c, int y) {
        return switch (c) {
            case '#' -> CASING;
            case 'x' -> CASING_PIPE;
            case '@' -> CORE;
            default -> null;
        };
    }

    private static HatchFlags hatch(char c, int y) {
        return c == 'x' ? HATCHES : null;
    }

    @Override
    public void onSuccessfulRematch(ShapeMatcher shapeMatcher) {
        Map<BlockPos, SimpleMember> simpleMembers = ((ShapeMatcherAccessor) shapeMatcher).getSimpleMembers();
        for (BlockPos pos : simpleMembers.keySet()) {
            if (level == null) {
                continue;
            }
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DetonationChamberCasingBlockEntity chamber) {
                chamber.setController(this);
                chambers.add(chamber);
            }
        }
    }

    @Override
    public void onFailedRematch(ShapeMatcher shapeMatcher) {
        for (DetonationChamberCasingBlockEntity chamber : chambers) {
            chamber.setController(null);
        }
        chambers.clear();
    }

    @Override
    public MachineRecipeType recipeType() {
        return YAIRecipeTypes.PULSE_DETONATION_GENERATOR;
    }

    @Override
    public void onInsert(boolean hasInsertedEnergy) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = getBlockPos().relative(orientation.facingDirection.getOpposite(), 5).above();
        serverLevel.explode(
                null,
                YAIDamageTypes.pdg(serverLevel),
                EXPLOSION_CALCULATOR,
                pos.getX(), pos.getY(), pos.getZ(),
                5.0F,
                false,
                Level.ExplosionInteraction.NONE,
                ParticleTypes.EXPLOSION_EMITTER,
                ParticleTypes.EXPLOSION_EMITTER,
                BLOCK_PARTICLES,
                SoundEvents.GENERIC_EXPLODE
        );
    }
}
