package com.imoonday.elemworld.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.NotNull;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWPotions {

    public static final Potion FREEZE = registerPotion("freeze", EWEffects.FREEZE);
    public static final Potion FREEZING_RESISTANCE = registerPotion("freezing_resistance", EWEffects.FREEZING_RESISTANCE);
    public static final Potion DIZZY = registerPotion("dizzy", EWEffects.DIZZY);
    public static int DEFAULT_DURATION = 60 * 20;

    public static void register() {
        LOGGER.info("Loading Potions");
    }

    public static Potion registerPotion(String id, StatusEffect effect, @NotNull Potion input, @NotNull Item item) {
        Potion potion = registerPotion(id, effect);
        BrewingRecipeRegistry.registerPotionRecipe(input, item, potion);
        return potion;
    }

    public static Potion registerPotion(String id, StatusEffect effect) {
        Potion potion = new Potion(new StatusEffectInstance(effect, DEFAULT_DURATION));
        return Registry.register(Registries.POTION, id(id), potion);
    }
}
