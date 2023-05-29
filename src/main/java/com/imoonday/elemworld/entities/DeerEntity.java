package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

public class DeerEntity extends HorseEntity implements BaseElement {
    public DeerEntity(EntityType<? extends HorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.GRASS;
    }

    @Override
    public EntityView method_48926() {
        return this.world;
    }
}
