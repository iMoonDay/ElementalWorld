package com.imoonday.elemworld.init;

import com.imoonday.elemworld.effects.DizzyEffect;
import com.imoonday.elemworld.effects.FreezeEffect;
import com.imoonday.elemworld.effects.FreezingResistanceEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEffects {

    public static final StatusEffect FREEZE = register("freeze", new FreezeEffect());
    public static final StatusEffect FREEZING_RESISTANCE = register("freezing_resistance", new FreezingResistanceEffect());
    public static final StatusEffect DIZZY = register("dizzy", new DizzyEffect());

    public static void register() {
        LOGGER.info("Loading Effects");
    }

    static StatusEffect register(String id, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, id(id), effect);
    }
}
