package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WaterElementalEnergyBallEntity;
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

public class WaterElementalStaffItem extends AbstractElementalStaffItem {

    public WaterElementalStaffItem() {
        super(128);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WATER;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new WaterElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 30 * 20));
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        lootables.put(LootTables.FISHING_TREASURE_GAMEPLAY, 0.0025f);
        return lootables;
    }
}
