package com.imoonday.elemworld.init;

import com.imoonday.elemworld.effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorldData.addTranslation;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEffects {

    public static final StatusEffect FREEZE = register("freeze", new FreezeEffect(), "Frozen", "冰冻");
    public static final StatusEffect FREEZING_RESISTANCE = register("freezing_resistance", new FreezingResistanceEffect(), "Frozen Resistance", "冰冻耐性");
    public static final StatusEffect DIZZY = register("dizzy", new DizzyEffect(), "Dizziness", "晕眩");
    public static final StatusEffect WATER_FEARING = register("water_fearing", new WaterFearingEffect(), "Water Fearing", "怕水");
    public static final StatusEffect PERSPECTIVE = register("perspective", new PerspectiveEffect(), "Perspective", "透视");
    public static final StatusEffect ROBBERY = register("robbery", new RobberyEffect(), "Robbery", "抢劫");
    public static final StatusEffect NO_FRIENDLY_HURT = register("no_friendly_hurt", new NoFriendlyHurtEffect(), "No Friendly Hurt", "友伤消除");

    public static void register() {
        LOGGER.info("Loading Effects");
    }

    public static StatusEffect register(String id, StatusEffect effect, String en_us, String zh_cn) {
        addTranslation(effect, en_us, zh_cn);
        return Registry.register(Registries.STATUS_EFFECT, id(id), effect);
    }
}
