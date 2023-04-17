package com.imoonday.elemworld.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.id;

public class EWEffects {

    static StatusEffect register(String id, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, id(id), effect);
    }
}
