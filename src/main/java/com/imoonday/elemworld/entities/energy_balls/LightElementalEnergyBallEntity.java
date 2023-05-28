package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LightElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public LightElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public LightElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.LIGHT_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.LIGHT;
    }
}
