package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class WindElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public WindElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindElementalEnergyBallEntity(LivingEntity owner, ItemStack stack, int power) {
        super(EWEntities.WIND_ELEMENTAL_ENERGY_BALL, owner, stack, power);
    }

    @Override
    public Element getElement() {
        return EWElements.WIND;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(power * 2, 0.5f * power, entity -> entity.setVelocity(0, 0.5 * power, 0));
    }
}
