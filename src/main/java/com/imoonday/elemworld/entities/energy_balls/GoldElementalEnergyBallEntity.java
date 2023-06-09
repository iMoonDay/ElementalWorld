package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoldElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public GoldElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public GoldElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.GOLD_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.GOLD;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(5.0f, this::spawnParticlesAndAddEffects, false);
        spawnGoldOres();
    }

    private void spawnGoldOres() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        int size = 5;
        List<BlockPos> list = new ArrayList<>();
        for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
                for (int k = -size; k <= size; k++) {
                    BlockPos pos = this.getBlockPos().add(i, j, k);
                    boolean replaceable = world.getBlockState(pos).isIn(BlockTags.STONE_ORE_REPLACEABLES);

                    if (replaceable) {
                        list.add(pos);
                    }
                }
            }
        }
        Collections.shuffle(list);
        int max = Math.min(this.random.nextBetween(4, 8), list.size());
        for (int i = 0; i < max; i++) {
            BlockPos pos = list.get(i);
            world.setBlockState(pos, Blocks.GOLD_ORE.getDefaultState(), Block.NOTIFY_LISTENERS);
            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS);
            Vec3d centerPos = pos.toCenterPos();
            serverWorld.spawnParticles(ParticleTypes.GLOW, centerPos.x, serverWorld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()), centerPos.z, 15, 0, 1, 0, 1);
        }
    }

    private void spawnParticlesAndAddEffects(LivingEntity entity) {
        spawnParticlesAtEntity(entity);
        Vec3d vec3d = entity.getPos().subtract(this.getPos()).normalize().multiply(2);
        entity.setVelocity(vec3d.x, 1, vec3d.z);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10 * 20));
    }

    public static void spawnParticlesAtEntity(LivingEntity entity) {
        Vec3d pos = entity.getBoundingBox().getCenter();
        World world = entity.world;
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        double step = 0.1;
        for (double i = step; i <= 3.5; i += step) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        spawnParticles(x + i * dx, y + i * dy, z + i * dz, world);
                    }
                }
            }
        }
    }

    public static void spawnParticles(double x, double y, double z, World world) {
        DustParticleEffect particle = new DustParticleEffect(Vec3d.unpackRgb(Color.YELLOW.getRGB()).toVector3f(), 255);
        if (world instanceof ClientWorld clientWorld) {
            clientWorld.addParticle(particle, x, y, z, 0, 0, 0);
        } else if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
