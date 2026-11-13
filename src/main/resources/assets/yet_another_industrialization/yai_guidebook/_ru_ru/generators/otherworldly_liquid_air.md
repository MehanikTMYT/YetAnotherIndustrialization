---
navigation:
  title: "Жидкий воздух иных миров"
  icon: "yet_another_industrialization:tempproof_air_intake"
  position: 10
  parent: yet_another_industrialization:generators.md
item_ids:
  - yet_another_industrialization:tempproof_air_intake
  - yet_another_industrialization:scorching_liquid_air_bucket
  - yet_another_industrialization:gelid_liquid_air_bucket
---

# Жидкий воздух иных миров
###### *то в жар, то в холод*

**ВНИМАНИЕ: Для работы с этим содержимым нужны сторонний мод для переноса жидкостей между измерениями и ещё один для постоянной загрузки чанков. Извините за неудобства!**

Состав воздуха не везде одинаков. Используя воздух других измерений, можно вырабатывать энергию и извлекать ресурсы!

Суровый климат иных миров мешает обычным воздухозаборникам работать должным образом. Вместо них используйте **термостойкие воздухозаборники**:

<RecipeFor id="yet_another_industrialization:tempproof_air_intake" />

В раскалённых измерениях, таких как **Незер**, можно добывать **обжигающий жидкий воздух**, расходуя небольшое количество **криожидкости**.

<Recipe id="yet_another_industrialization:pressurizer/scorching_liquid_air" />

В ледяных измерениях, таких как **Энд**, можно добывать **ледяной жидкий воздух**, расходуя небольшое количество **лавы**.

<Recipe id="yet_another_industrialization:pressurizer/gelid_liquid_air" />

## Выработка энергии

Соедините **обжигающий** и **ледяной** жидкий воздух в **теплообменнике**, чтобы получить уйму **пара**!

<Recipe id="yet_another_industrialization:heat_exchanger/liquid_air_to_steam" />

## Центрифугирование

Жидкий воздух можно перерабатывать в центрифуге, получая небольшие количества ресурсов:

<Row>
    <Recipe id="yet_another_industrialization:centrifuge/scorching_liquid_air_extract" />
    <Recipe id="yet_another_industrialization:centrifuge/gelid_liquid_air_extract" />
</Row>