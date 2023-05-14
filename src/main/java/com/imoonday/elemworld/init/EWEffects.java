package com.imoonday.elemworld.init;

import com.imoonday.elemworld.effects.DizzyEffect;
import com.imoonday.elemworld.effects.FreezeEffect;
import com.imoonday.elemworld.effects.FreezingResistanceEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorldData.addTranslation;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEffects {

    public static final StatusEffect FREEZE = registerEffect("freeze", new FreezeEffect(), "Frozen", "冰冻");
    public static final StatusEffect FREEZING_RESISTANCE = registerEffect("freezing_resistance", new FreezingResistanceEffect(), "Frozen Resistance", "冰冻耐性");
    public static final StatusEffect DIZZY = registerEffect("dizzy", new DizzyEffect(), "Dizziness", "晕眩");

    public static void register() {
        LOGGER.info("Loading Effects");
    }

    public static StatusEffect registerEffect(String id, StatusEffect effect, String en_us, String zh_cn) {
        addTranslation(effect, en_us, zh_cn);
        return Registry.register(Registries.STATUS_EFFECT, id(id), effect);
    }
}
