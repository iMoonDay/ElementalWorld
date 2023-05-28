package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.world.World;

public class JellyfishEntity extends WaterCreatureEntity implements BaseElement {
    public JellyfishEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WATER;
    }
}
