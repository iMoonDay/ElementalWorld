package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WoodElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class WoodElementalStaffItem extends AbstractElementalStaffItem {

    public WoodElementalStaffItem() {
        super(64);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.WOOD;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new WoodElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 30 * 20));
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        lootables.put(Blocks.OAK_LOG.getLootTableId(), 0.00125f);
        return lootables;
    }

}
