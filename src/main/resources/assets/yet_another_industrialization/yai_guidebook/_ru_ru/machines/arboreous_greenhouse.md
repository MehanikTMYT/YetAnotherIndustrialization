---
navigation:
  title: "Древесная оранжерея"
  icon: "yet_another_industrialization:arboreous_greenhouse"
  position: 0
  parent: yet_another_industrialization:machines.md
item_ids:
  - yet_another_industrialization:arboreous_greenhouse
---

# Древесная оранжерея
###### *пустьрастётпустьрастёт*

<GameScene zoom="1.5" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:arboreous_greenhouse" />
    <MultiblockShape controller="yet_another_industrialization:arboreous_greenhouse" useBigShape={true} x="-14" />
    <IsometricCamera yaw="190" pitch="15" />
</GameScene>

Древесные оранжереи выращивают деревья и другие крупные растения.

<Recipe id="yet_another_industrialization:craft/arboreous_greenhouse" />

## Начало работы

Для любого рецепта нужны один саженец (или его аналог), жидкость и подходящая почва в многоблоке:

<Recipe id="yet_another_industrialization:arboreous_greenhouse/minecraft/oak_sapling/regular" />

Разумеется, у разных растений разные потребности. Одним нужны вода и песок, другим — лава и незерак. Точные сведения всегда смотрите в просмотрщике рецептов!

Некоторые растения также дают дополнительные продукты: плоды, корни и т. д.

## Повышение выхода

Количество всех выходных продуктов можно удвоить, заменив используемую жидкость её [питательным вариантом](../utilities/fluids.md):

<Recipe id="yet_another_industrialization:arboreous_greenhouse/minecraft/oak_sapling/nutrient" />