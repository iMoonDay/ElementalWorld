package com.imoonday.elemworld.init;

import com.google.common.collect.ImmutableList;
import com.imoonday.elemworld.ElementalWorldData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWPotions {

    private static final List<Potion> POTIONS = new ArrayList<>();

    public static int DEFAULT_DURATION = 60 * 20;

    public static final Potion FREEZE = registerPotion("freeze", EWEffects.FREEZE, Potions.WATER, EWElements.ICE.getFragmentItem());
    public static final Potion FREEZING_RESISTANCE = registerPotion("freezing_resistance", EWEffects.FREEZING_RESISTANCE, Potions.AWKWARD, EWElements.FIRE.getFragmentItem());
    public static final Potion DIZZY = registerPotion("dizzy", EWEffects.DIZZY);
    public static final Potion WATER_FEARING = registerPotion("water_fearing", EWEffects.WATER_FEARING);
    public static final Potion PERSPECTIVE = registerPotion("perspective", EWEffects.PERSPECTIVE);
    public static final Potion ROBBERY = registerPotion("robbery", EWEffects.ROBBERY);
    public static final Potion NO_FRIENDLY_HURT = registerPotion("no_friendly_hurt", EWEffects.NO_FRIENDLY_HURT);
    public static final Potion LIGHT_FEARING = registerPotion("light_fearing", EWEffects.LIGHT_FEARING);

    public static void register() {
        LOGGER.info("Loading Potions");
    }

    public static Potion registerPotion(String id, StatusEffect effect, @NotNull Potion input, @NotNull Item item) {
        Potion potion = registerPotion(id, effect);
        BrewingRecipeRegistry.registerPotionRecipe(input, item, potion);
        return potion;
    }

    public static Potion registerPotion(String id, StatusEffect effect) {
        Potion potion = new Potion(new StatusEffectInstance(effect, effect.isInstant() ? 1 : DEFAULT_DURATION));
        ElementalWorldData.getTranslation(effect).ifPresent(translation -> {
            String en_us = translation.get();
            String zh_cn = translation.get("zh_cn");
            ElementalWorldData.addTranslation(Items.POTION.getTranslationKey() + ".effect." + id, "Potion of " + en_us, zh_cn + "药水");
            ElementalWorldData.addTranslation(Items.SPLASH_POTION.getTranslationKey() + ".effect." + id, "Splash Potion of " + en_us, "喷溅型" + zh_cn + "药水");
            ElementalWorldData.addTranslation(Items.LINGERING_POTION.getTranslationKey() + ".effect." + id, "Lingering Potion of " + en_us, "滞留型" + zh_cn + "药水");
            ElementalWorldData.addTranslation(Items.TIPPED_ARROW.getTranslationKey() + ".effect." + id, "Arrow of " + en_us, zh_cn + "之箭");
        });
        addPotion(potion);
        return Registry.register(Registries.POTION, id(id), potion);
    }

    public static void addPotion(Potion potion) {
        POTIONS.add(potion);
    }

    public static ImmutableList<Potion> getPotions() {
        return ImmutableList.copyOf(POTIONS);
    }
}
