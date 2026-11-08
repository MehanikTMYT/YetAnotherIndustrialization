---
navigation:
  title: "Arboreous Greenhouse"
  icon: "yet_another_industrialization:arboreous_greenhouse"
  position: 0
  parent: yet_another_industrialization:machines.md
item_ids:
  - yet_another_industrialization:arboreous_greenhouse
---

# Arboreous Greenhouse
###### *letitgrowleitgrow*

<GameScene zoom="1.5" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:arboreous_greenhouse" />
    <MultiblockShape controller="yet_another_industrialization:arboreous_greenhouse" useBigShape={true} x="-14" />
    <IsometricCamera yaw="190" pitch="15" />
</GameScene>

Arboreous Greenhouses are capable of growing trees or otherwise large plants.

<Recipe id="yet_another_industrialization:craft/arboreous_greenhouse" />

## Getting started

All recipes require a single sapling (or equivalent), a fluid and the appropriate multiblock soil:

<Recipe id="yet_another_industrialization:arboreous_greenhouse/minecraft/oak_sapling/regular" />

Naturally, different plants have different needs. Some might need Water and Sand while others require Lava and Netherrack. Always check your recipe viewer for accurate info!

Some also might have additional drops like fruits, roots, etc.

## Better yields

All outputs can be doubled by replacing the fluid used with its [nutrient variant](../utilities/fluids.md):

<Recipe id="yet_another_industrialization:arboreous_greenhouse/minecraft/oak_sapling/nutrient" />