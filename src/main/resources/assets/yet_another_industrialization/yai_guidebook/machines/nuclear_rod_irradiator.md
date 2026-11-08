---
navigation:
  title: "Nuclear Rod Irradiator"
  icon: "yet_another_industrialization:nuclear_rod_irradiator"
  position: 2
  parent: yet_another_industrialization:machines.md
item_ids:
  - yet_another_industrialization:nuclear_rod_irradiator
---

# Nuclear Rod Irradiator
###### *not the one you got at home*

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

Nuclear Rod Irradiators are capable of depleting nuclear fuel rods. They are both quicker and less laggy than using regular Nuclear Reactors for this goal.

<Recipe id="yet_another_industrialization:craft/nuclear_rod_irradiator" />

<FloatingColumn align="right">
	<PaddedBox left="5" right="10" bottom="5">
		<ItemImage align="right" id="yet_another_industrialization:demon_core" scale="2" />
	</PaddedBox>
</FloatingColumn>

To work, NRIs require EU and a **Neutron Source** item. These provide the depletion power required, but will be consumed or otherwise degrade overtime, needing to be replaced frequently. Check your recipe viewer for more details on them.