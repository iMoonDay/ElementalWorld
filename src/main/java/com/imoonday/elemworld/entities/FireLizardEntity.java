package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class FireLizardEntity extends HostileEntity implements BaseElement {
    public FireLizardEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.FIRE;
    }
}
