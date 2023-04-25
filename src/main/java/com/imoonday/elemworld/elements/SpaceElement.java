package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static net.minecraft.registry.tag.DamageTypeTags.IS_DROWNING;
import static net.minecraft.registry.tag.DamageTypeTags.IS_EXPLOSION;

public class SpaceElement extends Element {
    public SpaceElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public boolean ignoreDamage (DamageSource source, LivingEntity entity){
        if (source.isIndirect() || source.isIn(IS_DROWNING) || source.isIn(IS_EXPLOSION)) {
            if (source.isOf(DamageTypes.DROWN)) {
                BlockPos pos = BlockPos.ofFloored(entity.getEyePos());
                FluidState fluidState = entity.world.getBlockState(pos).getFluidState();
                if (!fluidState.isEmpty()) {
                    entity.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            } else {
                randomTeleport(entity);
                entity.setVelocity(Vec3d.ZERO);
            }
            return true;
        } else {
            return entity.getRandom().nextFloat() < 0.25f;
        }
    }

    @Override
    public boolean shouldImmuneOnDeath (LivingEntity entity){
        if (entity.getRandom().nextFloat() < 0.25f) {
            randomTeleport(entity);
            entity.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f);
            return true;
        }
        return super.shouldImmuneOnDeath(entity);
    }

    private static void randomTeleport (LivingEntity entity){
        World world = entity.world;
        if (!world.isClient) {
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();
            for (int i = 0; i < 16; ++i) {
                double g = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                double h = MathHelper.clamp(entity.getY() + (double) (entity.getRandom().nextInt(16) - 8), world.getBottomY(), world.getBottomY() + ((ServerWorld) world).getLogicalHeight() - 1);
                double j = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                if (entity.hasVehicle()) {
                    entity.stopRiding();
                }
                Vec3d vec3d = entity.getPos();
                if (!entity.teleport(g, h, j, false)) continue;
                world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(entity));
                SoundEvent soundEvent = entity instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ENTITY_ENDERMAN_TELEPORT;
                world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0f, 1.0f);
                entity.playSound(soundEvent, 1.0f, 1.0f);
                break;
            }
        }
    }
}
