---
navigation:
  title: "Импульсно-детонационный генератор"
  icon: "yet_another_industrialization:pulse_detonation_generator"
  position: 15
  parent: yet_another_industrialization:generators.md
item_ids:
  - yet_another_industrialization:pulse_detonation_generator
---

# Импульсно-детонационный генератор
###### *Держитесь на безопасном расстоянии!*

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:pulse_detonation_generator" />
</GameScene>

Использует энергию ударной волны управляемых взрывов, создаваемых искусственной взрывчаткой и жидким реагентом.

<Row>
    <Recipe id="yet_another_industrialization:craft/pulse_detonation_generator" />

    <Recipe id="yet_another_industrialization:craft/detonation_chamber_casing" />
</Row>



<Column>
    ## Рецепты с промышленным динамитом

    <Row>
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/industrial_tnt/8" />
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/industrial_tnt/16" />
    </Row>
    <Row>
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/industrial_tnt/32" />
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/industrial_tnt/64" />
    </Row>

    ## Рецепты с ядерной бомбой

    <Row>
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/nuke/8" />
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/nuke/16" />
    </Row>
    <Row>
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/nuke/32" />
        <Recipe id="yet_another_industrialization:pulse_detonation_generator/nuke/64" />
    </Row>
</Column>