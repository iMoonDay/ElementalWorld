package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.init.EWEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.imoonday.elemworld.init.EWIdentifiers.id;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MeteoriteEntity extends Entity {

    private static final TrackedData<Float> RADIUS = DataTracker.registerData(MeteoriteEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public MeteoriteEntity(EntityType<? extends MeteoriteEntity> type, World world) {
        super(type, world);
    }

    public MeteoriteEntity(World world, Vec3d pos, float radius) {
        super(EWEntities.METEORITE, world);
        this.setPosition(pos);
        this.setRadius(radius);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d velocity = this.getVelocity();
        Box box = this.getBoundingBox();
        if (!world.isClient) {
            int collideCount = (int) BlockPos.stream(box)
                    .filter(pos -> !world.getBlockState(pos).isAir() && world.getBlockState(pos).getFluidState().isEmpty())
                    .peek(pos -> world.breakBlock(pos, this.getRadius() <= 50, this))
                    .count();
            if (this.shouldExplode(collideCount)) {
                this.world.createExplosion(this, this.getX(), box.minY, this.getZ(), 2 + this.getRadius() * 2, true, World.ExplosionSourceType.TNT);
                this.discard();
            } else {
                HitResult hitResult;
                if ((hitResult = ProjectileUtil.getCollision(this, entity -> canHit())).getType() == HitResult.Type.ENTITY) {
                    EntityHitResult result = (EntityHitResult) hitResult;
                    Entity entity = result.getEntity();
                    if (entity instanceof LivingEntity living) {
                        living.damage(this.getDamageSources().inWall(), 2.0f);
                    }
                }
            }
        }
        this.setPosition(this.getPos().add(velocity));
        this.setVelocity(0, velocity.y - 0.02, 0);
        this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), box.maxY, this.getZ(), -velocity.x, -velocity.y, -velocity.z);
        if (this.age % 5 == 0) {
            this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 10.0f, 1.0f);
        }
    }

    protected boolean shouldExplode(int collideCount) {
        return collideCount >= getExplosionCount();
    }

    protected int getExplosionCount() {
        return (int) (this.getBoundingBox().getXLength() * this.getBoundingBox().getZLength());
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(RADIUS, 0.5f);
    }

    public float getRadius() {
        return this.dataTracker.get(RADIUS);
    }

    public void setRadius(float radius) {
        this.dataTracker.set(RADIUS, Math.max(radius, 0.5f));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float scale = (this.getRadius() + 0.5f) / 0.5f;
        return this.getType().getDimensions().scaled(scale);
    }

    @Override
    protected Box calculateBoundingBox() {
        return super.calculateBoundingBox().expand(this.getRadius());
    }

    private static void spawnMeteorite(CommandContext<ServerCommandSource> context) {
        Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");
        float radius = FloatArgumentType.getFloat(context, "radius");
        ServerWorld world = context.getSource().getWorld();
        MeteoriteEntity meteorite = new MeteoriteEntity(world, pos, radius);
        world.spawnEntity(meteorite);
    }

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("meteorite")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(argument("pos", Vec3ArgumentType.vec3())
                        .then(argument("radius", FloatArgumentType.floatArg(0.5f))
                                .executes(context -> {
                                    spawnMeteorite(context);
                                    return 0;
                                }))));
    }

    public static class Renderer extends EntityRenderer<MeteoriteEntity> {

        private static final Identifier TEXTURE = id("textures/entity/meteorite.png");
        private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);
        private final ModelPart main;

        public Renderer(EntityRendererFactory.Context ctx) {
            super(ctx);
            this.main = ctx.getPart(EWEntities.MODEL_METEORITE_LAYER);
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("main", ModelPartBuilder.create().uv(86, 32).cuboid(-4.0F, -16.0F, -4.0F, 8.0F, 16.0F, 8.0F, new Dilation(0.0F)).uv(50, 47).cuboid(-5.0F, -15.0F, -5.0F, 10.0F, 14.0F, 10.0F, new Dilation(0.0F)).uv(0, 40).cuboid(-6.0F, -14.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)).uv(0, 0).cuboid(-8.0F, -12.0F, -4.0F, 16.0F, 8.0F, 8.0F, new Dilation(0.0F)).uv(0, 16).cuboid(-7.0F, -13.0F, -5.0F, 14.0F, 10.0F, 10.0F, new Dilation(0.0F)).uv(0, 64).cuboid(-5.0F, -13.0F, -7.0F, 10.0F, 10.0F, 14.0F, new Dilation(0.0F)).uv(48, 74).cuboid(-4.0F, -12.0F, -8.0F, 8.0F, 8.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            return TexturedModelData.of(modelData, 128, 128);
        }

        @Override
        public void render(MeteoriteEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            matrices.push();
            float scale = (entity.getRadius() + 0.5f) / 0.5f;
            matrices.scale(scale, scale, scale);
            matrices.translate(0, -1.0f, 0);
            VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
            main.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }

        @Override
        public Identifier getTexture(MeteoriteEntity entity) {
            return TEXTURE;
        }
    }
}
