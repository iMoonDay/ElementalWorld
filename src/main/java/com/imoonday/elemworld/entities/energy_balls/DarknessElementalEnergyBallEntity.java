package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DarknessElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    private static final String ATTRACT_TICK_KEY = "AttractTick";
    private static final TrackedData<Integer> ATTRACT_TICK = DataTracker.registerData(DarknessElementalEnergyBallEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public DarknessElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public DarknessElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.DARKNESS_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.DARKNESS;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTRACT_TICK, 0);
    }

    public int getAttractTick() {
        return this.dataTracker.get(ATTRACT_TICK);
    }

    public void setAttractTick(int ticks) {
        this.dataTracker.set(ATTRACT_TICK, Math.max(ticks, 0));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(2.0f, this::addStatusEffects, false);
        setVelocity();
    }

    private void setVelocity() {
        if (!world.isClient) {
            Vec3d vec3d = this.getVelocity().multiply(0.1);
            this.setVelocity(vec3d);
            this.setAttractTick(10 * 20);
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
        if (this.getAttractTick() > 0 && !state.isAir()) {
            this.setVelocity(Vec3d.ZERO);
        }
    }

    @Override
    protected boolean discardOnCollision() {
        return false;
    }

    private void addStatusEffects(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, getDuration()));
        entity.addStatusEffect(new StatusEffectInstance(EWEffects.DIZZY, getDuration(), this.random.nextInt(5)));
        if (this.random.nextFloat() < 0.2f) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, getDuration(), this.random.nextInt(3)));
        }
    }

    private int getDuration() {
        return 20 * this.random.nextBetween(5, 10);
    }

    @Override
    public void tick() {
        super.tick();
        int tick = this.getAttractTick();
        if (tick > 0) {
            List<Entity> entities = world.getOtherEntities(this.getOwner(), this.getBoundingBox().expand((double) tick / 20), entity -> entity instanceof LivingEntity living && living.isAlive());
            for (Entity entity : entities) {
                LivingEntity living = (LivingEntity) entity;
                Vec3d vec3d = this.getPos().subtract(living.getPos()).normalize().multiply(0.05);
                living.setVelocity(vec3d);
                if (tick % 20 == 0) {
                    living.damage(this.getDamageSources().magic(), 2.0f);
                }
            }
            this.setAttractTick(--tick);
            if (tick <= 0) {
                this.world.createExplosion(getOwner(), this.getX(), this.getY(), this.getZ(), 3.0f, true, World.ExplosionSourceType.MOB);
                this.discard();
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(ATTRACT_TICK_KEY, NbtElement.INT_TYPE)) {
            this.setAttractTick(nbt.getInt(ATTRACT_TICK_KEY));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(ATTRACT_TICK_KEY, this.getAttractTick());
    }
}
