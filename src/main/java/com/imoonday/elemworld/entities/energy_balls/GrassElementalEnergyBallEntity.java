package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrassElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public GrassElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrassElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.GRASS_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.GRASS;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(0, this::growUp, true);
        fertilizeOrGrass();
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.HEART;
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.ENTITY_VILLAGER_CELEBRATE;
    }

    private void fertilizeOrGrass() {
        if (world.isClient) {
            return;
        }
        List<BlockPos> list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i <= 10; i++) {
            for (int j = 5; j >= -5; j--) {
                for (int k = 0; k <= 10; k++) {
                    for (int mul : new int[]{1, -1}) {
                        BlockPos pos = this.getBlockPos().add(i * mul, j, k * mul);
                        if (world.getBlockState(pos).isAir()) {
                            continue;
                        }
                        list.add(pos);
                    }
                }
            }
        }
        Collections.shuffle(list);
        for (BlockPos pos : list) {
            if (BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), world, pos)) {
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
                count++;
            } else if (world.getBlockState(pos).isOf(Blocks.DIRT) && world.getBlockState(pos.up()).isAir()) {
                world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
                count++;
            }
            if (count > 10) {
                break;
            }
        }
    }

    private void growUp(LivingEntity living) {
        if (living.isBaby()) {
            if (living instanceof ZombieEntity entity) {
                entity.setBaby(false);
            } else if (living instanceof ZoglinEntity entity) {
                entity.setBaby(false);
            } else if (living instanceof PiglinEntity entity) {
                entity.setBaby(false);
            } else if (living instanceof PassiveEntity entity) {
                entity.setBaby(false);
            }
        } else {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 0));
        }
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HEART, living.getX(), living.getY() + 1, living.getZ(), 15, 0, 1, 0, 1);
        }
    }
}
