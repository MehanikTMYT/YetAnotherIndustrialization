---
navigation:
  title: "Облучатель ядерных стержней"
  icon: "yet_another_industrialization:nuclear_rod_irradiator"
  position: 2
  parent: yet_another_industrialization:machines.md
item_ids:
  - yet_another_industrialization:nuclear_rod_irradiator
---

# Облучатель ядерных стержней
###### *совсем не тот, что у вас дома*

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:nuclear_rod_irradiator" />

    <Block x="1" y="2" z="1" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="1" y="2" z="2" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="1" y="2" z="3" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="-1" y="2" z="1" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="-1" y="2" z="2" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="-1" y="2" z="3" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="0" y="2" z="1" id="modern_industrialization:nuclear_item_hatch" />
    <Block x="0" y="2" z="3" id="modern_industrialization:nuclear_item_hatch" />

    <Block x="0" y="2" z="2" id="modern_industrialization:bronze_item_input_hatch" />
</GameScene>

Облучатели ядерных стержней истощают ядерные топливные стержни. Они делают это быстрее и создают меньше нагрузки, чем обычные ядерные реакторы.

<Recipe id="yet_another_industrialization:craft/nuclear_rod_irradiator" />

<FloatingColumn align="right">
	<PaddedBox left="5" right="10" bottom="5">
		<ItemImage align="right" id="yet_another_industrialization:demon_core" scale="2" />
	</PaddedBox>
</FloatingColumn>

Для работы облучателям нужны EU и предмет — **источник нейтронов**. Такие источники обеспечивают необходимое для истощения облучение, но расходуются или постепенно изнашиваются, поэтому их приходится часто заменять. Подробности смотрите в просмотрщике рецептов.