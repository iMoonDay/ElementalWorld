package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;

public class ThunderGodEntity extends ZombieEntity implements BaseElement {
    public ThunderGodEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.THUNDER;
    }
}
