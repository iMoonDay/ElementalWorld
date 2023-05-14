package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Objects;

public class FireElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public FireElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireElementalEnergyBallEntity(LivingEntity owner, int power) {
        super(EWEntities.FIRE_ELEMENTAL_ENERGY_BALL, owner, power);
    }

    @Override
    public Element getElement() {
        return EWElements.FIRE;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        Entity owner = getOwner();
        for (Entity entity : world.getOtherEntities(owner, Box.from(this.getPos()).expand(power * 2), entity -> entity instanceof LivingEntity living && living.isAlive())) {
            LivingEntity living = (LivingEntity) entity;
            living.damage(Objects.requireNonNullElse(owner, this).getDamageSources().magic(), 1.0f * power);
            living.setOnFireFor(power * 2);
            this.getElement().addEffect(living, owner);
        }
        this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1.0, 0, 0);
        this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.VOICE);
    }
}
