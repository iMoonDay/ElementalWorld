package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.FireElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class FireElementalStaffItem extends AbstractElementalStaffItem {

    public FireElementalStaffItem() {
        super(96);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.FIRE;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new FireElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 30));
    }

    @Override
    protected SoundEvent getSoundEvent(boolean isSneaking) {
        return isSneaking ? SoundEvents.ENTITY_GENERIC_DRINK : SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT;
    }

    @Override
    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        lootables.put(LootTables.NETHER_BRIDGE_CHEST, 0.05f);
        return lootables;
    }
}
