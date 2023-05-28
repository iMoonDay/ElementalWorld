package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class ElectricEelEntity extends FishEntity implements BaseElement {
    public ElectricEelEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return null;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return null;
    }

    @Override
    public ItemStack getBucketItem() {
        return null;
    }
}
