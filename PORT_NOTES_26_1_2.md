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

Фактические зависимости ещё проверяются при переносе. MI, Tesseract (config/lang) и GuideMe необходимы текущей архитектуре; нельзя считать Tesseract опциональным. Старый MI-модуль Tesseract в артефакте 26.1.2 отсутствует. GrandPower больше не используется новым энергетическим адаптером, но окончательное удаление требует переноса предметов. EMI сохраняется для просмотра рецептов.

## Карта слоёв Kotlin → Java (целевой порядок конверсии)

Батчи переносятся от листьев к корню. Полная сборка пока не проходит: Kotlin-дерево было удалено до восстановления Java-классов. Для проверки готового слоя используется отдельный source set foundation; его успешная компиляция не означает готовность всего мода.

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

## Проверенный прогресс — 2026-10-26

- Исправлен запуск сборки на Java 25: Gradle 9.2.1, ModDev repositories 2.0.147; Kotlin plugin/KFF удалены. Основная сборка ещё блокируется отсутствующими классами и старыми mixin API.
- На Java перенесены конфигурации, интерфейс переводов, идентификаторы, звуки, теги, тип урона, эффект полёта, draggable-контракт и энергетические адаптеры.
- EnergyComponentStorage использует транзакционные insert/extract самого MI. Его журнал обеспечивает откат вложенных транзакций и сохранение BlockEntity только после корневого commit; ёмкость и остаток — long.
- SlotLockerData переведён на ItemResource/FluidResource и неизменяемый record. Использовать withItem/withFluid/withMode и затем ItemStack.set; запрещено возвращаться к мутациям общего компонента.
- Зарегистрированы типы компонентов slot_locker_data и goggles_enabled; подключение их init к будущему entrypoint ещё впереди.
- В Tesseract 1.13.6-26.1.2 отсутствует compat.mi: 133 класса находятся в src_disabled upstream-ветки. Старые hooks/material helpers/GUI нельзя считать готовыми зависимостями. Следующий этап — прямые регистрации MI и перенос машин/предметов без заглушек.

### Изолированная проверка foundation

Сборка тестового артефакта (не релизный YAI):
```sh
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew -I scripts/foundation.gradle foundationQaJar
```

Запускать полученный *-foundation-qa.jar в отдельном dedicated NeoForge 26.1.2.122 с MI 2.6.0-26.1.2-fleet01 и GuideMe 26.1.14-beta. На NL создан игнорируемый qa-server с собственным портом 25981; libraries ссылается на .port-tools/srv26-ru/libraries. После проверок сервер останавливается автоматически. Обычный JavaExec непригоден: BlockEntity требует активный FML loader.

FoundationChecks проверяет транзакции/границы энергии и persistent/network round trips компонентов. Маркеры успеха: YAI_FOUNDATION_PASS и YAI_COMPONENTS_PASS; YAI_FOUNDATION_FAIL означает провал даже при коде выхода сервера 0. Проверка полного игрового мода и клиента пока не выполнена.

Результат isolated dedicated QA 2026-10-26: **39 проверок PASS** (17 энергетических + 22 компонентов). Лог NL: /root/home/scratch/yai-port/validation/foundation-server.log.

## Предметы — второй Java-батч, 2026-10-26

- YAIItems напрямую регистрирует девять перенесённых предметов с исходными ID через DeferredRegister.Items: guidebook, industrialists_goggles, dragon_egg_siphon_catalyst, tempproof_air_intake, demon_core, cachaca, ai_slop, ultradense_metal_ball_burger, energy_zap. Это ещё не весь реестр: machine_diagnoser, machine_remover, storage_slot_locker остаются в работе.
- Три вида еды сохраняют исходные nutrition/saturation, длительность и вероятности эффектов. Cachaça: 16 в стопке, 0.8 секунды, DRINK, nausea II на 160 ticks с вероятностью 35%, возврат стеклянной бутылки.
- Demon Core: 320 прочности; supportsEnchantment/isPrimaryItemFor запрещают стол и наковальню независимо от тегов.
- Очки: ванильный equippable HEAD, исходный компонент enabled=true; toggleMode изменяет только надетый предмет. Curios/Accessories не возвращаются согласно согласованному сокращению зависимостей.
- Перенесены YAIGuidebookItem и описание YAIGuide с MultiblockShapeCompiler MI. Клиентское открытие книги и отображение очков ещё не проверены.
- Для девяти предметов добавлены современные assets/.../items/*.json, ссылающиеся на существующие модели. Старые models/item сами по себе недостаточны для MC 26.
- ItemChecks регистрирует реальные YAIItems и YAIDataComponents через отдельный QA entrypoint, проверяет питание, эффекты, фактическое потребление бутылки, зачарование, слот головы и независимость копий при переключении очков.
- Следующий связанный этап: оставшиеся инструменты и их обращения к inventory/crafter API, затем прямой реестр машин (старые Tesseract hooks отсутствуют). Production entrypoint, creative tab и datagen ещё предстоит восстановить.

Результат второго батча: 61 dedicated-проверка PASS (17 energy + 22 component + 22 item), включая регистрацию под настоящими ID YAI. Логи: /root/home/scratch/yai-port/validation/items-server.log и items-build.log. Ключи item-tooltip сверены с en_us и ru_ru; клиентский рендер ещё не проверен.

## Storage Slot Locker — Java-батч 2026-10-26

- Зарегистрирован десятый предмет storage_slot_locker и современное определение его модели.
- Перенесены шесть режимов блокировки предметных/жидкостных слотов на публичные MIInventory.getItemStacks/getFluidStacks. MIStorageAccessor и UseOnContextAccessor инструменту больше не нужны.
- Перенесены dragItem/dragFluid, simulation без изменения компонентов, TooltipData и текстовые подсказки (Shift через TooltipFlag, без ссылки common-кода на клиентский Screen).
- Исправлено ложное сообщение об успехе на неизменяемых слотах. Player-lock не трогает machine-lock и слоты canPlayerLock=false; повторный вызов без изменений возвращает false.
- Native NeoForge serverbound payloads заменяют старые Tesseract CustomPacket для смены режима и выбора ресурса. Проверяются индекс, текущий containerId, stillValid и mayPickup; изменения синхронизируются через broadcastChanges. Удалён неиспользуемый playerId из смены режима, версия протокола 2.
- YAIPackets пока содержит только два перенесённых обработчика, не весь исходный сетевой набор.
- Осталось подключить клиентские scroll/EMI callbacks и перенести StorageSlotLockerComponent renderer; полная работа инструмента в клиенте пока не заявляется. Production entrypoint и оставшиеся инструменты (diagnoser/remover) также ещё не готовы.

Результат: 107 dedicated assertions PASS (17 energy + 22 components + 22 items + 46 locker).
Логи NL: /root/home/scratch/yai-port/validation/locker-server.log и locker-build.log.
В запуске был отдельный Log4j DebugFile/Netty kqueue platform-probe exception (OSX/BSD transport на Linux) до готовности сервера; сервер после этого запустился, все проверки прошли и завершился штатно. Это не чистый от предупреждений/ошибок лог; клиентская проверка и сетевой обмен с реальным клиентом ещё не выполнены.

## Клиент Slot Locker — 2026-10-26

- SlotLockerClient регистрирует фабрику подсказки и Shift+scroll для предмета в основной руке. Обработчик безопасен без player и не перехватывает прокрутку открытого GUI.
- StorageSlotLockerComponent перенесён с GuiGraphics/RenderSystem на GuiGraphicsExtractor; сохранены размеры 40x22, обе иконки, atlas 256x256 и четыре состояния рамки.
- EMI entrypoint и drag/drop handler перенесены на ItemResource/FluidResource и ClientPacketDistributor; компоненты выбранного ресурса сохраняются. Пустой список ингредиентов не вызывает исключения. Creative inventory по-прежнему исключён.
- Убран AbstractContainerScreenAccessor из пути drag/drop: индекс берётся из menu.slots, попадание проверяется по координатам. getGuiLeft/getGuiTop поддерживаются NeoForge 26.1.2.122, но помечены deprecated.
- В foundation QA добавлен отдельный CLIENT entrypoint и assets. На integrated server серверный QA больше не останавливает мир: dedicated проверки остаются отдельными.
- Изолированный клиент: /root/home/scratch/yai-port/client-harness, NeoForge 26.1.2.122, MI fleet01, GuideMe 26.1.14-beta, SEMI 1.1.24-semi151a6c88+26.1.2, DISPLAY=:99.
- В полном наборе старых assets выявлены ошибки machine/multiblock model loaders; их перенос остаётся отдельным обязательным этапом. Ресурсы не удалены и не заменены пустыми моделями.
- Production client entrypoint ещё отсутствует: при его восстановлении вызвать SlotLockerClient.init(modBus); EMI находит YAIEmiPlugin через @EmiEntrypoint.
