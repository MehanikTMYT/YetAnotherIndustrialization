package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import me.luligabi.yet_another_industrialization.common.YAIIds;
import me.luligabi.yet_another_industrialization.common.YAIRecipeTypes;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines;
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * port-26.1.2: перенос Kotlin {@code generator/multiblock/DragonSiphonBlockEntity.kt}.
 *
 * <p>Факты API26 (javap MI 2.6.0-26.1.2-fleet01): {@code SimpleMember#forBlock(Supplier)},
 * {@code #forBlockId(Identifier)}, {@code HatchFlags.Builder#with(HatchType...)},
 * {@code ShapeTemplate.Builder#build()}; {@code YAIBlocks.STEEL_PLATED_END_STONE_BRICKS} — наш
 * DeferredBlock (блок существует в порте, кейс casing регистрируется hook'ом — см. YAIMachines).</p>
 *
 * <p>Паттерн/правила перенесены дословно: '#' → кейсинг SPESB (+хатчи), '@' → стекло,
 * '$' → драконье яйцо; controllerXOffset = -1.</p>
 */
public class DragonSiphonBlockEntity extends YAIGeneratorMultiblockBlockEntity {

    public static final String ID = "dragon_egg_energy_siphon";
    public static final String NAME = "Dragon Egg Energy Siphon";

    private static final SimpleMember CASING = SimpleMember.forBlock(
            () -> YAIBlocks.STEEL_PLATED_END_STONE_BRICKS.get());

    private static final SimpleMember DRAGON_EGG = SimpleMember.forBlockId(
            Identifier.withDefaultNamespace("dragon_egg"));

    private static final List<List<String>> PATTERN = List.of(
            List.of("###", "###", "###"),
            List.of("#@#", "@$@", "#@#"),
            List.of("_#_", "###", "_#_")
    );

    private static final HatchFlags HATCHES = new HatchFlags.Builder()
            .with(HatchTypes.ITEM_INPUT,
                    HatchTypes.FLUID_INPUT, HatchTypes.FLUID_OUTPUT,
                    HatchTypes.ENERGY_OUTPUT)
            .build();

    private static final int CONTROLLER_X_OFFSET = -1;

    public static final ShapeTemplate SHAPE = buildShape();

    public DragonSiphonBlockEntity(BEP bep) {
        // апстрим: arrayOf(SHAPE) — одна форма в массиве
        super(bep, YAIIds.id(ID), new ShapeTemplate[]{SHAPE});
    }

    private static ShapeTemplate buildShape() {
        ShapeTemplate.Builder builder = new ShapeTemplate.Builder(
                YAIMachines.Casings.STEEL_PLATED_END_STONE_BRICKS);
        for (int y = 0; y < PATTERN.size(); y++) {
            YAIMultiblockHelper.addLayer(builder, y, y, PATTERN,
                    DragonSiphonBlockEntity::material, DragonSiphonBlockEntity::hatch, CONTROLLER_X_OFFSET);
        }
        return builder.build();
    }

    private static SimpleMember material(char c, int y) {
        return switch (c) {
            case '#' -> CASING;
            case '@' -> YAIMultiblockHelper.GLASS_MEMBER;
            case '$' -> DRAGON_EGG;
            default -> null;
        };
    }

    private static HatchFlags hatch(char c, int y) {
        return c == '#' ? HATCHES : null;
    }

    @Override
    public MachineRecipeType recipeType() {
        return YAIRecipeTypes.DRAGON_EGG_ENERGY_SIPHON;
    }
}
