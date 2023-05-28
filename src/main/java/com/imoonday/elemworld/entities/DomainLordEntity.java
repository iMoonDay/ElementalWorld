package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.world.World;

public class DomainLordEntity extends GhastEntity implements BaseElement {
    public DomainLordEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.EARTH;
    }
}
