---
navigation:
  title: "Большой блок хранения"
  icon: "yet_another_industrialization:large_storage_unit"
  position: 1
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:large_storage_unit
  - yet_another_industrialization:large_storage_unit_input_hatch
  - yet_another_industrialization:large_storage_unit_output_hatch
  - yet_another_industrialization:battery_casing
---

# Большой блок хранения
###### *Вот о какой жадности говорится в Библии, понимаете?*

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:large_storage_unit" />
    <MultiblockShape controller="yet_another_industrialization:large_storage_unit" useBigShape={true} x="-9" />
    <IsometricCamera yaw="190" pitch="15" />
</GameScene>

Большие блоки хранения вмещают в восемь раз больше энергии, чем обычный блок хранения того же напряжения. Это немало!

В отличие от других электрических машин, большие блоки хранения могут принимать и отдавать энергию своего и более низких напряжений. Например, блок уровня HV может обмениваться энергией с машинами уровней HV, MV и LV. Трансформаторы не нужны.

<Row>
    <Recipe id="yet_another_industrialization:craft/large_storage_unit" />

    <Recipe id="yet_another_industrialization:materials/battery_alloy/craft/machine_casing_special" />
</Row>

Приём и выдача энергии осуществляются через специальные люки:

<Row>
    <Recipe id="yet_another_industrialization:craft/large_storage_unit_input_hatch" />

    <Recipe id="yet_another_industrialization:craft/large_storage_unit_output_hatch" />
</Row>


