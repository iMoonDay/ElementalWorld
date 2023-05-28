package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WolverineEntity extends TameableEntity implements BaseElement {
    public WolverineEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.GOLD;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public EntityView method_48926() {
        return this.world;
    }
}
