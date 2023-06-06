package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.energy_balls.DarknessElementalEnergyBallEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public abstract class AbstractElementalEnergyBallEntity extends ProjectileEntity {

    private ItemStack staffStack = ItemStack.EMPTY;
    protected boolean collided = false;

    protected AbstractElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> type, LivingEntity owner, ItemStack staffStack) {
        this(type, owner, staffStack, 1.5f);
    }

    public AbstractElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> type, LivingEntity owner, ItemStack staffStack, float speed) {
        this(type, owner.world);
        this.refreshPositionAndAngles(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYaw(), owner.getPitch());
        this.refreshPosition();
        this.setOwner(owner);
        this.staffStack = staffStack.copy();
        this.setNoGravity(true);
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0f, speed, 1.0f);
        this.setGlowing(true);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    @Override
    public void tick() {
        HitResult hitResult;
        Entity owner = this.getOwner();
        if (!this.world.isClient && (owner != null && owner.isRemoved() || !this.world.isChunkLoaded(this.getBlockPos()) || this.age > discardAge() || this.collided && discardOnCollision())) {
            this.discard();
            return;
        }
        super.tick();
        if ((hitResult = ProjectileUtil.getCollision(this, entity1 -> canHit(entity1) && entity1 != owner)).getType() != HitResult.Type.MISS && !collided && shouldCollide(hitResult)) {
            this.onCollision(hitResult);
            this.collided = true;
        }
        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        Vec3d newPos = this.getPos().add(vec3d);
        this.setPosition(newPos);
        if (this.age % 20 == 0 || this.isTouchingWater()) {
            this.setVelocity(vec3d.multiply(0.95));
        }
        this.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(this.getElement().getColor().getRGB()).toVector3f(), 255), true, this.getX(), this.getBoundingBox().getCenter().y, this.getZ(), 0, 0, 0);
    }

    protected boolean shouldCollide(HitResult hitResult) {
        return true;
    }

    protected int discardAge() {
        return 30 * 20;
    }

    protected boolean discardOnCollision() {
        return true;
    }

    public abstract Element getElement();

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public float getTargetingMargin() {
        return 1.0f;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.staffStack != null) {
            nbt.put("StaffStack", this.staffStack.writeNbt(new NbtCompound()));
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("StaffStack", NbtElement.COMPOUND_TYPE)) {
            this.staffStack = ItemStack.fromNbt(nbt.getCompound("StaffStack"));
        }
    }

    /**
     * @param damage               Damage taken by each entity
     * @param livingEntityConsumer Operations on each living entity
     * @param particleAndSound     Add particles and play sound
     */
    protected void forEachLivingEntity(float damage, Consumer<LivingEntity> livingEntityConsumer, boolean particleAndSound) {
        forEachLivingEntity(10, damage, this::defaultPredicate, livingEntityConsumer, particleAndSound);
    }

    /**
     * @param range                Max distance from entity to this
     * @param damage               Damage taken by each entity
     * @param predicate            Effective entity conditions
     * @param livingEntityConsumer Operations on each living entity
     * @param particleAndSound     Add particles and play sound
     */
    protected void forEachLivingEntity(double range, float damage, Predicate<LivingEntity> predicate, Consumer<LivingEntity> livingEntityConsumer, boolean particleAndSound) {
        if (!world.isClient) {
            Entity owner = getOwner();
            world.getOtherEntities(owner, this.getBoundingBox().expand(range), entity -> entity instanceof LivingEntity living && predicate.test(living))
                    .stream().map(entity -> (LivingEntity) entity)
                    .sorted((o1, o2) -> (int) (o1.getPos().distanceTo(this.getPos()) - o2.getPos().distanceTo(this.getPos())))
                    .filter(livingEntity -> handleDamage(damage, owner, livingEntity))
                    .forEach(livingEntity -> {
                        livingEntity.addStatusEffect(new StatusEffectInstance(this.getElement().getEffect(), 15 * 20));
                        livingEntityConsumer.accept(livingEntity);
                    });
        }
        if (particleAndSound) {
            addParticleAndPlaySound();
        }
    }

    protected void addParticleAndPlaySound() {
        this.world.addParticle(getParticleType(), true, this.getX(), this.getY(), this.getZ(), 1.0, 0, 0);
        this.world.playSound(null, this.getBlockPos(), getSoundEvent(), SoundCategory.VOICE);
    }

    protected SoundEvent getSoundEvent() {
        return SoundEvents.ENTITY_GENERIC_EXPLODE;
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.EXPLOSION_EMITTER;
    }

    protected boolean handleDamage(float damage, Entity owner, LivingEntity livingEntity) {
        if (damage > 0) {
            livingEntity.damage(owner != null ? owner.getDamageSources().indirectMagic(this, owner) : this.getDamageSources().magic(), damage);
        } else if (damage < 0) {
            livingEntity.heal(damage);
        }
        return livingEntity.isAlive();
    }

    protected void spawnAreaEffectCloudEntity(float radius, int waitTime, Potion potion, double x, double y, double z, int color) {
        if (!this.world.isClient) {
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, x, y, z);
            if (getOwner() instanceof LivingEntity living) {
                areaEffectCloudEntity.setOwner(living);
            }
            areaEffectCloudEntity.setRadius(radius);
            areaEffectCloudEntity.setRadiusOnUse(-0.5f);
            areaEffectCloudEntity.setWaitTime(waitTime);
            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
            areaEffectCloudEntity.setPotion(potion);
            areaEffectCloudEntity.setColor(color);
            this.world.spawnEntity(areaEffectCloudEntity);
        }
    }

    protected ItemStack getStaffStack() {
        return staffStack.copy();
    }

    public final boolean defaultPredicate(LivingEntity entity) {
        return (!(entity instanceof Tameable tameable) || this.getOwner() != tameable.getOwner()) && entity.isAlive();
    }

    public static class EnergyBallEntityRenderer<T extends AbstractElementalEnergyBallEntity> extends EntityRenderer<T> {

        private final Identifier texture;

        public EnergyBallEntityRenderer(EntityRendererFactory.Context ctx, String fileName) {
            this(ctx, id("textures/entity/" + fileName + ".png"));
        }

        public EnergyBallEntityRenderer(EntityRendererFactory.Context ctx, Identifier texture) {
            super(ctx);
            this.texture = texture;
        }

        @Override
        protected int getBlockLight(T entity, BlockPos pos) {
            return 15;
        }

        @Override
        public void render(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
            matrixStack.push();
            matrixStack.scale(1.5f, 1.5f, 1.5f);
            if (entity instanceof DarknessElementalEnergyBallEntity darkness && darkness.collided) {
                int tick = darkness.getAttractTick();
                float scale = tick / 10.0f;
                matrixStack.scale(scale, scale, scale);
            }
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            MatrixStack.Entry entry = matrixStack.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            RenderLayer layer = RenderLayer.getEntityCutoutNoCull(texture);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);
            produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
            produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
            produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
            produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
            matrixStack.pop();
            super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
        }

        private void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV) {
            vertexConsumer.vertex(positionMatrix, x - 0.5f, (float) y - 0.25f, 0.0f).color(255, 255, 255, 255).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        }

        @Override
        public Identifier getTexture(T entity) {
            return texture;
        }
    }
}
