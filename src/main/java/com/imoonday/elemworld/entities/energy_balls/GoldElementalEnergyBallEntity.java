package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GoldElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {
    public GoldElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public GoldElementalEnergyBallEntity(LivingEntity owner, ItemStack stack, int power) {
        super(EWEntities.GOLD_ELEMENTAL_ENERGY_BALL, owner, stack, power);
    }

    @Override
    public Element getElement() {
        return EWElements.GOLD;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(power * 2, 0.5f * power, entity -> {
            Vec3d vec3d = entity.getPos().subtract(this.getPos()).normalize().multiply(power);
            entity.setVelocity(vec3d.x, 0.5 + 0.1 * power, vec3d.z);
        });
    }
}
