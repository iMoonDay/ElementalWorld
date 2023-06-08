package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

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
        forEachLivingEntity(3.0f, this::setBacktracking, true);
        addTime(random.nextBetween(50, 100));
    }

    private void addTime(int time) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + time);
            if (this.getOwner() instanceof PlayerEntity player) {
                player.sendMessage(Text.literal("时间 + " + time + " s"), true);
            }
        }
    }

    private void setBacktracking(LivingEntity entity) {
        entity.setBacktracking(15 * 20);
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
