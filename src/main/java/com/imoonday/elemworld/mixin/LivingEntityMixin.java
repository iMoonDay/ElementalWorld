package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWLivingEntity;
import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.effect.ElementEffect;
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
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
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

    private static final String ELEMENTS_KEY = "Elements";
    private ArrayList<Element> elements = new ArrayList<>();

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
        if (this.elements.isEmpty()) {
            addRandomElements();
        }
        if (this.elements.size() > 1) {
            this.elements.remove(INVALID);
        }
        checkEffects(StatusEffects.JUMP_BOOST, WIND);
        checkEffects(StatusEffects.SPEED, WIND, LIGHT, DARKNESS);
        checkEffects(StatusEffects.NIGHT_VISION, LIGHT);
        MutableText text = getElementsText(this.elements);
        entity.setCustomName(text);
    }

    private void checkEffects(StatusEffect type, Element... keys) {
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
                entity.addStatusEffect(new StatusEffectInstance(type, -1, 0, false, false, false));
            }
        } else if (effect != null && effect.isInfinite()) {
            entity.removeStatusEffect(type);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putIntArray(ELEMENTS_KEY, this.elements.stream().mapToInt(Element::getId).toArray());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(ELEMENTS_KEY, NbtElement.INT_ARRAY_TYPE)) {
            this.elements = Arrays.stream(nbt.getIntArray(ELEMENTS_KEY)).mapToObj(Element::byId).collect(Collectors.toCollection(ArrayList::new));
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
        if (source.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().getElements().contains(FIRE)) {
            if (entity.isInElement(WATER)) {
                StatusEffectInstance effect = entity.getStatusEffect(ElementEffect.get(WATER));
                if (effect != null) {
                    entity.setStatusEffect(new StatusEffectInstance(ElementEffect.get(WATER), Math.max(effect.getDuration() - 3 * 20, 0), effect.getAmplifier(), false, effect.shouldShowParticles(), effect.shouldShowIcon()), source.getAttacker());
                }
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "applyDamage", at = @At("TAIL"))
    public void applyDamage(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Entity attacker = source.getAttacker();
        if (attacker instanceof PlayerEntity player) {
            for (Element element : player.getMainHandStack().getElements()) {
                if (element == WATER && (entity.isInElement(FIRE) || entity.isOnFire())) {
                    if (entity.hasElementEffect(FIRE)) {
                        entity.removeStatusEffect(ElementEffect.get(FIRE));
                    }
                    if (entity.isOnFire()) {
                        entity.setOnFire(false);
                    }
                    entity.damage(player.getDamageSources().playerAttack(player), amount * 1.5f);
                    entity.world.addParticle(ParticleTypes.LARGE_SMOKE, entity.getX(), entity.getY() + entity.getHeight(), entity.getZ(), 0, 0, 0);
                    entity.world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.VOICE);
                }
            }
        }
    }

    private static void checkSpaceElement(DamageSource source, CallbackInfoReturnable<Boolean> cir, LivingEntity entity, Element element) {
        if (element == SPACE) {
            if (Arrays.stream(element.getIgnoreDamageTypes()).anyMatch(source::isOf)) {
                if (source.isOf(DamageTypes.DROWN)) {
                    List<BlockPos> posList = BlockPos.stream(entity.getBoundingBox(entity.getPose())).toList();
                    for (BlockPos pos : posList) {
                        FluidState fluidState = entity.world.getBlockState(pos).getFluidState();
                        if (fluidState.isOf(Fluids.WATER) || fluidState.isOf(Fluids.FLOWING_WATER)) {
                            entity.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        }
                    }
                } else {
                    randomTeleport(entity);
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
        if (source.getAttacker() instanceof LivingEntity entity) {
            float multiplier = getDamageMultiplier(entity);
            float value = cir.getReturnValueF() * multiplier;
            cir.setReturnValue(value);
        }
    }

    private float getDamageMultiplier(LivingEntity attacker) {
        float multiplier = 1.0f;
        for (Element element : attacker.getAllElements()) {
            multiplier *= element.getDamageMultiplier(attacker.world, attacker);
        }
        for (Element element : attacker.getMainHandStack().getElements()) {
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
    public boolean hasElementEffect(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.hasStatusEffect(ElementEffect.get(element));
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
