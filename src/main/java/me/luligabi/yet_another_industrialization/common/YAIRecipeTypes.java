package me.luligabi.yet_another_industrialization.common;

import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions;
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseTierCondition;
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.EnergyGenerationCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

/**
 * port-26.1.2: прямая регистрация recipe-типов YAI и process-условий.
 *
 * Апстрим (Kotlin YAIMachines) регистрировал типы через tesseract MI-hook
 * (MachineRecipeTypesMIHookContext), но tesseract-api в этом дереве — compileOnly
 * (нет в рантайме, mods.toml optional), поэтому hook-механизм неприменим:
 * типы создаются напрямую ({@link MachineRecipeType} публичный конструктор),
 * serializer берётся из публичного поля {@code type.serializer}.
 *
 * Соответствие data-рецептам (проверено по data-каталогам src):
 *   cryogenic_precipitator (20), arboreous_greenhouse (598),
 *   pulse_detonation_generator (8), dragon_egg_energy_siphon (2).
 */
public final class YAIRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, YAI.ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, YAI.ID);

    // Флаги withXxx — по апстриму YAIMachines.recipeTypes(hook)
    public static final MachineRecipeType CRYOGENIC_PRECIPITATOR = register("cryogenic_precipitator",
            t -> t.withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs());
    public static final MachineRecipeType ARBOREOUS_GREENHOUSE = register("arboreous_greenhouse",
            t -> t.withItemInputs().withItemOutputs().withFluidInputs());
    public static final MachineRecipeType DRAGON_EGG_ENERGY_SIPHON = register("dragon_egg_energy_siphon",
            t -> t.withItemInputs().withFluidInputs().withFluidOutputs());
    public static final MachineRecipeType PULSE_DETONATION_GENERATOR = register("pulse_detonation_generator",
            t -> t.withItemInputs().withFluidInputs().withFluidOutputs());

    private YAIRecipeTypes() {
    }

    private static MachineRecipeType register(String id, UnaryOperator<MachineRecipeType> config) {
        MachineRecipeType type = config.apply(
                new MachineRecipeType(Identifier.fromNamespaceAndPath(YAI.ID, id)));
        RECIPE_TYPES.register(id, () -> type);
        RECIPE_SERIALIZERS.register(id, () -> type.serializer);
        return type;
    }

    public static void init(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        // Process-conditions (по образцу апстримного YAIMachines.init)
        MachineProcessConditions.register(
                Identifier.fromNamespaceAndPath(YAI.ID, "arboreous_greenhouse_tier"),
                ArboreousGreenhouseTierCondition.CODEC,
                ArboreousGreenhouseTierCondition.STREAM_CODEC);
        MachineProcessConditions.register(
                Identifier.fromNamespaceAndPath(YAI.ID, "energy_generation"),
                EnergyGenerationCondition.CODEC,
                EnergyGenerationCondition.STREAM_CODEC);
    }
}
