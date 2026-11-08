---
navigation:
  title: "Fluids"
  icon: "yet_another_industrialization:nutrient_rich_water_bucket"
  position: 5
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:nutrient_rich_water_bucket
  - yet_another_industrialization:nutrient_rich_lava_bucket
  - yet_another_industrialization:dragon_breath_bucket
  - yet_another_industrialization:nutrient_rich_dragon_breath_bucket
---

# Fluids
###### *slurps*

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

## Nutrient Fluids

Most recipes that take fluid input can be made better using nutrient variants, be it in input and/or output efficiency.

<Row>
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_water" />
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_lava" />
</Row>

## Dragon's Breath

Dragon's Breath can be extracted from bottles onto buckets:

<Recipe id="yet_another_industrialization:centrifuge/dragon_breath" />

Once you have a few buckets, consider automating the process through [Impure Dragon Breath purification](../generators/dragon_egg_energy_siphon.md)!

Like water and lava, Dragon's Breath can be enriched:

<Row>
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_dragon_breath" />
    <Recipe id="yet_another_industrialization:centrifuge/nutrient_rich_dragon_breath_dragon_egg" />
</Row>
