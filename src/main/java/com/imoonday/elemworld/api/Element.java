package com.imoonday.elemworld.api;

import com.imoonday.elemworld.init.EWEffects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.ElementalWorld.id;
import static net.minecraft.entity.damage.DamageTypes.*;
import static net.minecraft.entity.effect.StatusEffects.*;
import static net.minecraft.registry.tag.DamageTypeTags.*;

@SuppressWarnings("unused")
public enum Element implements StringIdentifiable {

    INVALID(0, "invalid", 0), GOLD(1, "gold", 1, 1.25f, 1.25f, 1.0f, 0.75f) {
        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            entity.decelerate(0.9);
        }

        @Override
        public void afterInjury(LivingEntity entity, DamageSource source, float amount) {
            Random random = entity.getRandom();
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity living) {
                if (random.nextFloat() < 0.25f) {
                    Vec3d vec3d = living.getPos().subtract(entity.getPos()).normalize();
                    living.setVelocity(vec3d.x, 0.5, vec3d.z);
                    if (living instanceof ServerPlayerEntity player) {
                        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
                    }
                }
            }
        }

        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            Random random = attacker.getRandom();
            if (random.nextFloat() < 0.25f) {
                target.addStatusEffect(new StatusEffectInstance(EWEffects.DIZZY, 10 * 20, 0));
            }
        }
    }, WOOD(2, "wood", 1, 0.75f, 0.75f, 1.0f, 1.25f) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            if (state.isIn(BlockTags.LOGS)) {
                return 1.5f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }
    }, WATER(3, "water", 1, 1.0f, 0.5f, 1.0f, 1.0f) {
        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            if (target.isIn(FIRE)) {
                target.removeEffectOf(FIRE);
                if (target.isOnFire()) {
                    target.setOnFire(false);
                }
                World world = target.world;
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + target.getHeight(), target.getZ(), 8, 0.5, 0.25, 0.5, 0.0);
                    serverWorld.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
                }
            }
        }

        @Override
        public float getExtraDamage(LivingEntity target, float amount) {
            if (target.isIn(FIRE)) {
                return 2.0f;
            }
            return super.getExtraDamage(target, amount);
        }

        @Override
        public int getEffectTime(LivingEntity target) {
            if (target.hasElement(EARTH)) {
                return 3;
            }
            return super.getEffectTime(target);
        }

        @Override
        public boolean shouldAddEffect(LivingEntity entity) {
            return entity.isWet();
        }
    }, FIRE(4, "fire", 1, 1.0f, 1.5f, 1.0f, 1.0f) {
        @Override
        public float getExtraDamage(LivingEntity target, float amount) {
            if (target.isIn(WATER)) {
                return Math.max(-amount * 0.25f, -20);
            }
            return super.getExtraDamage(target, amount);
        }

        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            if (target.isIn(WATER)) {
                StatusEffectInstance effect = target.getStatusEffect(WATER.getEffect());
                if (effect != null) {
                    int duration = effect.getDuration() - 3 * 20;
                    if (duration <= 0) {
                        target.removeEffectOf(WATER);
                    } else {
                        target.setStatusEffect(new StatusEffectInstance(WATER.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()), attacker);
                    }
                }
            }
        }

        @Override
        public int getEffectTime(LivingEntity target) {
            if (target.hasElement(EARTH)) {
                return 3;
            }
            return super.getEffectTime(target);
        }

        @Override
        public boolean shouldAddEffect(LivingEntity entity) {
            return entity.isOnFire() || entity.isInLava();
        }

        @Override
        public void tick(LivingEntity entity) {
            if (!entity.hasStatusEffect(EWEffects.FREEZE)) {
                return;
            }
            entity.removeStatusEffect(EWEffects.FREEZE);
            entity.world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.VOICE, 1, 1);
        }

        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (!entity.hasStatusEffect(EWEffects.FREEZE)) {
                return;
            }
            entity.removeStatusEffect(EWEffects.FREEZE);
            entity.world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.VOICE, 1, 1);
            if (!entity.world.isClient) {
                ArrayList<ServerPlayerEntity> entities = new ArrayList<>(PlayerLookup.tracking(entity));
                if (entity instanceof ServerPlayerEntity player) {
                    entities.add(player);
                }
                for (ServerPlayerEntity player : entities) {
                    player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(player.getId(), EWEffects.FREEZE));
                }
            }
        }
    }, EARTH(5, "earth", 1, 1.0f, 1.0f, 1.0f, 1.5f), WIND(6, "wind", 2) {
        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            return source.isIn(IS_FALL);
        }

        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            List<LivingEntity> entities = target.world.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(3), Entity::isLiving);
            for (LivingEntity entity : entities) {
                if (target.hasEffectOf(FIRE)) {
                    FIRE.addEffect(entity, attacker);
                }
                Vec3d subtract = entity.getPos().subtract(attacker.getPos()).normalize();
                Vec3d vec3d = new Vec3d(subtract.x, 1, subtract.z);
                entity.setVelocity(vec3d);
            }
        }

        @Override
        public HashMap<StatusEffect, Integer> getPersistentEffects() {
            HashMap<StatusEffect, Integer> effects = new HashMap<>();
            effects.put(JUMP_BOOST, 0);
            effects.put(SPEED, 0);
            return effects;
        }
    }, THUNDER(7, "thunder", 2, 1.0f, 1.75f, 1.0f, 1.0f) {
        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            return source.isIn(IS_LIGHTNING);
        }

        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            if (target.isIn(WATER)) {
                HashSet<Entity> entities = new HashSet<>(getEntitiesNearby(target));
                HashSet<Entity> otherEntities = entities.stream().flatMap(entity1 -> new HashSet<>(getEntitiesNearby(entity1)).stream()).collect(Collectors.toCollection(HashSet::new));
                entities.addAll(otherEntities);
                for (Entity entity : entities) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity.damage(attacker.getDamageSources().magic(), 2)) {
                        livingEntity.removeEffectOf(WATER);
                        livingEntity.world.playSound(null, livingEntity.getBlockPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.VOICE);
                    }
                }
            } else if (target.isIn(FIRE)) {
                target.world.createExplosion(target, attacker.getDamageSources().explosion(attacker, attacker), new ExplosionBehavior(), target.getX(), target.getY(), target.getZ(), 2, false, World.ExplosionSourceType.NONE);
                target.removeEffectOf(FIRE);
                target.setOnFire(false);
            }
        }

        private static List<Entity> getEntitiesNearby(Entity entity) {
            return entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(5), entity1 -> entity1 instanceof LivingEntity livingEntity && !livingEntity.hasOneOf(EARTH, THUNDER) && livingEntity.hasElement(WATER));
        }

        @Override
        public boolean shouldAddEffectAfterInjury(LivingEntity entity, DamageSource source, float amount) {
            return source.isIn(IS_LIGHTNING);
        }
    }, ROCK(8, "rock", 2, 1.0f, 1.0f, 1.5f, 2.0f) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
                return 2.0f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            return source.isOf(IN_WALL);
        }

//        @Override
//        public HashMap<StatusEffect, Integer> getPersistentEffects() {
//            HashMap<StatusEffect, Integer> effects = new HashMap<>();
//            effects.put(HEALTH_BOOST, 1);
//            return effects;
//        }

        @Override
        public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(int slot) {
            Map<EntityAttribute, EntityAttributeModifier> map = new HashMap<>();
            EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, 4.0, EntityAttributeModifier.Operation.ADDITION);
            map.put(EntityAttributes.GENERIC_MAX_HEALTH, entityAttributeModifier);
            return map;
        }
    }, GRASS(9, "grass", 2) {
        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            BlockState state = entity.getSteppingBlockState();
            if (source.isIn(IS_FALL) && (state.isOf(Blocks.GRASS_BLOCK) || state.isIn(BlockTags.LEAVES))) {
                return true;
            }
            return super.ignoreDamage(source, entity);
        }

        @Override
        public void tick(LivingEntity entity) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.setHealTick(entity.getHealTick() + 1);
                int healTick = entity.getHealTick();
                boolean onGrass = entity.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK);
                if (onGrass && healTick >= 5 * 20 || healTick >= 10 * 20) {
                    entity.heal(1);
                    entity.setHealTick(0);
                    if (onGrass) {
                        entity.world.setBlockState(entity.getSteppingPos(), Blocks.DIRT.getDefaultState());
                    }
                }
            }
        }
    }, ICE(10, "ice", 2, 1.0f, 1.0f, 1.0f, 0.75f) {
        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (entity.isIn(WATER)) {
                if (entity.hasStatusEffect(EWEffects.FREEZE)) {
                    return;
                }
                entity.addStatusEffect(new StatusEffectInstance(EWEffects.FREEZE, 1, 0, false, false, false));
            } else {
                entity.decelerate(0.5);
            }
        }

        @Override
        public int getEffectTime(LivingEntity target) {
            if (target.hasStatusEffect(EWEffects.FREEZING_RESISTANCE)) {
                return 0;
            }
            return super.getEffectTime(target);
        }

        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            if (target.isIn(FIRE)) {
                target.removeEffectOf(FIRE);
                if (target.isOnFire()) {
                    target.setOnFire(false);
                }
                World world = target.world;
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + target.getHeight(), target.getZ(), 8, 0.5, 0.25, 0.5, 0.0);
                    serverWorld.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
                }
            }
        }

        @Override
        public void writeDamageMultiplier(HashMap<Predicate<LivingEntity>, Float> map) {
            map.put(living -> living.hasStatusEffect(EWEffects.FREEZE), 1.5f);
        }

        @Override
        public float getExtraDamage(LivingEntity target, float amount) {
            if (target.isIn(FIRE)) {
                return 3.0f;
            }
            return super.getExtraDamage(target, amount);
        }
    }, LIGHT(11, "light", 3) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            Float x = getMultiplier(world);
            if (x != null) return x;
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            Float x = getMultiplier(world);
            if (x != null) return x;
            return super.getDamageMultiplier(world, entity, target);
        }

        @Override
        public float getProtectionMultiplier(World world, LivingEntity entity) {
            Float x = getMultiplier(world);
            if (x != null) return x;
            return super.getProtectionMultiplier(world, entity);
        }

        @Nullable
        private Float getMultiplier(World world) {
            long time = world.getTimeOfDay();
            if (time >= 1000 && time < 13000) {
                return 1.5f;
            }
            return null;
        }

        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            return source.isIn(IS_FIRE) || source.isIn(IS_EXPLOSION);
        }

        @Override
        public void postHit(LivingEntity target, PlayerEntity attacker) {
            if (target.isIn(DARKNESS)) {
                float v = Random.create().nextFloat();
                double chance = 0.2 * (1 - (target.getHealth() / target.getMaxHealth()));
                if (v < chance) {
                    if (!target.isInvulnerable()) {
                        target.world.playSound(null, target.getBlockPos(), SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.VOICE);
                        target.kill();
                    }
                }
            }
        }

        @Override
        public HashMap<StatusEffect, Integer> getPersistentEffects() {
            HashMap<StatusEffect, Integer> effects = new HashMap<>();
            effects.put(SPEED, 0);
            effects.put(NIGHT_VISION, 0);
            return effects;
        }
    }, DARKNESS(12, "darkness", 3) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            Float x = getMultiplier(world, entity);
            if (x != null) return x;
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            Float x = getMultiplier(world, entity);
            if (x != null) return x;
            return super.getDamageMultiplier(world, entity, target);
        }

        @Override
        public float getProtectionMultiplier(World world, LivingEntity entity) {
            Float x = getMultiplier(world, entity);
            if (x != null) return x;
            return super.getProtectionMultiplier(world, entity);
        }

        @Nullable
        private Float getMultiplier(World world, LivingEntity entity) {
            long time = world.getTimeOfDay();
            if (time < 1000 || time >= 13000) {
                if (world.getLightLevel(entity.getBlockPos()) == 0) {
                    return 3.0f;
                }
                return 2.0f;
            }
            return null;
        }

        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            if (source.isIndirect()) {
                return false;
            }
            if (source.getAttacker() instanceof LivingEntity attacker) {
                long time = entity.world.getTimeOfDay();
                if (attacker.getMainHandStack().hasElement(LIGHT) || attacker.isIn(LIGHT) || time >= 1000 && time < 13000) {
                    return false;
                }
            }
            return source.isOf(MOB_ATTACK) || source.isOf(MOB_ATTACK_NO_AGGRO) || source.isOf(PLAYER_ATTACK);
        }

        @Override
        public HashMap<StatusEffect, Integer> getPersistentEffects() {
            HashMap<StatusEffect, Integer> effects = new HashMap<>();
            effects.put(SPEED, 0);
            return effects;
        }
    }, TIME(13, "time", 3, 1.75f, 1.75f, 1.75f, 1.0f) {
        @Override
        public boolean shouldImmuneOnDeath(LivingEntity entity) {
            if (entity.getImmuneCooldown() <= 0) {
                entity.setImmuneCooldown(5 * 60 * 20);
                entity.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f);
                return true;
            }
            return super.shouldImmuneOnDeath(entity);
        }

        @Override
        public void afterInjury(LivingEntity entity, DamageSource source, float amount) {
            if (entity.getImmuneCooldown() != 0) {
                return;
            }
            Random random = entity.getRandom();
            if (random.nextFloat() < 0.0625f) {
                entity.setHealth(entity.getMaxHealth());
            } else if (entity.getHealth() / entity.getMaxHealth() <= 0.2f) {
                if (random.nextFloat() < 0.25f) {
                    entity.setHealth(entity.getMaxHealth() / 2f);
                }
            }
        }
    }, SPACE(14, "space", 3, 1.5f, 1.5f, 1.5f, 1.0f) {
        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            if (source.isIndirect() || source.isIn(IS_DROWNING) || source.isIn(IS_EXPLOSION)) {
                if (source.isOf(DamageTypes.DROWN)) {
                    BlockPos pos = BlockPos.ofFloored(entity.getEyePos());
                    FluidState fluidState = entity.world.getBlockState(pos).getFluidState();
                    if (!fluidState.isEmpty()) {
                        entity.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                } else {
                    randomTeleport(entity);
                    entity.setVelocity(Vec3d.ZERO);
                }
                return true;
            } else {
                return entity.getRandom().nextFloat() < 0.25f;
            }
        }

        @Override
        public boolean shouldImmuneOnDeath(LivingEntity entity) {
            if (entity.getRandom().nextFloat() < 0.25f) {
                randomTeleport(entity);
                entity.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f);
                return true;
            }
            return super.shouldImmuneOnDeath(entity);
        }

        private static void randomTeleport(LivingEntity entity) {
            World world = entity.world;
            if (!world.isClient) {
                double d = entity.getX();
                double e = entity.getY();
                double f = entity.getZ();
                for (int i = 0; i < 16; ++i) {
                    double g = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                    double h = MathHelper.clamp(entity.getY() + (double) (entity.getRandom().nextInt(16) - 8), world.getBottomY(), world.getBottomY() + ((ServerWorld) world).getLogicalHeight() - 1);
                    double j = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                    if (entity.hasVehicle()) {
                        entity.stopRiding();
                    }
                    Vec3d vec3d = entity.getPos();
                    if (!entity.teleport(g, h, j, false)) continue;
                    world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(entity));
                    SoundEvent soundEvent = entity instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ENTITY_ENDERMAN_TELEPORT;
                    world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    entity.playSound(soundEvent, 1.0f, 1.0f);
                    break;
                }
            }
        }
    }, SOUND(15, "sound", 3, 1.75f, 1.75f, 1.75f, 1.0f) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            if (entity.isSubmergedInWater()) {
                return 1.5f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            if (entity.isSubmergedInWater()) {
                return 1.5f;
            }
            return super.getDamageMultiplier(world, entity, target);
        }

        @Override
        public float getProtectionMultiplier(World world, LivingEntity entity) {
            if (entity.isSubmergedInWater()) {
                return 1.5f;
            }
            return super.getProtectionMultiplier(world, entity);
        }

        @Override
        public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
            return source.isOf(SONIC_BOOM);
        }

        @Override
        public void tick(LivingEntity entity) {
            List<Entity> otherEntities = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(15), entity1 -> entity1 instanceof LivingEntity);
            for (Entity otherEntity : otherEntities) {
                LivingEntity livingEntity = (LivingEntity) otherEntity;
                double speed = livingEntity.getSpeed();
                if (speed == 0) {
                    continue;
                }
                livingEntity.addStatusEffect(new StatusEffectInstance(GLOWING, 2, 0, true, false, false));
            }
        }

        @Override
        public float getExtraDamage(LivingEntity target, float amount) {
            if (target.getSpeed() > 0) {
                return amount;
            }
            return super.getExtraDamage(target, amount);
        }
    };

    private static final IntFunction<Element> BY_ID;
    public static final StringIdentifiable.Codec<Element> CODEC;
    public static final int MAX_SIZE = Element.values().length - 1;
    public static final HashMap<Integer, Integer> LEVEL_SIZE = new HashMap<>();
    public static final HashMap<Predicate<LivingEntity>, Float> DAMAGE_MULTIPLIER = new HashMap<>();
    private final int id;
    private final String name;
    private final int level;
    private final float miningSpeedMultiplier;
    private final float damageMultiplier;
    private final float protectionMultiplier;
    private final float durabilityMultiplier;

    Element(int id, String name, int level) {
        this(id, name, level, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    Element(int id, String name, int level, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.protectionMultiplier = protectionMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        return miningSpeedMultiplier;
    }

    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        return damageMultiplier;
    }

    public float getProtectionMultiplier(World world, LivingEntity entity) {
        return protectionMultiplier;
    }

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
        return false;
    }

    public HashMap<StatusEffect, Integer> getPersistentEffects() {
        return new HashMap<>();
    }

    public static Element byId(int id) {
        return BY_ID.apply(id);
    }

    public UUID getUuid(int slot) {
        return UUID.nameUUIDFromBytes((this.getName() + " " + slot).getBytes());
    }

    public static Element createRandom() {
        Element element;
        Random random = Random.create();
        do {
            element = Element.byId(random.nextBetween(0, Element.values().length - 1));
        } while (element == null || random.nextFloat() > (float) 1 / element.level);
        return element;
    }

    public static Element createRandom(int level) {
        level = MathHelper.clamp(level, 0, 3);
        Element element = createRandom();
        while (element.level != level) {
            element = createRandom();
        }
        return element;
    }

    public static Element createRandom(int level, ArrayList<Element> exclude) {
        level = MathHelper.clamp(level, 0, 3);
        Element element = createRandom();
        int maxSize = LEVEL_SIZE.get(level);
        List<Element> elements = new ArrayList<>();
        for (Element element1 : exclude) {
            if (element1.level == level) {
                elements.add(element1);
            }
        }
        if (elements.size() >= maxSize) {
            return INVALID;
        }
        while (element.level != level || element.isOneOf(exclude)) {
            element = createRandom();
        }
        return element;
    }

    @Nullable
    public static MutableText getElementsText(ArrayList<Element> elements, boolean prefix) {
        if (elements.size() == 0) {
            return null;
        }
        if (elements.size() == 1 && elements.get(0) == INVALID) {
            return null;
        }
        MutableText text = prefix ? Text.literal("[元素]").formatted(Formatting.WHITE) : Text.empty();
        elements.sort(Comparator.comparingInt(element -> element.id));
        for (Element element : elements) {
            Formatting color = switch (element.getLevel()) {
                case 1 -> Formatting.WHITE;
                case 2 -> Formatting.AQUA;
                case 3 -> Formatting.GOLD;
                default -> null;
            };
            if (color != null) {
                if (!text.equals(Text.empty())) {
                    text.append(" ");
                }
                text.append(Text.translatable(element.getTranslationKey()).formatted(color));
            }
        }
        return text;
    }

    public Text getTranslationName() {
        Formatting color = switch (this.getLevel()) {
            case 1 -> Formatting.WHITE;
            case 2 -> Formatting.AQUA;
            case 3 -> Formatting.GOLD;
            default -> Formatting.GRAY;
        };
        return Text.translatable(this.getTranslationKey()).formatted(color);
    }

    public String getTranslationKey() {
        return "element.elemworld." + this.getName();
    }

    public Color getColor() {
        return switch (this.getLevel()) {
            case 1 -> Color.WHITE;
            case 2 -> Color.CYAN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }

    static {
        CODEC = StringIdentifiable.createCodec(Element::values);
        BY_ID = ValueLists.createIdToValueFunction(Element::getId, Element.values(), ValueLists.OutOfBoundsHandling.ZERO);
        for (Element element : Element.values()) {
            int level = element.level;
            LEVEL_SIZE.put(level, Optional.ofNullable(LEVEL_SIZE.get(level)).orElse(0) + 1);
            element.writeDamageMultiplier(DAMAGE_MULTIPLIER);
        }
    }

    @Override
    public String asString() {
        return this.name;
    }

    public StatusEffect getEffect() {
        return Effect.get(this);
    }

    public void addEffect(LivingEntity target, @Nullable PlayerEntity attacker) {
        int sec = this.getEffectTime(target);
        if (sec > 0) {
            StatusEffectInstance effect = new StatusEffectInstance(this.getEffect(), sec * 20, 0);
            target.addStatusEffect(effect, attacker);
            if (!target.world.isClient) {
                ArrayList<ServerPlayerEntity> entities = new ArrayList<>(PlayerLookup.tracking(target));
                if (target instanceof ServerPlayerEntity player) {
                    entities.add(player);
                }
                for (ServerPlayerEntity player : entities) {
                    player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), effect));
                }
            }
        }
    }

    public void addPersistentEffects(LivingEntity entity) {
        for (Map.Entry<StatusEffect, Integer> entry : this.getPersistentEffects().entrySet()) {
            StatusEffect effect = entry.getKey();
            int amplifier = entry.getValue();
            StatusEffectInstance instance = entity.getStatusEffect(effect);
            if (instance == null || instance.isFromElement() && instance.getAmplifier() != amplifier) {
                entity.addStatusEffect(new StatusEffectInstance(effect, -1, amplifier, false, false, false).setFromElement(true));
            }
        }
    }

    public int getEffectTime(LivingEntity target) {
        return 5;
    }

    public boolean isOneOf(Element... elements) {
        return Arrays.stream(elements).anyMatch(element -> this == element);
    }

    public boolean isOneOf(ArrayList<Element> elements) {
        return elements.contains(this);
    }

    public static void register() {
        for (Element element : Element.values()) {
            if (element == Element.INVALID) {
                continue;
            }
            Registry.register(Registries.STATUS_EFFECT, id(element.getName()), new Effect(element));
        }
    }

    public boolean shouldImmuneOnDeath(LivingEntity entity) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //元素效果
    }

    public void tick(LivingEntity entity) {

    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        //物品攻击后
    }

    public void postHit(LivingEntity target, LivingEntity attacker, float amount) {
        //物品攻击后，带伤害
    }

    public void afterInjury(LivingEntity entity, DamageSource source, float amount) {
        //受伤后，带伤害
    }

    public void writeDamageMultiplier(HashMap<Predicate<LivingEntity>, Float> map) {

    }

    public float getExtraDamage(LivingEntity target, float amount) {
        return 0.0f;
    }

    public void onEffectApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {

    }

    public void onEffectRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {

    }

    public boolean shouldAddEffect(LivingEntity entity) {
        return false;
    }

    public boolean shouldAddEffectAfterInjury(LivingEntity entity, DamageSource source, float amount) {
        return false;
    }

    public void onElementApplied(LivingEntity entity, int slot) {
        if (entity.world.isClient) {
            return;
        }
        AttributeContainer attributes = entity.getAttributes();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.getAttributeModifiers(slot).entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + slot, entityAttributeModifier.getValue(), entityAttributeModifier.getOperation()));
        }
    }

    public void onElementRemoved(LivingEntity entity, int slot) {
        if (entity.world.isClient) {
            return;
        }
        AttributeContainer attributes = entity.getAttributes();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.getAttributeModifiers(slot).entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(int slot) {
        return new HashMap<>();
    }

    private static class Effect extends StatusEffect {

        private final Element element;

        private Effect(Element element) {
            super(StatusEffectCategory.HARMFUL, element.getColor().getRGB());
            this.element = element;
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return true;
        }

        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            this.element.applyUpdateEffect(entity, amplifier);
        }

        @Override
        public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            super.onApplied(entity, attributes, amplifier);
            this.element.onEffectApplied(entity, attributes, amplifier);
        }

        @Override
        public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            super.onRemoved(entity, attributes, amplifier);
            this.element.onEffectRemoved(entity, attributes, amplifier);
        }

        public static StatusEffect get(Element element) {
            return Registries.STATUS_EFFECT.get(id(element.name));
        }

        @Override
        public Text getName() {
            return this.element.getTranslationName();
        }
    }
}
