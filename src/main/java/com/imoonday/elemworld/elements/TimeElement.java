package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

public class TimeElement extends Element {
    public TimeElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float armorMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, armorMultiplier, durabilityMultiplier);
    }

    @Override
    public boolean immuneOnDeath(LivingEntity entity){
        if (entity.getImmuneCooldown() <= 0) {
            entity.setImmuneCooldown(5 * 60 * 20);
            entity.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f);
            return true;
        }
        return super.immuneOnDeath(entity);
    }

    @Override
    public void afterInjury (LivingEntity entity, DamageSource source, float amount){
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
