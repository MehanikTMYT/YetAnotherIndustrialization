---
navigation:
  title: "Пилон полёта"
  icon: "yet_another_industrialization:flight_pylon"
  position: 0
  parent: yet_another_industrialization:utilities.md
item_ids:
  - yet_another_industrialization:flight_pylon
---

# Пилон полёта
###### *Мечта Икара*

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:flight_pylon" />
    <MultiblockShape controller="yet_another_industrialization:flight_pylon" useBigShape={true} x="-7" />
    <IsometricCamera yaw="190" pitch="15" />
</GameScene>

Пилоны полёта работают как электрические маяки... если бы маяки давали «только» творческий полёт. При этом пилонам полёта не нужен прямой доступ к небу, а их луч можно отключить в интерфейсе.

Обычно они становятся доступны на этапе MV:

<Recipe id="yet_another_industrialization:craft/flight_pylon_jetpack" />

Однако с помощью элитр (которые можно копировать, используя [драконье дыхание](../utilities/fluids.md)) пилон можно изготовить уже на этапе LV:

<Row>
    <Recipe id="yet_another_industrialization:craft/flight_pylon_elytra" />
    <Recipe id="yet_another_industrialization:mixer/elytra_duplication" />
</Row>

У пилонов несколько уровней: с каждым увеличиваются радиус действия и расход EU. Подробности смотрите в просмотрщике рецептов.

Для наглядности ниже показаны радиусы всех стандартных уровней (24, 48, 72, 96 и 128 блоков соответственно):

![Радиусы действия уровней пилона полёта](../assets/flight_pylon_tier_ranges.png)
