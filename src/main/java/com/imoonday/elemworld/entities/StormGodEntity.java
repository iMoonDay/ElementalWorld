package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.world.World;

public class StormGodEntity extends WitherEntity implements BaseElement {
    public StormGodEntity(EntityType<? extends WitherEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WIND;
    }
}
