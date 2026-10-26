package me.luligabi.yet_another_industrialization;
import static me.luligabi.yet_another_industrialization.common.item.YAIItems.*;

import me.luligabi.yet_another_industrialization.common.item.tools.*;
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public final class ItemChecks {
    private static int checks;
    public static void register(IEventBus bus) { me.luligabi.yet_another_industrialization.common.item.YAIItems.init(bus); }
    private static void check(boolean condition, String message) {
        checks++; if (!condition) throw new AssertionError(message);
    }
    public static void run(MinecraftServer server) {
        var slop = AI_SLOP.toStack();
        check(slop.get(DataComponents.FOOD).nutrition() == 4, "slop nutrition");
        check(Math.abs(slop.get(DataComponents.FOOD).saturation() - 3.2f) < 0.001f, "slop saturation");
        var drink = CACHACA.toStack();
        var consumable = drink.get(DataComponents.CONSUMABLE);
        check(drink.getMaxStackSize() == 16, "drink stack limit");
        check(drink.get(DataComponents.FOOD).canAlwaysEat(), "drink when full");
        check(drink.get(DataComponents.FOOD).nutrition() == 4, "drink nutrition");
        check(Math.abs(drink.get(DataComponents.FOOD).saturation() - 6.4f) < 0.001f, "drink saturation");
        check(consumable.consumeSeconds() == 0.8f && consumable.animation() == ItemUseAnimation.DRINK, "fast drink");
        var nausea = (ApplyStatusEffectsConsumeEffect) consumable.onConsumeEffects().getFirst();
        check(nausea.probability() == 0.35f, "nausea probability");
        check(nausea.effects().getFirst().getEffect().equals(MobEffects.NAUSEA)
            && nausea.effects().getFirst().getDuration() == 160 && nausea.effects().getFirst().getAmplifier() == 1, "nausea effect");
        var burger = ULTRADENSE_METAL_BALL_BURGER.toStack();
        check(burger.get(DataComponents.FOOD).nutrition() == 5 && burger.get(DataComponents.FOOD).saturation() == 10, "burger nutrition");
        var slow = (ApplyStatusEffectsConsumeEffect) burger.get(DataComponents.CONSUMABLE).onConsumeEffects().getFirst();
        check(slow.probability() == 0.1f && slow.effects().getFirst().getEffect().equals(MobEffects.SLOWNESS)
            && slow.effects().getFirst().getDuration() == 160 && slow.effects().getFirst().getAmplifier() == 2, "burger slowness");
        var player = FakePlayerFactory.getMinecraft(server.overworld());
        player.getAbilities().instabuild = false;
        check(drink.finishUsingItem(server.overworld(), player).is(Items.GLASS_BOTTLE), "consuming last drink returns bottle");
        var core = DEMON_CORE.toStack();
        var mending = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING);
        check(core.getMaxDamage() == 320, "core durability");
        check(!core.supportsEnchantment(mending) && !core.isPrimaryItemFor(mending), "core rejects book and table");
        check(!core.has(DataComponents.ENCHANTABLE), "core has no enchanting component");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        check(IndustrialistsGogglesItem.toggleMode(player) == null, "toggle without goggles");
        var goggles = INDUSTRIALISTS_GOGGLES.toStack();
        var copy = goggles.copy();
        check(goggles.get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.HEAD && goggles.getMaxStackSize() == 1, "head equipment");
        check(goggles.get(YAIDataComponents.GOGGLES_ENABLED.get()), "goggles enabled by default");
        player.setItemSlot(EquipmentSlot.HEAD, goggles);
        check(Boolean.FALSE.equals(IndustrialistsGogglesItem.toggleMode(player)), "disable goggles");
        check(copy.get(YAIDataComponents.GOGGLES_ENABLED.get()), "toggle leaves copied stack unchanged");
        check(Boolean.TRUE.equals(IndustrialistsGogglesItem.toggleMode(player)), "enable goggles");
        check(GUIDEBOOK.toStack().getCount() == 1, "guide item registration");
        System.out.println("YAI_ITEMS_PASS checks=" + checks);
    }
}
