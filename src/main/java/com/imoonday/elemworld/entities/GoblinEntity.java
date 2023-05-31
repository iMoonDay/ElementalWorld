package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import com.imoonday.elemworld.init.EWItems;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class GoblinEntity extends HostileEntity implements BaseElement {

    private int extraCoins = 0;

    public GoblinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.GOLD;
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(GoblinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.goalSelector.add(1, new JumpToTargetGoal(this, 0.5));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(random, difficulty);
        this.setCanPickUpLoot(true);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.equipAtChance(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
        this.equipAtChance(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
        this.equipAtChance(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        this.equipAtChance(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
    }

    private void equipAtChance(EquipmentSlot slot, ItemStack stack) {
        if (random.nextFloat() < 0.25f) {
            this.equipStack(slot, stack);
        }
    }

    public static DefaultAttributeContainer.Builder createGoblinAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        if (causedByPlayer) {
            this.dropStack(new ItemStack(EWItems.GOLD_COIN, this.random.nextBetween(1, 3) + this.extraCoins));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isClient) {
            ItemStack stack = this.getMainHandStack();
            if (stack.isOf(EWItems.GOLD_COIN)) {
                this.extraCoins += stack.getCount();
                this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl && target instanceof PlayerEntity player) {
            if (this.random.nextFloat() < 0.25f) {
                int slot = player.getInventory().getSlotWithStack(new ItemStack(EWItems.GOLD_COIN));
                if (slot != -1) {
                    ItemStack stack = player.getInventory().getStack(slot);
                    if (!stack.isEmpty()) {
                        int stackCount = stack.getCount();
                        int count = this.random.nextBetween(1, Math.min(3, stackCount));
                        player.dropStack(stack.copyWithCount(count));
                        stack.decrement(count);
                    }
                }
            }
        }
        return bl;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("ExtraCoins", NbtElement.INT_TYPE)) {
            this.extraCoins = nbt.getInt("ExtraCoins");
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("ExtraCoins", this.extraCoins);
    }

    public static EntityType<GoblinEntity> register() {
        return EWEntities.register("goblin",
                FabricEntityTypeBuilder.<GoblinEntity>create(SpawnGroup.MONSTER, GoblinEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build(),
                "Goblin",
                "哥布林",
                createGoblinAttributes(),
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                HostileEntity::canSpawnInDark,
                BiomeSelectors.spawnsOneOf(EntityType.ZOMBIE),
                SpawnGroup.MONSTER,
                100,
                3,
                6,
                Color.YELLOW.getRGB(),
                Color.ORANGE.getRGB());
    }

    public static class Renderer extends BipedEntityRenderer<GoblinEntity, Model> {

        private static final Identifier TEXTURE = id("textures/entity/goblin.png");

        public Renderer(EntityRendererFactory.Context ctx) {
            super(ctx, new GoblinEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.6f);
            this.addFeature(new ArmorFeatureRenderer<>(this, new GoblinEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE_INNER_ARMOR)), new GoblinEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE_OUTER_ARMOR)), ctx.getModelManager()));
        }

        @Override
        public Identifier getTexture(GoblinEntity entity) {
            return TEXTURE;
        }
    }

    public static class Model extends BipedEntityModel<GoblinEntity> {
        public Model(ModelPart root) {
            super(root);
        }
    }

    public static class JumpToTargetGoal extends Goal {
        private final double jumpHeight;
        private final PathAwareEntity mob;
        private boolean canJump = true;
        private int cooldown = 0;
        private boolean attacked = false;

        public JumpToTargetGoal(PathAwareEntity entity, double jumpHeight) {
            this.mob = entity;
            this.jumpHeight = jumpHeight;
        }

        @Override
        public boolean canStart() {
            if (mob.getTarget() == null) return false;
            double distance = mob.getTarget().getPos().distanceTo(mob.getPos());
            return distance >= 1 && distance <= 3;
        }

        @Override
        public void start() {
            if (mob.getTarget() == null) {
                return;
            }

            if (this.cooldown > 0) {
                return;
            }

            // Stop current movement and prepare to jump
            mob.getNavigation().stop();
            mob.setJumping(true);

            // Takeoff
            Vec3d targetPos = mob.getTarget().getPos();
            float deltaX = (float) (targetPos.getX() - mob.getX());
            float deltaZ = (float) (targetPos.getZ() - mob.getZ());
            float distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            mob.setVelocity(deltaX / distance, jumpHeight / 2, deltaZ / distance);
            canJump = false;
            this.cooldown = 20;
        }

        @Override
        public void stop() {
            mob.setJumping(false);
            this.cooldown = 0;
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !canJump && cooldown <= 0;
        }

        @Override
        public void tick() {
            // Check if the entity is in the air
            LivingEntity target = mob.getTarget();
            if (!mob.isOnGround()) {
                if (target != null) {
                    mob.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                }
                mob.setAttacking(true);
                attack(target);
            } else {
                mob.setAttacking(false);
                canJump = true;
                this.cooldown = Math.max(this.cooldown - 1, 0);
            }
            if (this.cooldown <= 0) {
                attacked = false;
            }
        }

        protected void attack(LivingEntity target) {
            if (this.attacked) {
                return;
            }
            double d = this.getSquaredMaxAttackDistance(target);
            if (this.mob.getSquaredDistanceToAttackPosOf(target) <= d) {
                this.mob.swingHand(Hand.MAIN_HAND);
                if (this.mob.tryAttack(target)) {
                    attacked = true;
                }
            }
        }

        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + entity.getWidth();
        }
    }
}
