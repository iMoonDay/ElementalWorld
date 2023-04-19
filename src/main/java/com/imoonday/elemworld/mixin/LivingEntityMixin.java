package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWLivingEntity;
import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.api.Element.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EWLivingEntity {

    /**@see #resetHealTick
     * 受伤后方法
     * @see #tryImmune
     * 死亡时方法
     * @see #afterDamaged(DamageSource, float, CallbackInfo)
     * 扣除血量后方法
     * **/

    private static final String ELEMENTS_KEY = "Elements";
    private static final String HEAL_TICK_KEY = "HealTick";
    private static final String IMMUNE_COOLDOWN_TICK_KEY = "ImmuneCooldownTick";
    private ArrayList<Element> elements = new ArrayList<>();
    private int healTick = 0;
    private int immuneCooldownTick = 0;

    @Override
    public ArrayList<Element> getElements() {
        return elements;
    }

    @Override
    public boolean addElement(Element element) {
        if (!this.elements.contains(element)) {
            this.elements.add(element);
            return true;
        }
        return false;
    }

    @Override
    public void removeElement(Element elements) {
        this.elements.remove(elements);
    }

    @Override
    public void clearElements() {
        this.elements = new ArrayList<>();
        this.elements.add(INVALID);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {
            return;
        }
        checkElements();
        checkEffects(StatusEffects.JUMP_BOOST, 0, WIND);
        checkEffects(StatusEffects.SPEED, 0, WIND, LIGHT, DARKNESS);
        checkEffects(StatusEffects.NIGHT_VISION, 0, LIGHT);
        checkEffects(StatusEffects.HEALTH_BOOST, 1, ROCK);
        setNameWithElements();
        elementsTick();
    }

    private void elementsTick() {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasElement(SOUND)) {
            List<Entity> otherEntities = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(15), entity1 -> entity1 instanceof LivingEntity);
            for (Entity otherEntity : otherEntities) {
                LivingEntity livingEntity = (LivingEntity) otherEntity;
                double speed = livingEntity.getSpeed();
                if (speed == 0) {
                    continue;
                }
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 0, true, false, false));
            }
        } else if (entity.hasElement(GRASS) && entity.getHealth() < entity.getMaxHealth()) {
            this.healTick++;
            if (entity.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK) && this.healTick >= 5 * 20 || this.healTick >= 10 * 20) {
                entity.heal(1);
                this.healTick = 0;
            }
        }
        if (this.immuneCooldownTick > 0) {
            this.immuneCooldownTick--;
        }
        if (this.immuneCooldownTick < 0) {
            this.immuneCooldownTick = 0;
        }
    }

    @Override
    public double getSpeed() {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        double dx = livingEntity.getX() - livingEntity.lastRenderX;
        double dy = livingEntity.getY() - livingEntity.lastRenderY;
        double dz = livingEntity.getZ() - livingEntity.lastRenderZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return dist * 20;
    }

    private void checkElements() {
        if (this.elements.isEmpty()) {
            addRandomElements();
        }
        if (this.elements.size() > 1) {
            this.elements.remove(INVALID);
        }
    }

    private void setNameWithElements() {
        LivingEntity entity = (LivingEntity) (Object) this;
        MutableText text = getElementsText(this.elements);
        if (entity.getCustomName() == null || entity.getCustomName().getString().startsWith("[") && entity.getCustomName().getString().endsWith("]")) {
            entity.setCustomName(text);
        }
    }

    private void checkEffects(StatusEffect type, int amplifier, Element... keys) {
        LivingEntity entity = (LivingEntity) (Object) this;
        boolean hasElement = false;
        for (Element element : entity.getAllElements()) {
            for (Element key : keys) {
                if (element == key) {
                    hasElement = true;
                    break;
                }
            }
        }
        StatusEffectInstance effect = entity.getStatusEffect(type);
        if (hasElement) {
            if (effect == null) {
                entity.addStatusEffect(new StatusEffectInstance(type, -1, amplifier, true, false, false));
            } else if (effect.getAmplifier() < amplifier) {
                entity.setStatusEffect(new StatusEffectInstance(type, -1, amplifier, true, false, false), null);
            }
        } else if (effect != null && effect.isInfinite() && effect.isAmbient()) {
            entity.removeStatusEffect(type);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putIntArray(ELEMENTS_KEY, this.elements.stream().mapToInt(Element::getId).toArray());
        nbt.putInt(HEAL_TICK_KEY, this.healTick);
        nbt.putInt(IMMUNE_COOLDOWN_TICK_KEY, this.immuneCooldownTick);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(ELEMENTS_KEY, NbtElement.INT_ARRAY_TYPE)) {
            this.elements = Arrays.stream(nbt.getIntArray(ELEMENTS_KEY)).mapToObj(Element::byId).collect(Collectors.toCollection(ArrayList::new));
        }
        if (nbt.contains(HEAL_TICK_KEY)) {
            this.healTick = nbt.getInt(HEAL_TICK_KEY);
        }
        if (nbt.contains(IMMUNE_COOLDOWN_TICK_KEY)) {
            this.immuneCooldownTick = nbt.getInt(IMMUNE_COOLDOWN_TICK_KEY);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isInvulnerableTo(source)) {
            cir.setReturnValue(false);
        }
        if (entity.world.isClient) {
            cir.setReturnValue(false);
        }
        for (Element element : entity.getAllElements()) {
            checkSpaceElement(source, cir, entity, element);
        }
        for (Element element : entity.getAllElements()) {
            if (Arrays.stream(element.getIgnoreDamageTypes()).anyMatch(source::isOf)) {
                if (element == DARKNESS && source.getAttacker() instanceof LivingEntity living && living.getMainHandStack().hasElement(LIGHT)) {
                    continue;
                }
                cir.setReturnValue(false);
            }
            BlockState state = entity.getSteppingBlockState();
            if (element == GRASS && source.isOf(DamageTypes.FALL) && (state.isOf(Blocks.GRASS_BLOCK) || state.isIn(BlockTags.LEAVES))) {
                cir.setReturnValue(false);
            }
        }
        if (source.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().hasElement(FIRE)) {
            if (entity.isInElement(WATER)) {
                StatusEffectInstance effect = entity.getStatusEffect(Element.getEffect(WATER));
                if (effect != null) {
                    entity.setStatusEffect(new StatusEffectInstance(Element.getEffect(WATER), Math.max(effect.getDuration() - 3 * 20, 0), effect.getAmplifier(), false, effect.shouldShowParticles(), effect.shouldShowIcon()), source.getAttacker());
                }
                cir.setReturnValue(false);
            }
        }
    }

    //受伤后执行
    @Inject(method = "damage", at = @At("RETURN"))
    public void resetHealTick(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (cir.getReturnValueZ()) {
            boolean resettable = !entity.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK);
            if (this.healTick > 0 && resettable) {
                this.healTick = 0;
            }
        }
    }

    //死亡时执行
    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHealth()F"), cancellable = true)
    public void tryImmune(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getHealth() - amount > 0) {
            return;
        }
        if (entity.hasElement(TIME) && this.immuneCooldownTick <= 0) {
            this.immuneCooldownTick = 5 * 60 * 20;
            entity.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f);
            ci.cancel();
        }
        if (entity.hasElement(SPACE)) {
            if (Random.create().nextFloat() < 0.25f) {
                randomTeleport(entity);
                entity.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f);
                ci.cancel();
            }
        }
    }

    //受伤后执行
    @Inject(method = "applyDamage", at = @At("TAIL"))
    public void afterDamaged(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasElement(TIME)) {
            if (this.immuneCooldownTick == 0) {
                Random random = Random.create();
                if (random.nextFloat() < 0.0625f) {
                    entity.setHealth(entity.getMaxHealth());
                } else if (entity.getHealth() / entity.getMaxHealth() <= 0.2f) {
                    if (random.nextFloat() < 0.25f) {
                        entity.setHealth(entity.getMaxHealth() / 2f);
                    }
                }
            }
        }
        if (source.getAttacker() instanceof LivingEntity living && (source.isOf(DamageTypes.PLAYER_ATTACK) || source.isOf(DamageTypes.MOB_ATTACK)) && living.getMainHandStack().hasElement(SOUND)) {
            if (entity.getSpeed() > 0) {
                entity.damage(living.getDamageSources().sonicBoom(living), amount);
            }
        }
    }

    private static void checkSpaceElement(DamageSource source, CallbackInfoReturnable<Boolean> cir, LivingEntity entity, Element element) {
        if (element == SPACE) {
            if (Arrays.stream(element.getIgnoreDamageTypes()).anyMatch(source::isOf)) {
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
                cir.setReturnValue(false);
            } else if (!source.isIndirect()) {
                float random = Random.create().nextFloat();
                if (random < 0.25f) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    public void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            float value = cir.getReturnValueF() * getDamageMultiplier(attacker, source.getSource());
            cir.setReturnValue(value);
        }
    }

    private float getDamageMultiplier(LivingEntity attacker, Entity sourceEntity) {
        ItemStack stack;
        if (sourceEntity instanceof TridentEntity trident) {
            stack = trident.asItemStack();
        } else {
            ItemStack mainHandStack = attacker.getMainHandStack();
            ItemStack offHandStack = attacker.getOffHandStack();
            if (sourceEntity instanceof ArrowEntity) {
                if (mainHandStack.isOf(Items.BOW) || mainHandStack.isOf(Items.CROSSBOW)) {
                    stack = mainHandStack;
                } else if (offHandStack.isOf(Items.BOW) || offHandStack.isOf(Items.CROSSBOW)) {
                    stack = offHandStack;
                } else {
                    stack = ItemStack.EMPTY;
                }
            } else {
                stack = mainHandStack;
            }
        }
        float multiplier = 1.0f;
        for (Element element : attacker.getAllElements()) {
            multiplier = element.getDamageMultiplier(attacker.world, attacker);
        }
        for (Element element : stack.getElements()) {
            multiplier *= element.getDamageMultiplier(attacker.world, attacker);
        }
        return multiplier;
    }

    @Inject(method = "getArmor", at = @At("RETURN"), cancellable = true)
    public void getArmor(CallbackInfoReturnable<Integer> cir) {
        float multiplier = getProtectionMultiplier();
        int value = (int) (cir.getReturnValueI() * multiplier);
        cir.setReturnValue(value);
    }

    private float getProtectionMultiplier() {
        LivingEntity entity = (LivingEntity) (Object) this;
        float multiplier = 1.0f;
        for (Element element : entity.getAllElements()) {
            multiplier *= element.getProtectionMultiplier(entity.world, entity);
        }
        return multiplier;
    }

    @Override
    public ArrayList<Element> getAllElements() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ArrayList<Element> list = new ArrayList<>(this.elements);
        for (ItemStack armorItem : entity.getArmorItems()) {
            for (Element element : armorItem.getElements()) {
                if (!list.contains(element)) {
                    list.add(element);
                }
            }
        }
        return list;
    }

    private void addRandomElements() {
        addRandomElement();
        Random random = Random.create();
        if (random.nextFloat() < 0.5f) {
            addRandomElement();
            if (random.nextFloat() < 0.1) {
                addRandomElement();
            }
        }
    }

    private void addRandomElement() {
        float chance = Random.create().nextFloat();
        if (chance < 0.05f) {
            addRandomElement(3);
        } else if (chance < 0.25f) {
            addRandomElement(2);
        } else if (chance < 0.75f) {
            addRandomElement(1);
        } else {
            addElement(INVALID);
        }
    }

    private void addRandomElement(int level) {
        while (true) {
            boolean success = addElement(createRandom(level));
            if (success) break;
        }
    }

    @Override
    public boolean hasElement(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getAllElements().contains(element);
    }

    @Override
    public boolean hasOneOfElements(Element... elements) {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (Element elem : entity.getAllElements()) {
            if (elem.isOneOf(elements)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasElementEffect(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.hasStatusEffect(Element.getEffect(element));
    }

    @Override
    public void removeElementEffect(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasElementEffect(element)) {
            entity.removeStatusEffect(Element.getEffect(element));
        }
    }

    @Override
    public boolean isInElement(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.hasElementEffect(element) || entity.hasElement(element);
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
}
