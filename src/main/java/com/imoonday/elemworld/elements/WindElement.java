package com.imoonday.elemworld.elements;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.imoonday.elemworld.init.EWElements.FIRE;
import static net.minecraft.entity.effect.StatusEffects.JUMP_BOOST;
import static net.minecraft.entity.effect.StatusEffects.SPEED;
import static net.minecraft.registry.tag.DamageTypeTags.IS_FALL;

public class WindElement extends Element {
    public WindElement() {
        super(2, 2, 20);
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return source.isIn(IS_FALL) ? 0.2f : 1.0f;
    }

    @Override
    public void postHit(LivingEntity target, PlayerEntity attacker) {
        List<Entity> entities = target.world.getOtherEntities(attacker, target.getBoundingBox().expand(3), Entity::isLiving);
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (target.hasEffectOf(FIRE)) {
                FIRE.addEffect(living, attacker);
            }
            Vec3d subtract = living.getPos().subtract(attacker.getPos()).normalize();
            Vec3d vec3d = new Vec3d(subtract.x, 1, subtract.z);
            living.setVelocity(vec3d);
        }
    }

    @Override
    public Map<StatusEffect, Integer> getPersistentEffects(Map<StatusEffect, Integer> effects) {
        effects.put(JUMP_BOOST, 0);
        effects.put(SPEED, 0);
        return effects;
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }
}
