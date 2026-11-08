---
navigation:
  title: "Large Storage Unit"
  icon: "yet_another_industrialization:large_storage_unit"
  position: 1
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:large_storage_unit
  - yet_another_industrialization:large_storage_unit_input_hatch
  - yet_another_industrialization:large_storage_unit_output_hatch
  - yet_another_industrialization:battery_casing
---

# Large Storage Unit
###### *This is the kind of greed they talk about in the Bible, y' know?*

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:large_storage_unit" />
    <MultiblockShape controller="yet_another_industrialization:large_storage_unit" useBigShape={true} x="-9" />
    <IsometricCamera yaw="190" pitch="15" />
</GameScene>

Large Storage Units are capable of storing eight times more than their voltage's storage unit. That's a lot!

Unlike other electric machines, LSUs can I/O to its voltage and lower ones. i.e. At HV it's capable of performing I/O to other machines at its voltage, MV and LV. No transformers required.

<Row>
    <Recipe id="yet_another_industrialization:craft/large_storage_unit" />

    <Recipe id="yet_another_industrialization:materials/battery_alloy/craft/machine_casing_special" />
</Row>

Energy I/O is done through special hatches:

<Row>
    <Recipe id="yet_another_industrialization:craft/large_storage_unit_input_hatch" />

    <Recipe id="yet_another_industrialization:craft/large_storage_unit_output_hatch" />
</Row>


