---
navigation:
  title: "Сифон энергии драконьего яйца"
  icon: "yet_another_industrialization:dragon_egg_energy_siphon"
  position: 5
  parent: yet_another_industrialization:generators.md
item_ids:
  - yet_another_industrialization:dragon_egg_energy_siphon
  - yet_another_industrialization:dragon_egg_siphon_catalyst
  - yet_another_industrialization:steel_plated_end_stone_bricks
  - yet_another_industrialization:impure_dragon_breath_bucket
---

# Сифон энергии драконьего яйца
###### *Закон Крейна снова в деле*

<GameScene zoom="3" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:dragon_egg_energy_siphon" />
</GameScene>

Сифоны энергии драконьего яйца способны вырабатывать энергию, используя силу драконьего яйца... А вот и нет! Это же MI. Здесь ничто не работает полностью пассивно :P

<Row>
    <Recipe id="yet_another_industrialization:craft/dragon_egg_energy_siphon" />
    <Recipe id="yet_another_industrialization:craft/steel_plated_end_stone_bricks" />
</Row>

## Выработка энергии

В **сборщике** изготовьте **катализаторы сифона драконьего яйца** из **стальных пластин**, одной **крошечной кучки кварцевой пыли** и [драконьего дыхания](../utilities/fluids.md). Катализаторы расходуются при каждой выработке энергии.

Затем подайте **катализатор сифона драконьего яйца** и 1000mB **драконьего дыхания**, чтобы получить энергию!

<Recipe id="yet_another_industrialization:dragon_egg_energy_siphon/dragon_breath" />

## Что остаётся после

После выработки энергии поданное драконье дыхание превращается в **загрязнённое драконье дыхание**. 

В таком виде оно ни для чего не пригодно, поэтому его нужно очистить в центрифуге. При этом также можно получить немного **крошечных кучек кварцевой пыли**.

<Recipe id="yet_another_industrialization:centrifuge/impure_dragon_breath_purify" />

За каждый цикл количество **драконьего дыхания** увеличивается, поэтому процесс позволяет автоматизировать его получение, даже если вырабатываемая энергия не нужна.

## Как повысить выход?

Используйте [питательное драконье дыхание](../utilities/fluids.md), чтобы улучшить все этапы этой производственной цепочки!

<Recipe id="yet_another_industrialization:dragon_egg_energy_siphon/nutrient_dragon_breath" />

<Recipe id="yet_another_industrialization:centrifuge/impure_dragon_breath_purify_nutrient" />