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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class SpatialCrackEntity extends Entity {

    private static final String MAX_AGE_KEY = "MaxAge";
    private static final String TELEPORT_POS_KEY = "TeleportPos";
    private int maxAge;
    private Vec3d teleportPos;

    public SpatialCrackEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public SpatialCrackEntity(World world, Vec3d pos, int maxAge, int minDistance, int maxDistance) {
        super(EWEntities.SPATIAL_CRACK, world);
        this.setPosition(pos);
        this.maxAge = maxAge;
        this.setYaw(world.random.nextBetween(-180, 180));
        this.teleportPos = this.calculatePos(minDistance, maxDistance);
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
        this.world.getEntitiesByClass(SpatialCrackEntity.class, this.getBoundingBox().expand(15), spatialCrackEntity -> spatialCrackEntity.age > this.age).forEach(Entity::discard);
        this.world.getOtherEntities(this, this.getBoundingBox(), entity -> entity.getBoundingBox().intersects(this.getBoundingBox()) && entity.isLiving() && !entity.isSpectator()).stream().map(entity -> (LivingEntity) entity).toList().forEach(this::teleport);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
    }

    public void teleport(LivingEntity entity) {
        entity.setImmuneFallDamage(true);
        if (this.teleportPos == null) {
            this.teleportPos = calculatePos(500, 2000);
        }
        double x = this.teleportPos.x;
        double z = this.teleportPos.z;
        entity.requestTeleport(x, this.getMinY(x, z), z);
    }

    public double getMinY(double x, double z) {
        double y = this.world.getTopY();
        if (!(this.world instanceof ServerWorld serverWorld)) {
            return y;
        }
        boolean forced = false;
        int chunkX = ChunkSectionPos.getSectionCoord(x);
        int chunkZ = ChunkSectionPos.getSectionCoord(z);
        if (!this.world.isPosLoaded((int) x, (int) z)) {
            forced = serverWorld.setChunkForced(chunkX, chunkZ, true);
        }
        int topY = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) x, (int) z);
        if (topY > 5) {
            y = topY;
        }
        if (forced) {
            serverWorld.setChunkForced(chunkX, chunkZ, false);
        }
        return y;
    }

    public Vec3d calculatePos(int minDistance, int maxDistance) {
        double distance = minDistance + ((maxDistance - minDistance) * this.random.nextDouble());
        float direction = this.random.nextFloat() * 360;
        double x = this.getX() + (distance * Math.sin(Math.toRadians(direction)));
        double z = this.getZ() + (distance * Math.cos(Math.toRadians(direction)));
        return new Vec3d(x, 255, z);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(MAX_AGE_KEY, NbtElement.INT_TYPE)) {
            this.maxAge = nbt.getInt(MAX_AGE_KEY);
        }
        if (nbt.contains(TELEPORT_POS_KEY, NbtElement.LIST_TYPE)) {
            this.teleportPos = getTeleportPos(nbt);
        }
    }

    public Vec3d getTeleportPos(NbtCompound nbt) {
        NbtList list = nbt.getList(TELEPORT_POS_KEY, NbtElement.DOUBLE_TYPE);
        if (list.size() != 3) {
            return calculatePos(500, 2000);
        }
        double x = list.getDouble(0);
        double y = list.getDouble(1);
        double z = list.getDouble(2);
        return new Vec3d(x, y, z);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt(MAX_AGE_KEY, this.maxAge);
        if (this.teleportPos == null) {
            this.teleportPos = calculatePos(500, 2000);
        }
        nbt.put(TELEPORT_POS_KEY, this.toNbtList(this.teleportPos.x, this.teleportPos.y, this.teleportPos.z));
    }

    public static class Renderer extends EntityRenderer<SpatialCrackEntity> {

        private static final Identifier TEXTURE = id("textures/entity/spatial_crack.png");
        private final ModelPart main;

        public Renderer(EntityRendererFactory.Context ctx) {
            super(ctx);
            this.main = ctx.getPart(EWEntities.SPATIAL_CRACK_MODEL_LAYER);
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -15.0F, 0.0F, 10.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            return TexturedModelData.of(modelData, 32, 32);
        }

        @Override
        public void render(SpatialCrackEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            matrices.push();
            matrices.scale(1.5f, 1.5f, 1.5f);
            matrices.translate(0, -0.5f, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
            RenderLayer layer = RenderLayer.getEntityCutout(TEXTURE);
            VertexConsumer consumer = vertexConsumers.getBuffer(layer);
            main.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }

        @Override
        public Identifier getTexture(SpatialCrackEntity entity) {
            return TEXTURE;
        }
    }
}
