package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;

public class TimeElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public TimeElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public TimeElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.TIME_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.TIME;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(3.0f, this::backToBefore, true);
        addTime(random.nextBetween(500, 1500));
    }

    private void addTime(int time) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + time);
        }
    }

    private void backToBefore(LivingEntity entity) {
        Map<Long, Vec3d> posHistory = entity.getPosHistory();
        if (posHistory != null && !posHistory.isEmpty()) {
            long time = entity.world.getTime() - 15 * 20;
            Vec3d vec3d = posHistory.get(time);
            if (vec3d == null) {
                Optional<Map.Entry<Long, Vec3d>> optional = posHistory.entrySet().stream().min((o1, o2) -> Math.toIntExact((o1.getKey() - time) - (o2.getKey() - time)));
                if (optional.isPresent()) {
                    vec3d = optional.get().getValue();
                }
            }
            if (vec3d != null) {
                entity.teleport(vec3d.x, vec3d.y, vec3d.z, false);
            }
        }
    }

    @Override
    protected void onSkyHeight() {
        if (world.isClient) {
            return;
        }
        this.addTime(18000);
        this.discard();
    }
}
