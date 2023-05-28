package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;

public class MudMonsterEntity extends SlimeEntity implements BaseElement {
    public MudMonsterEntity(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.EARTH;
    }
}
