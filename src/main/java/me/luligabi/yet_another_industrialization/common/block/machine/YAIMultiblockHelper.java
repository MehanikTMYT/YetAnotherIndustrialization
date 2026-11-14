package me.luligabi.yet_another_industrialization.common.block.machine;

import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

/**
 * port-26.1.2: перенос Kotlin {@code YAIMultiblockHelper} (interface + companion → статический
 * utility-класс, т.к. Java без companion object; ловушка AGENTS.md про Kotlin-синтаксис).
 *
 * <p>Факт API26 (javap MI 2.6.0-26.1.2-fleet01): {@code HatchFlags.Builder.with(HatchType...)},
 * {@code SimpleMember#matchesState(BlockState, BlockEntity)} /
 * {@code #getPreviewState()} / {@code #newBlockEntity(...)},
 * {@code ShapeTemplate.Builder#add(int,int,int,SimpleMember[, HatchFlags])}.</p>
 */
public final class YAIMultiblockHelper {

    /** Стандартный набор хатчей апстрима (для большинства мультиблоков YAI). */
    public static final HatchFlags HATCHES = new HatchFlags.Builder()
            .with(HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT,
                    HatchTypes.FLUID_INPUT, HatchTypes.FLUID_OUTPUT,
                    HatchTypes.ENERGY_INPUT)
            .build();

    /** Простое стекло (кейс: '@' в паттернах). */
    public static final SimpleMember GLASS_MEMBER = new SimpleMember() {
        @Override
        public boolean matchesState(BlockState state, BlockEntity be) {
            return state.is(Tags.Blocks.GLASS_BLOCKS);
        }

        @Override
        public BlockEntity newBlockEntity(RegistryAccess registries, Level level, BlockPos pos, BlockState state) {
            return null;
        }

        @Override
        public BlockState getPreviewState() {
            return Blocks.GLASS.defaultBlockState();
        }
    };

    /** Разбор символа паттерна → блок-участник (null — символ не является материалом). */
    @FunctionalInterface
    public interface MemberResolver {
        SimpleMember member(char c, int y);
    }

    /** Разбор символа паттерна → флаги хатчей (null — на этой позиции хатчи запрещены). */
    @FunctionalInterface
    public interface HatchResolver {
        HatchFlags hatches(char c, int y);
    }

    /**
     * Апстрим: {@code fun ShapeTemplate.Builder.addLayer(y, patternIndex, ...)} — обход строки
     * {@code pattern.get(patternIndex)} слева направо (x = индекс символа), z = индекс строки.
     */
    public static ShapeTemplate.Builder addLayer(ShapeTemplate.Builder builder, int y, int patternIndex,
                                                 List<List<String>> pattern,
                                                 MemberResolver materials, HatchResolver hatches,
                                                 int controllerXOffset) {
        List<String> rows = pattern.get(patternIndex);
        for (int z = 0; z < rows.size(); z++) {
            String row = rows.get(z);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (c == '_') {
                    continue;
                }
                SimpleMember member = materials.member(c, y);
                if (member == null) {
                    continue;
                }
                HatchFlags flags = hatches.hatches(c, y);
                if (flags == null) {
                    builder.add(x + controllerXOffset, y, z, member);
                } else {
                    builder.add(x + controllerXOffset, y, z, member, flags);
                }
            }
        }
        return builder;
    }

    private YAIMultiblockHelper() {
    }
}
