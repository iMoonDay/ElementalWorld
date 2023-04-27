package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWLivingEntity;
import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWElements;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.function.Predicate;

import static com.imoonday.elemworld.api.Element.createRandom;
import static com.imoonday.elemworld.api.Element.getElementsText;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EWLivingEntity {

    private static final String ELEMENTS_KEY = "Elements";
    private static final String HEAL_TICK_KEY = "HealTick";
    private static final String IMMUNE_COOLDOWN_KEY = "ImmuneCooldown";
    private final List<ItemStack> oldStacks = new ArrayList<>();
    private ArrayList<Element> elements = new ArrayList<>();
    private int healTick = 0;
    private int immuneCooldown = 0;

    @Override
    public ArrayList<Element> getElements() {
        return elements;
    }

    @Override
    public boolean addElement(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (element == null) {
            return false;
        }
        if (!element.isSuitableFor(entity)) {
            return false;
        }
        if (this.elements.contains(element)) {
            return false;
        }
        this.elements.add(element);
        element.onElementApplied(entity, -1);
        return true;
    }

    @Override
    public void removeElement(Element elements) {
        LivingEntity entity = (LivingEntity) (Object) this;
        this.elements.remove(elements);
        elements.onElementRemoved(entity, -1);
    }

    @Override
    public void clearElements() {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (Element element : this.elements) {
            element.onElementRemoved(entity, -1);
        }
        this.elements.clear();
        this.elements.add(EWElements.EMPTY);
    }

    @Override
    public void setElements(ArrayList<Element> elements) {
        this.clearElements();
        for (Element element : elements) {
            this.addElement(element);
        }
    }

    @Override
    public int getHealTick() {
        return healTick;
    }

    @Override
    public void setHealTick(int healTick) {
        this.healTick = healTick;
    }

    @Override
    public int getImmuneCooldown() {
        return immuneCooldown;
    }

    @Override
    public void setImmuneCooldown(int immuneCooldown) {
        this.immuneCooldown = immuneCooldown;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        elementTick();
        checkEffects();
        checkElements();
        setNameWithElements();
        cooldownTick();
    }

    private void elementTick() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ArrayList<Element> list = entity.getAllElements();
        for (Element element : list) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            element.tick(entity);
            element.addPersistentEffects(entity);
        }
        for (Element element : Element.getRegistrySet()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            if (element.shouldAddEffect(entity)) {
                element.addEffect(entity, null);
            }
        }
        if (!entity.world.isClient) {
            checkElementChange();
        }
    }

    private void checkElementChange() {
        LivingEntity entity = (LivingEntity) (Object) this;
        List<ItemStack> newStacks = Arrays.asList(entity.getEquippedStack(EquipmentSlot.HEAD), entity.getEquippedStack(EquipmentSlot.CHEST), entity.getEquippedStack(EquipmentSlot.LEGS), entity.getEquippedStack(EquipmentSlot.FEET));
        List<ItemStack> oldStacks = this.oldStacks;
        if (oldStacks.size() == newStacks.size()) {
            for (int i = 0; i < newStacks.size(); i++) {
                ItemStack newStack = newStacks.get(i);
                ItemStack oldStack = oldStacks.get(i);
                if (ItemStack.areEqual(oldStack, newStack)) {
                    continue;
                }
                if (!oldStack.isEmpty()) {
                    for (Element element : oldStack.getElements()) {
                        if (element == null) {
                            continue;
                        }
                        if (element.isInvalid()) {
                            continue;
                        }
                        if (newStack.getElements().contains(element)) {
                            continue;
                        }
                        element.onElementRemoved(entity, i);
                    }
                }
                if (!newStack.isEmpty()) {
                    for (Element element : newStack.getElements()) {
                        if (element == null) {
                            continue;
                        }
                        if (element.isInvalid()) {
                            continue;
                        }
                        if (oldStack.getElements().contains(element)) {
                            continue;
                        }
                        element.onElementApplied(entity, i);
                    }
                }
            }
        }
        this.oldStacks.clear();
        newStacks.forEach(stack -> this.oldStacks.add(stack.copy()));
    }

    private void cooldownTick() {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {
            return;
        }
        if (this.immuneCooldown > 0) {
            this.immuneCooldown--;
        }
        if (this.immuneCooldown < 0) {
            this.immuneCooldown = 0;
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
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {
            return;
        }
        if (hasSuitableElement() && this.elements.isEmpty()) {
            addRandomElements();
        }
        if (this.elements.size() > 1) {
            this.elements.remove(EWElements.EMPTY);
        }
    }

    public boolean hasSuitableElement() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return Element.getRegistrySet().stream().anyMatch(element -> element.isSuitableFor(entity));
    }

    private void setNameWithElements() {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {
            return;
        }
        List<Text> texts = getElementsText(this.elements, true, false);
        if (entity.getCustomName() == null || entity.getCustomName().getString().startsWith("[元素]")) {
            entity.setCustomName(!texts.isEmpty() ? texts.get(0) : null);
        }
    }

    private void checkEffects() {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (Element element : Element.getRegistrySet()) {
            if (entity.isIn(element)) {
                continue;
            }
            Set<StatusEffect> effects = element.getPersistentEffects().keySet();
            for (StatusEffect effect : effects) {
                StatusEffectInstance instance = entity.getStatusEffect(effect);
                if (instance == null || !instance.isFromElement()) {
                    continue;
                }
                entity.removeStatusEffect(effect);
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = new NbtList();
        for (Element element : this.elements) {
            NbtCompound nbtCompound = element.toNbt();
            list.add(nbtCompound);
        }
        nbt.put(ELEMENTS_KEY, list);
        nbt.putInt(HEAL_TICK_KEY, this.healTick);
        nbt.putInt(IMMUNE_COOLDOWN_KEY, this.immuneCooldown);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(ELEMENTS_KEY, NbtElement.LIST_TYPE)) {
            ArrayList<Element> list = new ArrayList<>();
            for (NbtElement nbtElement : nbt.getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE)) {
                NbtCompound nbtCompound = (NbtCompound) nbtElement;
                Element element = Element.fromNbt(nbtCompound);
                list.add(element);
            }
            this.elements = list;
        }
        if (nbt.contains(HEAL_TICK_KEY, NbtElement.INT_TYPE)) {
            this.healTick = nbt.getInt(HEAL_TICK_KEY);
        }
        if (nbt.contains(IMMUNE_COOLDOWN_KEY, NbtElement.INT_TYPE)) {
            this.immuneCooldown = nbt.getInt(IMMUNE_COOLDOWN_KEY);
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
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            if (element.ignoreDamage(source, entity)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHealth()F"), cancellable = true)
    public void tryImmune(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getHealth() - amount > 0) {
            return;
        }
        if (shouldImmuneOnDeath()) {
            ci.cancel();
        }
    }

    private boolean shouldImmuneOnDeath() {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (Element element : entity.getAllElements()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            if (element.shouldImmuneOnDeath(entity)) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "applyDamage", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void afterInjury(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (amount <= 0) {
            return;
        }
        Entity attacker = source.getAttacker();
        for (Element element : entity.getAllElements()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            element.afterInjury(entity, source, amount);
        }
        for (Element element : Element.getRegistrySet()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            if (element.shouldAddEffectAfterInjury(entity, source, amount)) {
                element.addEffect(entity, source.getAttacker());
            }
        }
        if (attacker instanceof LivingEntity living && !source.isIndirect()) {
            ArrayList<Element> list = living.getMainHandStack().getElements();
            for (Element element : list) {
                if (element == null) {
                    continue;
                }
                if (element.isInvalid()) {
                    continue;
                }
                element.postHit(entity, living, amount);
            }
        }
        if (this.healTick > 0 && !entity.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK)) {
            this.healTick = 0;
        }
        if (attacker instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.literal("本次伤害: " + amount), true);
        }
    }

    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    public void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (source.getAttacker() instanceof LivingEntity attacker) {
            float baseDamage = cir.getReturnValueF() + getExtraDamage(attacker, source.getSource(), entity, amount);
            float finalDamage = baseDamage * getDamageMultiplier(attacker, source.getSource(), entity);
            cir.setReturnValue(Float.valueOf(String.format("%.2f", finalDamage)));
        }
    }

    private float getDamageMultiplier(LivingEntity attacker, Entity sourceEntity, LivingEntity target) {
        ItemStack stack = getAttackStack(attacker, sourceEntity);
        float multiplier = 1.0f;
        for (Element element : attacker.getAllElements()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getDamageMultiplier(attacker.world, attacker, target) - 1;
            multiplier += element.getLevelMultiplier(f);
        }
        for (Element element : stack.getElements()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getDamageMultiplier(attacker.world, attacker, target) - 1;
            multiplier += element.getLevelMultiplier(f);
        }
        for (Map.Entry<Predicate<LivingEntity>, Float> entry : Element.getDamageMultiplierMap().entrySet()) {
            if (entry.getKey().test(target)) {
                multiplier += entry.getValue() - 1;
            }
        }
        return multiplier;
    }

    private float getExtraDamage(LivingEntity attacker, Entity sourceEntity, LivingEntity target, float amount) {
        ItemStack stack = getAttackStack(attacker, sourceEntity);
        float damage = 0.0f;
        for (Element element : attacker.getAllElements()) {
            damage += element.getExtraDamage(target, amount);
        }
        for (Element element : stack.getElements()) {
            damage += element.getExtraDamage(target, amount);
        }
        return damage;
    }

    private static ItemStack getAttackStack(LivingEntity attacker, Entity sourceEntity) {
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
        return stack;
    }

    @Inject(method = "getArmor", at = @At("RETURN"), cancellable = true)
    public void getArmor(CallbackInfoReturnable<Integer> cir) {
        float multiplier = getProtectionMultiplier();
        int value = (int) (cir.getReturnValueI() * multiplier);
        if (value == 0 && multiplier != 1.0f) {
            value += multiplier;
        }
        cir.setReturnValue(value);
    }

    private float getProtectionMultiplier() {
        LivingEntity entity = (LivingEntity) (Object) this;
        float multiplier = 1.0f;
        for (Element element : entity.getAllElements()) {
            if (element == null) {
                continue;
            }
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getProtectionMultiplier(entity.world, entity) - 1;
            multiplier += element.getLevelMultiplier(f);
        }
        return multiplier;
    }

    @Override
    public ArrayList<Element> getAllElements() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ArrayList<Element> list = new ArrayList<>(entity.getElements());
        for (ItemStack armorItem : entity.getArmorItems()) {
            armorItem.getElements().stream().filter(element -> !list.contains(element)).forEach(list::add);
        }
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            try {
                Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(entity);
                trinketComponent.ifPresent(component -> component.getAllEquipped()
                        .forEach(stackPair -> stackPair.getRight().getElements()
                                .stream().filter(element -> !list.contains(element))
                                .forEach(list::add)));
            } catch (Exception ignore) {

            }
        }
        return list;
    }

    private void addRandomElements() {
        float chance = 1.0f;
        Random random = Random.create();
        do {
            addRandomElement();
            chance /= 2;
        } while (random.nextFloat() < chance);
    }

    private void addRandomElement() {
        LivingEntity entity = (LivingEntity) (Object) this;
        boolean success;
        do {
            success = addElement(createRandom(entity));
        } while (!success && this.elements.size() < Element.getRegistryMap().size());
    }

    @Override
    public boolean hasElement(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getAllElements().contains(element);
    }

    @Override
    public boolean hasOneOf(Element... elements) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getAllElements().stream().anyMatch(elem -> elem.isIn(elements));
    }

    @Override
    public boolean hasEffectOf(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.hasStatusEffect(element.getEffect());
    }

    @Override
    public void removeEffectOf(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffectOf(element)) {
            entity.removeStatusEffect(element.getEffect());
        }
    }

    @Override
    public boolean isIn(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.hasEffectOf(element) || entity.hasElement(element);
    }

    @Override
    public void decelerate(double multiplier) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Vec3d velocity = entity.getVelocity();
        entity.setVelocity(velocity.x * multiplier, velocity.y, velocity.z * multiplier);
    }

    @Override
    public boolean isHolding(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getMainHandStack().hasElement(element) || entity.getOffHandStack().hasElement(element);
    }
}
