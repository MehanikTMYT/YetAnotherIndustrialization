---
navigation:
  title: "Жидкости"
  icon: "yet_another_industrialization:nutrient_rich_water_bucket"
  position: 5
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:nutrient_rich_water_bucket
  - yet_another_industrialization:nutrient_rich_lava_bucket
  - yet_another_industrialization:dragon_breath_bucket
  - yet_another_industrialization:nutrient_rich_dragon_breath_bucket
---

# Жидкости
###### *хлюп-хлюп*

<FloatingColumn align="right">
	<PaddedBox left="5" right="10" bottom="5">
		<Row gap="1">
			<ItemImage id="minecraft:water_bucket" scale="2" />
			<ItemImage id="yet_another_industrialization:nutrient_rich_water_bucket" scale="2" />
		</Row>
		<Row gap="1">
			<ItemImage id="minecraft:lava_bucket" scale="2" />
			<ItemImage id="yet_another_industrialization:nutrient_rich_lava_bucket" scale="2" />
		</Row>
        <Row gap="1">
			<ItemImage id="yet_another_industrialization:dragon_breath_bucket" scale="2" />
			<ItemImage id="yet_another_industrialization:nutrient_rich_dragon_breath_bucket" scale="2" />
		</Row>
	</PaddedBox>
</FloatingColumn>

## Питательные жидкости

Для большинства рецептов, потребляющих жидкости, питательные варианты позволяют снизить расход ресурсов и/или увеличить выход.

<Row>
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_water" />
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_lava" />
</Row>

## Драконье дыхание

Драконье дыхание можно извлекать из бутылочек и накапливать в вёдрах:

<Recipe id="yet_another_industrialization:centrifuge/dragon_breath" />

Получив несколько вёдер, можно автоматизировать процесс с помощью [очистки загрязнённого драконьего дыхания](../generators/dragon_egg_energy_siphon.md)!

Как и воду с лавой, драконье дыхание можно обогатить:

<Row>
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_dragon_breath" />
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_dragon_breath_dragon_egg" />
</Row>
