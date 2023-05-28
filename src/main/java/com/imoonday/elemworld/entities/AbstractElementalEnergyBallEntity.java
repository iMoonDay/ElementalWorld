package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public abstract class AbstractElementalEnergyBallEntity extends ProjectileEntity {

    private ItemStack staffStack = ItemStack.EMPTY;
    protected boolean collided = false;

    protected AbstractElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> type, LivingEntity owner, ItemStack staffStack) {
        this(type, owner, staffStack, 1.0f);
    }

    public AbstractElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> type, LivingEntity owner, ItemStack staffStack, float speed) {
        this(type, owner.world);
        this.staffStack = staffStack;
        this.refreshPositionAndAngles(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYaw(), owner.getPitch());
        this.refreshPosition();
        this.setOwner(owner);
        this.staffStack = staffStack.copy();
        this.setNoGravity(true);
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0f, speed, 1.0f);
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
        if (!this.world.isClient && (owner != null && owner.isRemoved() || !this.world.isChunkLoaded(this.getBlockPos()) || this.age > 30 * 20 || this.collided)) {
            this.discard();
            return;
        }
        super.tick();
        if ((hitResult = ProjectileUtil.getCollision(this, entity1 -> canHit(entity1) && entity1 != owner)).getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
            this.collided = true;
        }
        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        if (this.age % 20 == 0) {
            this.setVelocity(vec3d.multiply(0.95));
        }
        this.setPosition(d, e, f);
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
     * @param range                Max distance from entity to this
     * @param damageFunc           Damage taken by each entity
     * @param predicate            Effective entity conditions
     * @param livingEntityConsumer Operations on each living entity
     * @param elseToDo             Run when no entities are found
     */
    protected void forEachLivingEntity(double range, Function<LivingEntity, Float> damageFunc, Predicate<LivingEntity> predicate, Consumer<LivingEntity> livingEntityConsumer, Runnable elseToDo) {
        if (!world.isClient) {
            Entity owner = getOwner();
            List<Entity> entities = world.getOtherEntities(owner, this.getBoundingBox().expand(range), entity -> entity instanceof LivingEntity living && predicate.test(living));
            if (entities.isEmpty()) {
                elseToDo.run();
            } else {
                for (Entity entity : entities) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    float amount = damageFunc.apply(livingEntity) * this.getStaffStack().getDamageMultiplier(livingEntity);
                    if (amount > 0) {
                        livingEntity.damage(owner != null ? owner.getDamageSources().indirectMagic(this, owner) : this.getDamageSources().magic(), amount);
                    }
                    livingEntityConsumer.accept(livingEntity);
                }
            }
        }
        this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1.0, 0, 0);
        this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.VOICE);
    }

    private void spawnAreaEffectCloudEntity(Entity entity, float radius, int waitTime) {
        if (!this.world.isClient) {
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, entity.getX(), entity.getY(), entity.getZ());
            if (getOwner() instanceof LivingEntity living) {
                areaEffectCloudEntity.setOwner(living);
            }
            areaEffectCloudEntity.setRadius(radius);
            areaEffectCloudEntity.setRadiusOnUse(-0.5f);
            areaEffectCloudEntity.setWaitTime(waitTime);
            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
            areaEffectCloudEntity.setPotion(this.getElement().getElementPotion());
            this.world.spawnEntity(areaEffectCloudEntity);
        }
    }

    protected ItemStack getStaffStack() {
        return staffStack.copy();
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
            matrixStack.scale(2.0f, 2.0f, 2.0f);
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
