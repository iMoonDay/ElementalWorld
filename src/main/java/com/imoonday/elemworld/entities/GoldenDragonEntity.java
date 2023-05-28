package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.world.World;

public class GoldenDragonEntity extends EnderDragonEntity implements BaseElement {
    public GoldenDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.GOLD;
    }
}
