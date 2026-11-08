---
navigation:
  title: "Trash Can Hatches"
  icon: "yet_another_industrialization:fluid_trash_can_hatch"
  position: 4
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:item_trash_can_hatch
  - yet_another_industrialization:fluid_trash_can_hatch
---

# Trash Can Hatches
###### *1, 2, 3... and poof!*

<GameScene zoom="4" interactive={true} fullWidth={true}>
    <Block id="yet_another_industrialization:item_trash_can_hatch" x="-1" />
    <Block id="yet_another_industrialization:fluid_trash_can_hatch" />
    <IsometricCamera yaw="0" pitch="0" />
</GameScene>

Special output hatches that automatically void anything as soon as it's outputted. Not any different from placing an Automatic Trash Can and enabling auto output, but I mean, it's neat, right?

<Row>
    <Recipe id="yet_another_industrialization:craft/item_trash_can_hatch" />

    <Recipe id="yet_another_industrialization:craft/fluid_trash_can_hatch" />
</Row>
__**WARNING: These hatches have no priority over regular hatches. Make sure to properly lock all slots or risk having precious output thrown out!**__
