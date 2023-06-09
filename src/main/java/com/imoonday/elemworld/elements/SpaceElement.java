package com.imoonday.elemworld.elements;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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

import java.awt.*;
import java.util.Map;

import static net.minecraft.registry.tag.DamageTypeTags.IS_DROWNING;
import static net.minecraft.registry.tag.DamageTypeTags.IS_EXPLOSION;

public class SpaceElement extends Element {
    public SpaceElement() {
        super(1, 3, 5, 0.5f, 0.5f, 0.2f, 0.0f);
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        if (source.isIndirect() || source.isIn(IS_DROWNING) || source.isIn(IS_EXPLOSION)) {
            if (source.isOf(DamageTypes.DROWN)) {
                BlockPos pos = BlockPos.ofFloored(entity.getEyePos());
                FluidState fluidState = entity.world.getBlockState(pos).getFluidState();
                if (!fluidState.isEmpty()) {
                    entity.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            } else {
                randomTeleport(entity, 16.0);
                entity.setVelocity(Vec3d.ZERO);
            }
            return 0.2f;
        } else {
            return entity.getRandom().nextFloat() < 0.25f ? 0.2f : 1.0f;
        }
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    @Override
    public boolean immuneDamageOnDeath(LivingEntity entity) {
        if (entity.getRandom().nextFloat() < 0.25f) {
            boolean teleport = randomTeleport(entity, 16);
            entity.world.sendEntityStatus(entity, EntityStatuses.USE_TOTEM_OF_UNDYING);
            return teleport;
        }
        return super.immuneDamageOnDeath(entity);
    }

    public static boolean randomTeleport(LivingEntity entity, double range) {
        World world = entity.world;
        if (!world.isClient) {
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();
            for (int i = 0; i < 16; ++i) {
                double g = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * range * 2;
                double h = MathHelper.clamp(entity.getY() + (entity.getRandom().nextInt((int) range) - range / 2), world.getBottomY(), world.getBottomY() + ((ServerWorld) world).getLogicalHeight() - 1);
                double j = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * range * 2;
                if (entity.hasVehicle()) {
                    entity.stopRiding();
                }
                Vec3d vec3d = entity.getPos();
                if (!entity.teleport(g, h, j, false)) continue;
                world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(entity));
                SoundEvent soundEvent = entity instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ENTITY_ENDERMAN_TELEPORT;
                world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0f, 1.0f);
                entity.playSound(soundEvent, 1.0f, 1.0f);
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(Map<EntityAttribute, EntityAttributeModifier> map, int slot) {
        map.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, 3, EntityAttributeModifier.Operation.ADDITION));
        map.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, 3, EntityAttributeModifier.Operation.ADDITION));
        return map;
    }
}
