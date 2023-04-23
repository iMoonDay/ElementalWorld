package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.registry.tag.DamageTypeTags.IS_LIGHTNING;
import static com.imoonday.elemworld.init.EWElements.*;

public class ThunderElement extends Element {
    public ThunderElement(int level, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(level, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public boolean ignoreDamage (DamageSource source, LivingEntity entity){
        return source.isIn(IS_LIGHTNING);
    }

    @Override
    public void postHit (LivingEntity target, PlayerEntity attacker){
        if (target.isIn(WATER)) {
            HashSet<Entity> entities = new HashSet<>(getEntitiesNearby(target));
            HashSet<Entity> otherEntities = entities.stream().flatMap(entity1 -> new HashSet<>(getEntitiesNearby(entity1)).stream()).collect(Collectors.toCollection(HashSet::new));
            entities.addAll(otherEntities);
            for (Entity entity : entities) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.damage(attacker.getDamageSources().magic(), 2)) {
                    livingEntity.removeEffectOf(WATER);
                    livingEntity.world.playSound(null, livingEntity.getBlockPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.VOICE);
                }
            }
        } else if (target.isIn(FIRE)) {
            target.world.createExplosion(target, attacker.getDamageSources().explosion(attacker, attacker), new ExplosionBehavior(), target.getX(), target.getY(), target.getZ(), 2, false, World.ExplosionSourceType.NONE);
            target.removeEffectOf(FIRE);
            target.setOnFire(false);
        }
    }

    private static List<Entity> getEntitiesNearby (Entity entity){
        return entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(5), entity1 -> entity1 instanceof LivingEntity livingEntity && !livingEntity.hasOneOf(EARTH, THUNDER) && livingEntity.hasElement(WATER));
    }

    @Override
    public boolean shouldAddEffectAfterInjury (LivingEntity entity, DamageSource source, float amount){
        return source.isIn(IS_LIGHTNING);
    }
}