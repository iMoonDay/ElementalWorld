package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class SpatialCrackEntity extends Entity {

    private int maxAge;

    public SpatialCrackEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public SpatialCrackEntity(World world, Vec3d pos, int maxAge) {
        super(EWEntities.SPATIAL_CRACK, world);
        this.setPosition(pos);
        this.maxAge = maxAge;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            return;
        }
        if (this.age >= this.maxAge) {
            this.discard();
        }
        if (!this.world.isChunkLoaded(this.getBlockPos())) {
            this.discard();
        }
        List<LivingEntity> entities = this.world.getOtherEntities(this, this.getBoundingBox(), entity -> entity.getBoundingBox().intersects(this.getBoundingBox()) && entity instanceof LivingEntity).stream().map(entity -> ((LivingEntity) entity)).toList();
        for (LivingEntity entity : entities) {
            teleportRandomly(entity, 500, 2000);
            this.maxAge -= 20;
        }
    }

    public static void teleportRandomly(LivingEntity entity, int minDistance, int maxDistance) {
        World world = entity.getEntityWorld();
        double distance = minDistance + ((maxDistance - minDistance) * world.random.nextDouble());
        float direction = world.random.nextFloat() * 360;
        double x = entity.getPos().getX() + (distance * Math.sin(Math.toRadians(direction)));
        double z = entity.getPos().getZ() + (distance * Math.cos(Math.toRadians(direction)));
        entity.setImmuneFallDamage(true);
        entity.requestTeleport(x, 255, z);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public static class Renderer extends EntityRenderer<SpatialCrackEntity> {

        private static final Identifier TEXTURE = id("textures/entity/spatial_crack.png");
        private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
        private final ModelPart main;

        public Renderer(EntityRendererFactory.Context ctx) {
            super(ctx);
            this.main = ctx.getPart(EWEntities.METEORITE_MODEL_LAYER);
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -15.0F, 0.0F, 10.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            return TexturedModelData.of(modelData, 32, 32);
        }

        @Override
        public void render(SpatialCrackEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            matrices.push();
            VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
            main.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }

        @Override
        public Identifier getTexture(SpatialCrackEntity entity) {
            return TEXTURE;
        }
    }
}
