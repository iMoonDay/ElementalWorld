package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.EWRegister;
import com.imoonday.elemworld.effects.DizzyEffect;
import com.imoonday.elemworld.effects.FreezeEffect;
import com.imoonday.elemworld.effects.FreezingResistanceEffect;
import net.minecraft.entity.effect.StatusEffect;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWEffects {

    public static final StatusEffect FREEZE = EWRegister.registerEffect("freeze", new FreezeEffect());
    public static final StatusEffect FREEZING_RESISTANCE = EWRegister.registerEffect("freezing_resistance", new FreezingResistanceEffect());
    public static final StatusEffect DIZZY = EWRegister.registerEffect("dizzy", new DizzyEffect());

    public static void register() {
        LOGGER.info("Loading Effects");
    }
}
