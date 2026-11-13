---
navigation:
  title: "Мусорные люки"
  icon: "yet_another_industrialization:fluid_trash_can_hatch"
  position: 4
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:item_trash_can_hatch
  - yet_another_industrialization:fluid_trash_can_hatch
---

# Мусорные люки
###### *1, 2, 3... и нету!*

<GameScene zoom="4" interactive={true} fullWidth={true}>
    <Block id="yet_another_industrialization:item_trash_can_hatch" x="-1" />
    <Block id="yet_another_industrialization:fluid_trash_can_hatch" />
    <IsometricCamera yaw="0" pitch="0" />
</GameScene>

Специальные выходные люки, которые сразу уничтожают всё поступающее в них. По сути, то же самое, что автоматическая мусорная корзина с включённым автовыводом, но ведь удобно, правда?

<Row>
    <Recipe id="yet_another_industrialization:craft/item_trash_can_hatch" />

    <Recipe id="yet_another_industrialization:craft/fluid_trash_can_hatch" />
</Row>
__**ВНИМАНИЕ: У этих люков нет приоритета над обычными. Обязательно правильно заблокируйте все слоты, иначе ценные продукты могут отправиться в мусор!**__
