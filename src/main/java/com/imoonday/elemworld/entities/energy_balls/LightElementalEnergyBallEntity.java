package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LightElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public LightElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public LightElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.LIGHT_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.LIGHT;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(5.0f, this::addStatusEffects, true);
        addLight();
    }

    private void addStatusEffects(LivingEntity living) {
        living.addStatusEffect(new StatusEffectInstance(EWEffects.LIGHT_FEARING, 15 * 20));
        living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 15 * 20));
        living.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10 * 20));
    }

    private void addLight() {
        if (world.isClient) {
            return;
        }
        int size = 16;
        List<BlockPos> list = new ArrayList<>();
        for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
                for (int k = -size; k <= size; k++) {
                    BlockPos pos = this.getBlockPos().add(i, j, k).up();
                    boolean placeable = world.getBlockState(pos).isReplaceable() && world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), Direction.UP) && world.canPlace(Blocks.TORCH.getDefaultState(), pos, ShapeContext.absent()) && world.getBlockState(pos).getFluidState().isEmpty();
                    if (placeable) {
                        list.add(pos);
                    }
                }
            }
        }
        list = list.stream().distinct().collect(Collectors.toList());
        Collections.shuffle(list);
        int max = Math.min(this.random.nextBetween(16, 32), list.size());
        for (int i = 0; i < max; i++) {
            BlockPos pos = list.get(i);
            FallingBlockEntity.spawnFromBlock(world, pos.up(3), Blocks.TORCH.getDefaultState());
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX(), pos.getY(), pos.getZ(), 15, 0, 1, 0, 1);
            }
        }
    }
}
