package me.luligabi.yet_another_industrialization.common.block.machine.util.components;

import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * port-26.1.2: перенос Kotlin {@code SuppliedShapeSelection} — {@link ShapeSelection}, у которого
 * линии (названия тиров) берутся у поставщика, а не заданы статически.
 *
 * <p><b>Отличие от апстрима (важно для26.1.2).</b> Апстрим реализовывал интерфейс
 * {@code GuiComponentServer} напрямую и имел СОБСТВЕННЫЙ TYPE
 * ({@code yet_another_industrialization:supplied_shape_selection}). В MI 2.6.0-26.1.2-fleet01 это
 * не работает (javap):</p>
 * <ul>
 *   <li>{@code network/machines/ChangeShapePacket} ищет компонент по КЛАССУ —
 *       {@code guiComponents.getOrThrow(ShapeSelection.class)} → наш класс не был бы найден и
 *       клики по стрелкам выбора почвы падали бы на сервере;</li>
 *   <li>декодирование параметров на клиенте идёт по TYPE-id, и у собственного TYPE нет
 *       зарегистрированного {@code GuiComponentsClient}-фабрики (в порте нет client-слоя машин).</li>
 * </ul>
 *
 * <p>Решение: наследуем {@link ShapeSelection} (→ {@code getOrThrow(ShapeSelection.class)} находит
 * наш экземпляр через {@code Class.isInstance}, см. {@code ComponentStorage#getNullable}) и НЕ
 * переопределяем {@code getType()} — возвращается {@code ShapeSelection.TYPE}, клиентский
 * {@code ShapeSelectionClient} уже зарегистрирован самим MI. Линии/данные при этом остаются
 * динамическими ({@link #getParams()}/{@link #extractData()} переопределены).</p>
 *
 * <p>Факт API26: {@code ShapeSelection(Behavior, LineInfo...) — Preconditions.checkArgument(lines.length &gt; 0)},
 * поэтому в {@code super()} передаётся одна placeholder-линия (она не используется, т.к. оба
 * метода переопределены); {@code ShapeSelection.LineInfo(List&lt;Component&gt;, boolean)}.</p>
 */
public class SuppliedShapeSelection extends ShapeSelection {

    /** Заглушка для super(): ShapeSelection требует хотя бы одну линию (Preconditions). */
    private static final LineInfo PLACEHOLDER = new LineInfo(List.of(Component.empty()), true);

    private final List<Supplier<LineInfo>> lines;

    @SafeVarargs
    public SuppliedShapeSelection(ShapeSelection.Behavior behavior, Supplier<LineInfo>... lines) {
        super(behavior, PLACEHOLDER);
        this.lines = List.of(lines);
    }

    @Override
    public List<LineInfo> getParams() {
        return lines.stream().map(Supplier::get).toList();
    }

    @Override
    public List<Integer> extractData() {
        return IntStream.range(0, lines.size()).map(behavior::getCurrentIndex).boxed().toList();
    }
}
