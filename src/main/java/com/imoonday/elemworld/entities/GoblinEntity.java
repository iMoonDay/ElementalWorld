package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import com.imoonday.elemworld.interfaces.FixedElement;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class GoblinEntity extends PathAwareEntity implements FixedElement {

    public GoblinEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    public GoblinEntity(World world) {
        this(EWEntities.GOBLIN, world);
    }

    @Override
    public Element getElement() {
        return EWElements.GOLD;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createGoblinAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isFallFlying() && this.onGround && !this.isTouchingWater()) {
            Box box = this.getBoundingBox().expand(1.0F, 0.5F, 1.0F);
            PlayerEntity player = this.world.getClosestPlayer(this, 10.0D);

            if (player != null && player.isAlive()) {
                if (box.intersects(player.getBoundingBox())) {
                    this.startJumpAttack(player);
                }
            }
        }
    }

    private void startJumpAttack(PlayerEntity target) {
        this.setVelocity(this.getVelocity().add(0.0, 0.8D, 0.0));
        float attackDamage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        target.damage(this.getDamageSources().mobAttack(this), this.getJumpAttackDamage(attackDamage));
        this.playAmbientSound();
    }

    @Override
    public int getSafeFallDistance() {
        return 6;
    }

    @Override
    protected float getJumpVelocity() {
        return 0.9f;
    }

    protected float getJumpAttackDamage(float attackDamage) {
        return (attackDamage * 2.0F);
    }

    public static class Renderer extends BipedEntityRenderer<GoblinEntity, Model> {

        public Renderer(EntityRendererFactory.Context ctx) {
            super(ctx, new GoblinEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.6f);
        }

        @Override
        public Identifier getTexture(GoblinEntity entity) {
            return new Identifier("textures/entity/zombie/zombie.png");
        }
    }

    public static class Model extends BipedEntityModel<GoblinEntity> {
        public Model(ModelPart root) {
            super(root);
        }
    }
}
