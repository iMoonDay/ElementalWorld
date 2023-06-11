package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WindElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class WindElementalStaffItem extends AbstractElementalStaffItem {

    public WindElementalStaffItem() {
        super(192);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WIND;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new WindElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 30 * 20));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 30 * 20));
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        lootables.put(EntityType.ALLAY.getLootTableId(), 0.002f);
        return lootables;
    }

}
