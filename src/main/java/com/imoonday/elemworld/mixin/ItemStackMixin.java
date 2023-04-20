package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWItemStack;
import com.imoonday.elemworld.api.Element;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.api.Element.*;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EWItemStack {

    /**
     * @see #postHit(LivingEntity, PlayerEntity, CallbackInfo)
     * 击中后方法
     **/

    private static final String ELEMENTS_KEY = "Elements";

    @Override
    public ArrayList<Element> getElements() {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.hasNbt()) {
            return new ArrayList<>();
        }
        return Arrays.stream(stack.getOrCreateNbt().getIntArray(ELEMENTS_KEY)).mapToObj(Element::byId).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void setElements(ArrayList<Element> elements) {
        ItemStack stack = (ItemStack) (Object) this;
        stack.getOrCreateNbt().putIntArray(ELEMENTS_KEY, elements.stream().mapToInt(Element::getId).toArray());
    }

    @Override
    public boolean hasElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.getElements().contains(element);
    }

    @Override
    public boolean addElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        if (element == INVALID && stack.getElements().size() >= 1) {
            return false;
        }
        if (!stack.hasElement(element)) {
            List<Integer> elements = Arrays.stream(stack.getOrCreateNbt().getIntArray(ELEMENTS_KEY)).boxed().collect(Collectors.toList());
            elements.add(element.getId());
            stack.getOrCreateNbt().putIntArray(ELEMENTS_KEY, elements);
            return true;
        }
        return false;
    }

    @Override
    public void removeElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        ArrayList<Element> elements = stack.getElements();
        elements.remove(element);
        stack.setElements(elements);
    }

    @Inject(method = "onCraft", at = @At("TAIL"))
    public void onCraft(World world, PlayerEntity player, int amount, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageable()) {
            stack.addRandomElements();
        }
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageable() && stack.getElements().size() == 0) {
            stack.addRandomElements();
        }
        if (stack.getElements().size() > 1 && stack.hasElement(INVALID)) {
            ArrayList<Element> elements = new ArrayList<>(stack.getElements());
            elements.remove(INVALID);
            stack.setElements(elements);
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void elementsTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getElements().size() == 0) {
            return;
        }
        MutableText text = getElementsText(stack.getElements(),false);
        if (text != null) {
            List<Text> list = cir.getReturnValue();
            list.add(1, text);
            cir.setReturnValue(list);
        }
    }

    //攻击后执行
    @Inject(method = "postHit", at = @At("TAIL"))
    public void postHit(LivingEntity target, PlayerEntity attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            addElementEffect(target, attacker, element);
            switch (element) {
                case WATER, ICE -> {
                    if (target.isInElement(FIRE) || target.isOnFire()) {
                        target.removeElementEffect(FIRE);
                        if (target.isOnFire()) {
                            target.setOnFire(false);
                        }
                        target.damage(attacker.getDamageSources().magic(), element == WATER ? 2.0f : 3.0f);
                        target.world.addParticle(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + target.getHeight(), target.getZ(), 0, 0, 0);
                        target.world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.VOICE);
                    }
                }
                case WIND -> {
                    List<LivingEntity> entities = target.world.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(3), Entity::isLiving);
                    for (LivingEntity entity : entities) {
                        if (target.hasElementEffect(FIRE)) {
                            addElementEffect(entity, attacker, FIRE);
                        }
                        Vec3d subtract = entity.getPos().subtract(entity.getPos()).normalize();
                        Vec3d vec3d = new Vec3d(subtract.x, 1, subtract.z);
                        entity.setVelocity(vec3d);
                    }
                }
                case THUNDER -> {
                    if (target.isInElement(WATER) || target.isWet()) {
                        HashSet<Entity> entities = new HashSet<>(getConductiveEntitiesNearby(target));
                        HashSet<Entity> otherEntities = entities.stream().flatMap(entity1 -> new HashSet<>(getConductiveEntitiesNearby(entity1)).stream()).collect(Collectors.toCollection(HashSet::new));
                        entities.addAll(otherEntities);
                        for (Entity entity : entities) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(attacker.getDamageSources().magic(), 2);
                            livingEntity.removeElementEffect(WATER);
                            livingEntity.world.playSound(null, livingEntity.getBlockPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.VOICE);
                        }
                    } else if (target.isInElement(FIRE) || target.isOnFire()) {
                        target.world.createExplosion(target, attacker.getDamageSources().explosion(attacker, attacker), new ExplosionBehavior(), target.getX(), target.getY(), target.getZ(), 2, false, World.ExplosionSourceType.NONE);
                        target.removeElementEffect(FIRE);
                        target.setOnFire(false);
                    }
                }
                case LIGHT -> {
                    if (target.isInElement(DARKNESS)) {
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
            }
        }
    }

    private static List<Entity> getConductiveEntitiesNearby(Entity entity) {
        return entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(5), entity1 -> entity1 instanceof LivingEntity livingEntity && !livingEntity.hasOneOfElements(EARTH, THUNDER) && (livingEntity.hasElement(WATER) || livingEntity.isWet()));
    }

    private static void addElementEffect(LivingEntity target, PlayerEntity attacker, Element element) {
        int sec = target.hasElement(EARTH) && element.isOneOf(WATER, FIRE) ? 3 : 5;
        target.addStatusEffect(new StatusEffectInstance(element.asEffect(), sec * 20, 0), attacker);
    }

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element element : stack.getElements()) {
            multiplier *= element.getDurabilityMultiplier();
        }
        int value = (int) (cir.getReturnValueI() * multiplier);
        cir.setReturnValue(value);
    }

    @Override
    public void addRandomElements() {
        ItemStack stack = (ItemStack) (Object) this;
        addRandomElement(stack);
        if (Random.create().nextFloat() < 0.5f) {
            addRandomElement(stack);
        }
    }

    @Override
    public void addNewRandomElements(int count) {
        ItemStack stack = (ItemStack) (Object) this;
        for (int i = 0; i < count; i++) {
            addNewRandomElement(stack);
        }
    }

    private static void addRandomElement(ItemStack stack) {
        float chance = Random.create().nextFloat();
        if (chance < 0.05f) {
            addRandomElement(stack, 3);
        } else if (chance < 0.25f) {
            addRandomElement(stack, 2);
        } else if (chance < 0.75f) {
            addRandomElement(stack, 1);
        } else {
            stack.addElement(INVALID);
        }
    }

    private static void addNewRandomElement(ItemStack stack) {
        float chance = Random.create().nextFloat();
        if (chance < 0.05f) {
            addNewRandomElement(stack, 3);
        } else if (chance < 0.25f) {
            addNewRandomElement(stack, 2);
        } else {
            addNewRandomElement(stack, 1);
        }
    }

    private static void addRandomElement(ItemStack stack, int level) {
        while (true) {
            boolean success = stack.addElement(createRandom(level));
            int maxSize = level == 0 ? 1 : 5;
            List<Element> list = new ArrayList<>();
            for (Element element : stack.getElements()) {
                if (element.getLevel() == level) {
                    list.add(element);
                }
            }
            boolean isFull = list.size() >= maxSize;
            if (success || isFull) break;
        }
    }

    private static void addNewRandomElement(ItemStack stack, int level) {
        stack.addElement(Element.createRandom(level, stack.getElements()));
    }
}
