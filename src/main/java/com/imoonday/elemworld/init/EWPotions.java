package com.imoonday.elemworld.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWPotions {

    public static final int DEFAULT_DURATION = 60 * 20;
    public static final Item[] ITEMS = {Items.TIPPED_ARROW, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};

    public static final Potion FREEZE = register("freeze", EWEffects.FREEZE);
    public static final Potion FREEZING_RESISTANCE = register("freezing_resistance", EWEffects.FREEZING_RESISTANCE);
    public static final Potion DIZZY = register("dizzy", EWEffects.DIZZY);

    public static void register() {
        LOGGER.info("Loading Potions");
    }

    static Potion register(String id, StatusEffect effect, Potion neededPotion, Item neededItem) {
        Potion potion = new Potion(new StatusEffectInstance(effect, DEFAULT_DURATION));
//        Arrays.stream(ITEMS).toList().forEach(item -> ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(PotionUtil.setPotion(new ItemStack(item), potion))));
        if (neededPotion != null && neededItem != null) {
            BrewingRecipeRegistry.registerPotionRecipe(neededPotion, neededItem, potion);
        }
        return Registry.register(Registries.POTION, id(id), potion);
    }

    static Potion register(String id, StatusEffect effect) {
        Potion potion = new Potion(new StatusEffectInstance(effect, DEFAULT_DURATION));
//        Arrays.stream(ITEMS).toList().forEach(item -> ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(PotionUtil.setPotion(new ItemStack(item), potion))));
        return Registry.register(Registries.POTION, id(id), potion);
    }
}
