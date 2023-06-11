package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.GrassElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWItems;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GrassElementalStaffItem extends AbstractElementalStaffItem {

    public GrassElementalStaffItem() {
        super(128);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.GRASS;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new GrassElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 5 * 20, 1));
    }

    public static void trySpawnGrassElementalStaff(World world, BlockPos pos) {
        if (world.random.nextFloat() < 0.0075f) {
            Vec3d vec3d = Vec3d.add(pos, 0.5, 1.01, 0.5).addRandom(world.random, 0.7f);
            ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), new ItemStack(EWItems.GRASS_ELEMENTAL_STAFF));
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }
}
