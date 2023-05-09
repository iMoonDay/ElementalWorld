package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.EWRegister;
import net.minecraft.potion.Potion;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWPotions {

    public static final Potion FREEZE = EWRegister.registerPotion("freeze", EWEffects.FREEZE);
    public static final Potion FREEZING_RESISTANCE = EWRegister.registerPotion("freezing_resistance", EWEffects.FREEZING_RESISTANCE);
    public static final Potion DIZZY = EWRegister.registerPotion("dizzy", EWEffects.DIZZY);

    public static void register() {
        LOGGER.info("Loading Potions");
    }
}
