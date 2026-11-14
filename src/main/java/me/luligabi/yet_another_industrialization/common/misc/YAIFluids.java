package me.luligabi.yet_another_industrialization.common.misc;

import aztech.modern_industrialization.fluid.MIBucketItem;
import aztech.modern_industrialization.fluid.MIFluid;
import aztech.modern_industrialization.fluid.MIFluidBlock;
import me.luligabi.yet_another_industrialization.common.YAI;
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks;
import me.luligabi.yet_another_industrialization.common.item.YAIItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike; // port-26.1.2: ItemLike переехал в world.level
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * port-26.1.2: перенос апстримного YAIFluids.kt.
 *
 * Апстрим шёл через tesseract MIFluidHolder, но в собранном tesseract его нет
 * (исключён из сборки, см. mi-stack/tesseract-neoforge/build.gradle), а
 * MIFluids.fluid(...) из MI регистрирует флюиды ПОД НЕЙМСПЕСОМ modern_industrialization
 * (DeferredRegister.create(Registries.FLUID, "modern_industrialization")) — это
 * неверно для YAI: data-рецепты ждут yet_another_industrialization:*.
 *
 * Поэтому регистрация своя (паттерн FluidDefinition из исходников MI-26,
 * но с нашими реестрами ns=yet_another_industrialization):
 * fluid -> fluidBlock -> bucket -> fluidType (лямбды читают поля позже, при вызове).
 */
public final class YAIFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, YAI.ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, YAI.ID);

    private static final class Entry {
        final String id;
        final int color;
        final boolean gas;
        DeferredHolder<Fluid, MIFluid> fluid;
        DeferredBlock<MIFluidBlock> block;
        DeferredHolder<Item, MIBucketItem> bucket;
        DeferredHolder<FluidType, FluidType> type;

        Entry(String id, int color, boolean gas) {
            this.id = id;
            this.color = color;
            this.gas = gas;
        }

        void register() {
            // порядок и лямбды — по FluidDefinition (MI-26)
            fluid = FLUIDS.register(id, () -> new MIFluid(block, bucketLike(), type, color));
            block = YAIBlocks.BLOCKS.registerBlock(id, p -> new MIFluidBlock(p, color));
            bucket = YAIItems.ITEMS.registerItem(id + "_bucket",
                    props -> new MIBucketItem(fluid.get(), color, props));
            type = FLUID_TYPES.register(id, () -> {
                FluidType.Properties props = FluidType.Properties.create()
                        .descriptionId(block.get().getDescriptionId());
                if (gas) {
                    props.density(-1000); // легче воздуха (по MI FluidDefinition)
                }
                return new FluidType(props);
            });
        }

        private ItemLike bucketLike() {
            return () -> bucket.get();
        }
    }

    // id/названия/цвета/свойства — дословно из апстримного YAIFluids.kt
    public static Entry NUTRIENT_RICH_WATER;
    public static Entry NUTRIENT_RICH_LAVA;
    public static Entry DRAGON_BREATH;
    public static Entry NUTRIENT_RICH_DRAGON_BREATH;
    public static Entry IMPURE_DRAGON_BREATH;
    public static Entry SCORCHING_LIQUID_AIR;
    public static Entry GELID_LIQUID_AIR;

    private YAIFluids() {
    }

    public static void init(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);

        NUTRIENT_RICH_WATER = new Entry("nutrient_rich_water", 0xAADCF9, false);
        NUTRIENT_RICH_LAVA = new Entry("nutrient_rich_lava", 0xA62530, false);
        DRAGON_BREATH = new Entry("dragon_breath", 0xCD75A4, true);
        NUTRIENT_RICH_DRAGON_BREATH = new Entry("nutrient_rich_dragon_breath", 0xAC2C7B, true);
        IMPURE_DRAGON_BREATH = new Entry("impure_dragon_breath", 0xC47E80, true);
        SCORCHING_LIQUID_AIR = new Entry("scorching_liquid_air", 0xFAB991, true);
        GELID_LIQUID_AIR = new Entry("gelid_liquid_air", 0x639FC3, true);

        NUTRIENT_RICH_WATER.register();
        NUTRIENT_RICH_LAVA.register();
        DRAGON_BREATH.register();
        NUTRIENT_RICH_DRAGON_BREATH.register();
        IMPURE_DRAGON_BREATH.register();
        SCORCHING_LIQUID_AIR.register();
        GELID_LIQUID_AIR.register();
    }
}
