package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

public class SongBirdEntity extends ParrotEntity implements BaseElement {
    public SongBirdEntity(EntityType<? extends ParrotEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.SOUND;
    }

    @Override
    public EntityView method_48926() {
        return this.world;
    }
}
