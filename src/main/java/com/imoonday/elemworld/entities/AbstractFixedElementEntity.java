package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.interfaces.FixedElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public abstract class AbstractFixedElementEntity extends PathAwareEntity implements FixedElement {

    protected AbstractFixedElementEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getElements().stream().noneMatch(entry -> entry.element().isOf(getElement()))) {
            this.addElement(getElement().withRandomLevel());
        }
    }
}
