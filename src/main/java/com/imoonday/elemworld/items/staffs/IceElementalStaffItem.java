package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.IceElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class IceElementalStaffItem extends AbstractElementalStaffItem {

    public IceElementalStaffItem(){
        super(192);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.ICE;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new IceElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {

    }

    @Override
    protected SoundEvent getSoundEvent(boolean isSneaking) {
        return null;
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        return lootables;
    }
}
