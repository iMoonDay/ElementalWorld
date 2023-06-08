package com.imoonday.elemworld.elements;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

import static net.minecraft.entity.damage.DamageTypes.SONIC_BOOM;
import static net.minecraft.entity.effect.StatusEffects.GLOWING;

public class SoundElement extends Element {
    public SoundElement() {
        super(1, 3, 5, 0.75f, 0.75f, 0.25f, 0.0f);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        if (entity.isSubmergedInWater()) {
            return 0.5f;
        }
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        if (entity.isSubmergedInWater()) {
            return 0.5f;
        }
        return super.getDamageMultiplier(world, entity, target);
    }

    @Override
    public float getMaxHealthMultiplier(World world, LivingEntity entity) {
        if (entity.isSubmergedInWater()) {
            return 0.25f;
        }
        return super.getMaxHealthMultiplier(world, entity);
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return source.isOf(SONIC_BOOM) ? 0.2f : 1.0f;
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }

    @Override
    public void tick(LivingEntity entity) {
        List<Entity> otherEntities = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(15), Entity::isLiving);
        for (Entity otherEntity : otherEntities) {
            if (otherEntity instanceof PlayerEntity) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) otherEntity;
            double speed = livingEntity.getSpeed();
            if (speed != 0) {
                livingEntity.addStatusEffect(new StatusEffectInstance(GLOWING, 2, 0, true, false, false));
            }
        }
    }

    @Override
    public float getExtraDamage(LivingEntity target, float amount) {
        if (target.getSpeed() > 0) {
            return amount;
        }
        return super.getExtraDamage(target, amount);
    }
}
