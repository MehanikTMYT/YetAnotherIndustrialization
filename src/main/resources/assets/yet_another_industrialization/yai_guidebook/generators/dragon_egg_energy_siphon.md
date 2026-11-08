---
navigation:
  title: "Dragon Egg Energy Siphon"
  icon: "yet_another_industrialization:dragon_egg_energy_siphon"
  position: 5
  parent: yet_another_industrialization:generators.md
item_ids:
  - yet_another_industrialization:dragon_egg_energy_siphon
  - yet_another_industrialization:dragon_egg_siphon_catalyst
  - yet_another_industrialization:steel_plated_end_stone_bricks
  - yet_another_industrialization:impure_dragon_breath_bucket
---

# Dragon Egg Energy Siphon
###### *Crane's law attacks once again*

<GameScene zoom="3" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:dragon_egg_energy_siphon" />
</GameScene>

Dragon Egg Energy Siphons are capable of generating energy by harnessing the Dragon Egg's power... Not! I mean, this is MI. Nothing is truly passive here :P

<Row>
    <Recipe id="yet_another_industrialization:craft/dragon_egg_energy_siphon" />
    <Recipe id="yet_another_industrialization:craft/steel_plated_end_stone_bricks" />
</Row>

## Producing energy

Using an **Assembler**, craft some **Dragon Egg Siphon Catalysts** with **Steel Plates**, a single **Quartz Tiny Dust** and [Dragon's Breath](../utilities/fluids.md). They're consumed everytime energy is generated.

Finally, input a **Dragon Egg Siphon Catalyst** and 1000mB of **Dragon Breath** to produce energy!

<Recipe id="yet_another_industrialization:dragon_egg_energy_siphon/dragon_breath" />

## Aftermath

After producing energy, the inserted Dragon's Breath becomes **Impure Dragon's Breath**. 

At this state, it cannot be used for anything, so you'll need to purify it by centrifuging it. Doing so might also yield some **Quartz Tiny Dust**.

<Recipe id="yet_another_industrialization:centrifuge/impure_dragon_breath_purify" />

This cycle grants a net-positive amount of **Dragon's Breath**, also serving as the way to automate it even if you're not interested in the energy produced.

## Better yields?

Use [Nutrient Dragon's Breath](../utilities/fluids.md) to improve all aspects of this processing chain!

<Recipe id="yet_another_industrialization:dragon_egg_energy_siphon/nutrient_dragon_breath" />

<Recipe id="yet_another_industrialization:centrifuge/impure_dragon_breath_purify_nutrient" />