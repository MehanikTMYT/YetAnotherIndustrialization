# PORT NOTES — Yet Another Industrialization → Minecraft 26.1.2 / NeoForge / Java 25

Ветка: `port/26.1-pl` · Рабочая копия: NL `/root/home/forks/yai-stack/YetAnotherIndustrialization`
Источник: `Luligabi1/YetAnotherIndustrialization` @ `1.21.1` (`3641da7`, «Update to MI 2.5.8»), версия мода 1.6.3-1.21.1
Цель: та же функциональность на MI 26.1.2 (наш форк `aztech:Modern-Industrialization:2.6.0-26.1.2-fleet01`), весь исходный код — Java 25.

## Решение пользователя (2026-10-25)

1. **Добавлен ru_ru.json** — коммит `e1de03d` (полное покрытие 176/176 ключей, плейсхолдеры и `\n` сохранены; проверка скриптом).
2. **Минимум зависимостей**: оставить только основной мод (MI) и то, что без него не существует.
3. **Полный переход Kt → Java**: устранение Kotlin-кода 1:1.

## Политика зависимостей (итог)

| Dependency (1.21.1) | Роль | Решение | Обоснование |
|---|---|---|---|
| Modern Industrialization 2.5.8 | ВАЖНО | **ОСТАВЛЯЕМ** → наш адаптированный артефакт `aztech:Modern-Industrialization:2.6.0-26.1.2-fleet01` (mavenLocal) | основной мод |
| Tesseract API 1.12.7 | API ядер YAI | **ОСТАВЛЯЕМ** → `net.swedz:tesseract-api-neoforge:1.13.6-26.1.2` (modmaven) + его транзитивная `tesseract-api:1.0.2` | внутренний API YAI (config/lang/proxy/hooks/packets/materials); заменить нельзя без переписывания мода. Сам Tesseract 1.13.6 — чистая Java |
| Kotlin for Forge 5.6 | язык рантайма | **УБИРАЕМ** (после полного перехода на Java) | мод станет non-Kotlin; mods.toml перепишется на `javafml` |
| GrandPower 3.0 | energy API | **ОСТАВЛЯЕМ** (транзитивно от MI; в 26.x GrandPower сам по себе — простая lib) | 4 файла используют напрямую (`ISimpleEnergyItem`, `ILongEnergyStorage`, `EnergyStorageUtil`) |
| GuideMe 21.1.9 | справочник | **ОСТАВЛЯЕМ** → `org.appliedenergistics:guideme:26.1.10-alpha` | поддерживаемая фича (guidebook item + guide), сборка 26.1.2 у нас уже есть (MI использует ту же) |
| EMI 1.1.22 | recipe viewer | **ОСТАВЛЯЕМ** → EMI 26.1.2 из mavenLocal (`dev.emi:emi-neoforge:1.1.24-semi151a6c88+26.1.2`) | в сборке пакета стек — EMI (как у MI); драг-дроп Storage Slot Locker зависит от него |
| Jade (curse) | tooltip | **УБИРАЕМ** | 0 обращений к классам в коде — пустая зависимость даже в апстриме |
| Curios 9.5.1 | доп. слоты | **УБИРАЕМ** | только 2 файла прокси-адаптеров и 1 рендерер; очки уже работают через ванильный `Equipable` (head). Сохраняется базовый `YAIModSlotProxy` (vanilla fallback) |
| Accessories 1.1.0-beta | доп. слоты | **УБИРАЕМ** | 1 прокси-файл; то же самое |
| JEI 19.24 / REI 16.0 | recipe viewer | **УБИРАЕМ** (пакеты jei/, rei/ в port/26.1-pl удалены) | в сборке только EMI (решение пользователя 2026-10-25: только саб-сависимости, кроме core); общие категории (Numismatic/FLP/ItemCharger/LSU/NeutronSource — `common/compat/recipeviewer/*Category`) на абстракции MI остаются и работают через EMI |
| (KubeJS) | stub | Оставляем пустую заглушку классом-болванкой (в апстриме уже пустая) | нулевая стоимость |

 Итог времени выполнения: jar YAI требует **только** MI (+ ваниль). Опциональные: Tesseract (нужен), GuideMe (guidebook), EMI (просмотр рецептов). Curios/Accessories/JEI/REI/KFF — не требуются.

## Карта слоёв Kotlin → Java (целевой порядок конверсии)

Батчи собираются от листьев к корню, компилируются поэтапно (состояние Gradle под 26.1.2 остаётся постоянно зелёным после каждого коммита в converted-пакет):

1. util, YAIConfig, YAIText (интерфейсы) · YAI (точка входа), YAIUtil · misc (component/datamap/effect/keybind/material/network/sound/tags/trades/command/hatchtypes/fluids/creative tab/tooltips)
2. item (food/misc/tools) + proxy (только `YAIModSlotProxy`-vanilla) + compat/mi (hooks), compat/kubejs (PAS)
3. block/machine/*: machinal BE → multiblock → генераторы; YAIMachines/YAIBlocks/YAIFluids реестры; model/multiblock geometry (common)
4. client/ (config, component, model/multiblock, renderer) — für BER/model loaders, в т.ч. после AT-проверок
5. datagen/ (26 файлов) — Java-версия providers; затем контрольное `runData` с верификацией, что `src/generated/resources` byte-равен апстроимовому (кроме уже добавленного ru_ru.json)
6. Удалить всё `.kt`, плагин kotlin и KFF; mods.toml → `javafml`; финальная сборка+QA.

## Основные API-переходы (черновик таблицы, IE. 1)

| Было (1.21.1) | Стало (26.1.2) |
|---|---|
| `aztech.modern_industrialization.thirdparty.fabrictransfer.api.*` (ItemVariant/FluidVariant/TransferVariant/Transaction) | `net.neoforged.neoforge.transfer.*` (ItemResource/FluidResource/Resource/Transaction) |
| `CableTier` (accessor-field `rawVoltage`) и др. миксины MI | см. миксин-инвентарь + diff по `mi-stack/port/26.1.2` (22 миксина сохраняются, где целевые классы живы) |
| `IdLike` (MI) → `Identifier` (в Tesseract 1.13.6 вместо `ResourceLocation` — MC 26 переименование) | `net.minecraft.resources.Identifier` |
| AT: `ModelBlockRenderer` internals, `LootTable`/`LootPool` internals, `TreeGrower` fields, `ServerLevel.players`, `Level.random` | пересборка AT под 26.1.2 по месту (проверка каждого таргета) |
| плеер–настройки `parchment 2024.11.17` | убраны (для 26.1.2 нет официальных карт); код пишется с учётом расширенных error messages |
| `neoforge.mods.toml`/`kubejs` плагинов нет | актуализация: `equippable` для очков НЕ нужен (ванильный Equipable), AT для `emissive` и т.д. — по ходу |

## Коммит-регламент порт-ветки
- Каждый батч = коммит (шаг батча + STC).
- НО НЕ push до публиковать (JSON правок не выбрасывать в network).
