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
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class GoblinTraderEntity extends MerchantEntity implements BaseElement {

    // 生物的交易信息
    private static final int BUY_USES = 7;
    private static final int MAX_TRADES_PER_LEVEL = 5;
    private static final float PRICE_MULTIPLIER = 0.05f;
    private static final Item CURRENCY = EWItems.GOLD_COIN;

    public GoblinTraderEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    public static EntityType<GoblinTraderEntity> register() {
        return EWEntities.register("goblin_trader",
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GoblinTraderEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build(),
                "Goblin Trader",
                "哥布林商人",
                createMobAttributes(),
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                GoblinTraderEntity::canMobSpawn,
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE,
                10,
                1,
                1,
                Color.YELLOW.getRGB(),
                Color.GREEN.getRGB());
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
        if (offer.shouldRewardPlayerExperience()) {
            int i = 3 + this.random.nextInt(4);
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    // 设置生物交易信息，在 Vanilla 环境下应该在自定义 Mod 中手动创建定制化的交易
    @Override
    protected void fillRecipes() {
        // 添加交易信息
        if (this.offers == null) {
            this.offers = new TradeOfferList();
        }

        // 向生物添加交易信息
        LinkedHashMap<Item, Integer> map = new LinkedHashMap<>();
        map.put(EWItems.ELEMENT_DETECTOR, 1);
        map.put(EWItems.BASE_ELEMENT_FRAGMENT, this.random.nextBetween(1, 3));
        map.put(EWItems.UMBRELLA, 1);
        EWItems.getAllStaffs().forEach(item -> map.put(item, 1));
        float f = 1.0f / 0.75f;
        do {
            List<Item> list = new ArrayList<>(map.keySet());
            Collections.shuffle(list);
            Item item = list.get(0);
            this.add(item, map.get(item));
            map.remove(item);
            f *= 0.75f;
        } while (this.random.nextFloat() < f);
    }

    protected void add(Item sellItem, int count) {
        if (this.offers != null) {
            this.offers.add(new TradeOffer(new ItemStack(CURRENCY, getPrice()), new ItemStack(sellItem, count), BUY_USES, MAX_TRADES_PER_LEVEL, PRICE_MULTIPLIER));
        }
    }

    protected int getPrice() {
        double mean = 32;
        double stdDev = 10;
        double randomValue = this.random.nextGaussian() * stdDev + mean;
        return (int) randomValue;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setCanPickUpLoot(true);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    // 设置生物属性
    @Override
    protected void initGoals() {
        // 设置行为
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 0.5));
        this.goalSelector.add(2, new TemptGoal(this, 0.5, Ingredient.ofItems(EWItems.GOLD_COIN), false));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.25));
        this.goalSelector.add(5, new LookAtCustomerGoal(this));
        this.goalSelector.add(6, new StopFollowingCustomerGoal(this));
        this.goalSelector.add(7, new GoToWalkTargetGoal(this, 0.6f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        // 设置目标
        this.targetSelector.add(0, new RevengeGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PillagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, EvokerEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, VindicatorEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, IllusionerEntity.class, true));
        this.targetSelector.add(6, new ActiveTargetGoal<>(this, VexEntity.class, true));
        this.targetSelector.add(7, new ActiveTargetGoal<>(this, MobEntity.class, 10, true, false, entity -> entity instanceof ZoglinEntity || entity instanceof HoglinEntity));
    }

    @Override
    public boolean isLeveledMerchant() {
        return false;
    }

    // 当选择到生物时的交互行为
    // 打开生物的选项窗口
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.isAlive() && !this.hasCustomer() && (!this.isBaby() || player.isSneaking())) {
            if (hand == Hand.MAIN_HAND) {
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (!this.world.isClient) {
                this.setCustomer(player);
                this.sendOffers(player, this.getDisplayName(), 1);
            }
            return ActionResult.success(this.world.isClient);
        } else {
            return super.interactMob(player, hand);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_WANDERING_TRADER_TRADE;
        }
        return SoundEvents.ENTITY_WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEvent getDrinkSound(ItemStack stack) {
        if (stack.isOf(Items.MILK_BUCKET)) {
            return SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK;
        }
        return SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION;
    }

    @Override
    protected SoundEvent getTradingSound(boolean sold) {
        return sold ? SoundEvents.ENTITY_WANDERING_TRADER_YES : SoundEvents.ENTITY_WANDERING_TRADER_NO;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_WANDERING_TRADER_YES;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        if (causedByPlayer) {
            this.dropStack(new ItemStack(EWItems.GOLD_COIN, this.random.nextBetween(5, 10)));
        }
    }

    @Override
    public Element getBaseElement() {
        return EWElements.GOLD;
    }

    public static class Renderer extends BipedEntityRenderer<GoblinTraderEntity, GoblinTraderEntity.Model> {

        private static final Identifier TEXTURE = id("textures/entity/goblin_trader.png");

        public Renderer(EntityRendererFactory.Context ctx) {
            super(ctx, new GoblinTraderEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.6f);
            this.addFeature(new ArmorFeatureRenderer<>(this, new GoblinTraderEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE_INNER_ARMOR)), new GoblinTraderEntity.Model(ctx.getPart(EntityModelLayers.ZOMBIE_OUTER_ARMOR)), ctx.getModelManager()));
        }

        @Override
        public Identifier getTexture(GoblinTraderEntity entity) {
            return TEXTURE;
        }
    }

    public static class Model extends BipedEntityModel<GoblinTraderEntity> {
        public Model(ModelPart root) {
            super(root);
        }
    }
}
