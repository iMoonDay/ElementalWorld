package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SoundElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public SoundElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public SoundElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.SOUND_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.SOUND;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(10, 0, entity -> defaultPredicate(entity) && isMoving(entity), this::addStatusEffects, false);
    }

    private boolean isMoving(LivingEntity entity){
        //entity.getVelocity().length() > 0.0784000015258789
        Vec3d vec3d = entity.getVelocity();
        return Math.abs(vec3d.x) < 0.003 && Math.abs(vec3d.y) < 0.003 && Math.abs(vec3d.z) < 0.003;
    }

    private void addStatusEffects(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 10 * 20));
        spawnSonic(entity);
    }

    private void spawnSonic(LivingEntity target) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        Vec3d vec3d = this.getPos().add(0.0, 1.6f, 0.0);
        Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
        Vec3d vec3d3 = vec3d2.normalize();
        for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
            Vec3d vec3d4 = vec3d.add(vec3d3.multiply(i));
            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
        this.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0f, 1.0f);
        target.damage(serverWorld.getDamageSources().sonicBoom(this), 10.0f);
        double d = 0.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        double e = 2.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
    }
}
