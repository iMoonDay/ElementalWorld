package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.api.WeightRandom;
import com.imoonday.elemworld.init.EWEntities;
import com.imoonday.elemworld.init.EWItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class ElementalElfEntity extends PathAwareEntity {

    public ElementalElfEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    @Override
    public boolean dropElementFragmentRandomly() {
        return false;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new FlyGoal(this, 1.5f));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0f));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        WeightRandom.getRandom(Element.getRegistrySet(false), element -> element.getWeight(this)).ifPresent(element -> this.dropItem(element.getFragmentItem()));
        if (this.random.nextFloat() < 0.01f) {
            WeightRandom.getRandom(EWItems.getAllStaffs()).ifPresent(this::dropItem);
        }
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02f, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8f));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02f, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
            } else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.91f));
            }
        }
        this.updateLimbs(false);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        return false;
    }

    public static class Renderer extends MobEntityRenderer<ElementalElfEntity, Model> {

        public Renderer(EntityRendererFactory.Context context) {
            super(context, new Model(context.getPart(EWEntities.MODEL_ELEMENTAL_ELF_LAYER)), 0.25f);
        }

        @Override
        public Identifier getTexture(ElementalElfEntity entity) {
            return id("textures/entity/elemental_elf.png");
        }
    }

    public static class Model extends EntityModel<ElementalElfEntity> {

        private final ModelPart main;

        public Model(ModelPart root) {
            this.main = root.getChild("main");
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            main.addChild("main", ModelPartBuilder.create().uv(0, 8).cuboid(-4.0F, -2.5F, -8.5F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)).uv(12, 8).cuboid(-3.0F, -1.5F, -7.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F)).uv(12, 0).cuboid(-2.0F, -0.5F, -6.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(6, 8).cuboid(3.0F, -2.5F, -8.5F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)).uv(10, 13).cuboid(2.0F, -1.5F, -7.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F)).uv(0, 14).cuboid(1.0F, -0.5F, -6.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 0).cuboid(-2.0F, -1.5F, -4.5F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5F, -2.5F, 0.0F, 3.1416F, 0.0F));
            return TexturedModelData.of(modelData, 32, 32);
        }

        @Override
        public void setAngles(ElementalElfEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }
}
