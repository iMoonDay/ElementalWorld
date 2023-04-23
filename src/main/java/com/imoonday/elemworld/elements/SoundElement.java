package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.entity.damage.DamageTypes.SONIC_BOOM;
import static net.minecraft.entity.effect.StatusEffects.GLOWING;

public class SoundElement extends Element {
    public SoundElement(int level, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(level, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public float getMiningSpeedMultiplier (World world, LivingEntity entity, BlockState state){
        if (entity.isSubmergedInWater()) {
            return 1.5f;
        }
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageMultiplier (World world, LivingEntity entity, LivingEntity target){
        if (entity.isSubmergedInWater()) {
            return 1.5f;
        }
        return super.getDamageMultiplier(world, entity, target);
    }

    @Override
    public float getProtectionMultiplier (World world, LivingEntity entity){
        if (entity.isSubmergedInWater()) {
            return 1.5f;
        }
        return super.getProtectionMultiplier(world, entity);
    }

    @Override
    public boolean ignoreDamage (DamageSource source, LivingEntity entity){
        return source.isOf(SONIC_BOOM);
    }

    @Override
    public void tick (LivingEntity entity){
        List<Entity> otherEntities = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(15), entity1 -> entity1 instanceof LivingEntity);
        for (Entity otherEntity : otherEntities) {
            LivingEntity livingEntity = (LivingEntity) otherEntity;
            double speed = livingEntity.getSpeed();
            if (speed == 0) {
                continue;
            }
            livingEntity.addStatusEffect(new StatusEffectInstance(GLOWING, 2, 0, true, false, false));
        }
    }

    @Override
    public float getExtraDamage (LivingEntity target,float amount){
        if (target.getSpeed() > 0) {
            return amount;
        }
        return super.getExtraDamage(target, amount);
    }
}
