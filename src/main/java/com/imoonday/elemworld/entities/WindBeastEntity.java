package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.world.World;

public class WindBeastEntity extends FoxEntity implements BaseElement {
    public WindBeastEntity(EntityType<? extends FoxEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WIND;
    }
}
