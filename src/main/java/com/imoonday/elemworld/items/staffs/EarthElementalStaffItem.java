package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.EarthElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class EarthElementalStaffItem extends AbstractElementalStaffItem {

    public EarthElementalStaffItem() {
        super(256);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.EARTH;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new EarthElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 30 * 20));
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        lootables.put(LootTables.VILLAGE_DESERT_HOUSE_CHEST, 0.02f);
        return lootables;
    }

}
