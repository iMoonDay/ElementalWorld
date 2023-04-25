package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imoonday.elemworld.init.EWElements.FIRE;
import static net.minecraft.entity.effect.StatusEffects.JUMP_BOOST;
import static net.minecraft.entity.effect.StatusEffects.SPEED;
import static net.minecraft.registry.tag.DamageTypeTags.IS_FALL;

public class WindElement extends Element {
    public WindElement(int maxLevel, int rareLevel, int weight) {
        super(maxLevel, rareLevel, weight);
    }

    @Override
    public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
        return source.isIn(IS_FALL);
    }

    @Override
    public void postHit(LivingEntity target, PlayerEntity attacker) {
        List<LivingEntity> entities = target.world.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(3), Entity::isLiving);
        for (LivingEntity entity : entities) {
            if (target.hasEffectOf(FIRE)) {
                FIRE.addEffect(entity, attacker);
            }
            Vec3d subtract = entity.getPos().subtract(attacker.getPos()).normalize();
            Vec3d vec3d = new Vec3d(subtract.x, 1, subtract.z);
            entity.setVelocity(vec3d);
        }
    }

    @Override
    public Map<StatusEffect, Integer> getPersistentEffects() {
        Map<StatusEffect, Integer> effects = new HashMap<>();
        effects.put(JUMP_BOOST, 0);
        effects.put(SPEED, 0);
        return effects;
    }
}
