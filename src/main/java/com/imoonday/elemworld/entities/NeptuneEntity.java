package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class NeptuneEntity extends PathAwareEntity implements BaseElement {
    public NeptuneEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WATER;
    }
}
