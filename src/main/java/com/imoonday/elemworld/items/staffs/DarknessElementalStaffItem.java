package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.DarknessElementalEnergyBallEntity;
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

public class DarknessElementalStaffItem extends AbstractElementalStaffItem {

    public DarknessElementalStaffItem() {
        super(96);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.DARKNESS;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new DarknessElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.removeStatusEffect(StatusEffects.DARKNESS);
        user.removeStatusEffect(StatusEffects.BLINDNESS);
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 30 * 10));
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        lootables.put(EntityType.WITHER.getLootTableId(), 0.5f);
        return lootables;
    }

}
