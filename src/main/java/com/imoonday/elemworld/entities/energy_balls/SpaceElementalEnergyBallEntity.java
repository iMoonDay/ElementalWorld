package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.SpatialCrackEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SpaceElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public SpaceElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public SpaceElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.SPACE_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.SPACE;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(5.0f, this::teleportRandomly, false);
        spawnSpatialCrack();
    }

    private void teleportRandomly(LivingEntity entity) {
        if (world.isClient) {
            return;
        }
        double d = entity.getX();
        double e = entity.getY();
        double f = entity.getZ();
        for (int i = 0; i < 16; ++i) {
            double g = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0 * 2;
            double h = MathHelper.clamp(entity.getY() + (double) (entity.getRandom().nextInt(16) - 8), world.getBottomY(), world.getBottomY() + ((ServerWorld) world).getLogicalHeight() - 1);
            double j = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0 * 2;
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

    private void spawnSpatialCrack() {
        if (!world.isClient) {
            SpatialCrackEntity crack = new SpatialCrackEntity(world, this.getPos().add(0, 1, 0), 15 * 20);
            world.spawnEntity(crack);
        }
    }

}
