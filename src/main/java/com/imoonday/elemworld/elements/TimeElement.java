package com.imoonday.elemworld.elements;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.random.Random;

import java.awt.*;

public class TimeElement extends Element {
    public TimeElement() {
        super(1, 3, 5, 0.75f, 0.75f, 0.25f, 0.0f);
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

    @Override
    public boolean immuneDamageOnDeath(LivingEntity entity) {
        if (entity.getImmuneCooldown() <= 0) {
            entity.setImmuneCooldown(5 * 60 * 20);
            entity.world.sendEntityStatus(entity, EntityStatuses.USE_TOTEM_OF_UNDYING);
            return true;
        }
        return super.immuneDamageOnDeath(entity);
    }

    @Override
    public void afterInjury(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getImmuneCooldown() != 0) {
            return;
        }
        Random random = entity.getRandom();
        if (random.nextFloat() < 0.0625f) {
            entity.setHealth(entity.getMaxHealth());
        } else if (entity.getHealth() / entity.getMaxHealth() <= 0.2f) {
            if (random.nextFloat() < 0.25f) {
                entity.setHealth(entity.getMaxHealth() / 2f);
            }
        }
    }
}
