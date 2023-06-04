package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.MeteoriteEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EarthElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public EarthElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public EarthElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.EARTH_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.EARTH;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(5.0f, this::spawnFallingBlocks, true);
        spawnMeteorite();
    }

    private void spawnMeteorite() {
        if (!world.isClient) {
            MeteoriteEntity meteorite = new MeteoriteEntity(world, this.getPos().add(0, 200, 0), 15.0f);
            world.spawnEntity(meteorite);
            meteorite.setVelocity(0, -2, 0);
        }
    }

    private void spawnFallingBlocks(LivingEntity entity) {
        BlockPos.stream(entity.getBoundingBox()).forEach(pos -> {
            FallingBlockEntity blockEntity = FallingBlockEntity.spawnFromBlock(world, pos.up(5), Blocks.DIRT.getDefaultState());
            blockEntity.setHurtEntities(entity.getMaxHealth() / 4, (int) (entity.getMaxHealth() / 2));
            blockEntity.setDestroyedOnLanding();
        });
    }
}
