package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.*;
import com.imoonday.elemworld.init.EWElements;
import dev.emi.trinkets.api.SlotReference;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
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

import static com.imoonday.elemworld.api.ElementEntry.createRandomFor;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EWLivingEntity {

    private static final String ELEMENTS_KEY = "Elements";
    private static final String HEAL_TICK_KEY = "HealTick";
    private static final String IMMUNE_COOLDOWN_KEY = "ImmuneCooldown";
    private final List<ItemStack> oldStacks = new ArrayList<>();
    protected Set<ElementEntry> elements = new HashSet<>();
    private int healTick = 0;
    private int immuneCooldown = 0;

    @Override
    public Set<ElementEntry> getElements() {
        return elements;
    }

    @Override
    public boolean addElement(ElementEntry entry) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Element element = entry.element();
        if (element == null) {
            return false;
        }
        if (!element.isSuitableFor(entity)) {
            return false;
        }
        if (this.elements.stream().anyMatch(instance1 -> instance1.element().isOf(element))) {
            return false;
        }
        this.elements.add(entry);
        element.onElementApplied(entity, -1, entry.level());
        return true;
    }

    @Override
    public void removeElement(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (ElementEntry entry : this.elements) {
            if (entry.element().isOf(element)) {
                this.elements.remove(entry);
                element.onElementRemoved(entity, -1, entry.level());
                break;
            }
        }
    }

    @Override
    public void clearElements() {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (ElementEntry entry : this.elements) {
            entry.element().onElementRemoved(entity, -1, entry.level());
        }
        this.elements.clear();
        this.elements.add(ElementEntry.EMPTY);
    }

    @Override
    public void setElements(Set<ElementEntry> entries) {
        this.clearElements();
        for (ElementEntry entry : entries) {
            this.addElement(entry);
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

    @Inject(method = "dropLoot", at = @At("TAIL"))
    public void dropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasNoElement() || !causedByPlayer) {
            return;
        }
        WeightRandom<Element> random = WeightRandom.create();
        Set<ElementEntry> entries = entity.getElements();
        random.addAll(ElementEntry.getElementSet(entries), Element::getWeight);
        random.add(EWElements.EMPTY, EWElements.EMPTY.getWeight());
        Element element = random.next();
        if (element != null && !element.isInvalid()) {
            Item item = element.getFragmentItem();
            if (item != null) {
                entity.dropItem(item);
            }
        }
    }

    @Override
    public boolean hasNoElement() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getElements().isEmpty() || entity.getElements().size() == 1 && entity.getElements().contains(ElementEntry.EMPTY);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        elementTick();
        checkElements();
        cooldownTick();
    }

    private void elementTick() {
        LivingEntity entity = (LivingEntity) (Object) this;
        List<ElementEntry> list = entity.getAllElements(false);
        for (ElementEntry entry : list) {
            Element element = entry.element();
            if (element == null || element.isInvalid()) {
                continue;
            }
            element.tick(entity);
            Map<StatusEffect, Integer> effects = new HashMap<>();
            element.getPersistentEffects(effects);
            for (Map.Entry<StatusEffect, Integer> effectEntry : effects.entrySet()) {
                StatusEffect key = effectEntry.getKey();
                Integer value = effectEntry.getValue();
                entity.addStatusEffect(new StatusEffectInstance(key, 2, value, false, false, false));
            }
        }
        for (Element element : Element.getRegistrySet()) {
            if (element == null || element.isInvalid()) {
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
                    for (ElementEntry entry : oldStack.getElements()) {
                        Element element = entry.element();
                        if (element.isInvalid()) {
                            continue;
                        }
                        if (newStack.getElement(entry.element()).isPresent()) {
                            continue;
                        }
                        element.onElementRemoved(entity, i, entry.level());
                    }
                }
                if (!newStack.isEmpty()) {
                    for (ElementEntry entry : newStack.getElements()) {
                        Element element = entry.element();
                        if (element.isInvalid()) {
                            continue;
                        }
                        if (oldStack.getElement(entry.element()).isPresent()) {
                            continue;
                        }
                        element.onElementApplied(entity, i, entry.level());
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
            this.elements.remove(ElementEntry.EMPTY);
        }
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : entity.getArmorItems()) {
            stacks.add(stack);
        }
        stacks.add(entity.getMainHandStack());
        stacks.add(entity.getOffHandStack());
        for (ItemStack stack : stacks) {
            if (stack.hasSuitableElement() && stack.getElements().size() == 0) {
                stack.addRandomElements();
            }
        }
    }

    public boolean hasSuitableElement() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return Element.getRegistrySet().stream().anyMatch(element -> element.isSuitableFor(entity));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = new NbtList();
        for (ElementEntry entry : this.elements) {
            NbtCompound nbtCompound = entry.toNbt();
            list.add(nbtCompound);
        }
        nbt.put(ELEMENTS_KEY, list);
        nbt.putInt(HEAL_TICK_KEY, this.healTick);
        nbt.putInt(IMMUNE_COOLDOWN_KEY, this.immuneCooldown);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(ELEMENTS_KEY, NbtElement.LIST_TYPE)) {
            Set<ElementEntry> entries = new HashSet<>();
            for (NbtElement nbtElement : nbt.getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE)) {
                NbtCompound nbtCompound = (NbtCompound) nbtElement;
                Optional<ElementEntry> element = ElementEntry.fromNbt(nbtCompound);
                if (element.isEmpty()) {
                    continue;
                }
                entries.add(element.get());
            }
            this.elements = entries;
        }
        if (nbt.contains(HEAL_TICK_KEY, NbtElement.INT_TYPE)) {
            this.healTick = nbt.getInt(HEAL_TICK_KEY);
        }
        if (nbt.contains(IMMUNE_COOLDOWN_KEY, NbtElement.INT_TYPE)) {
            this.immuneCooldown = nbt.getInt(IMMUNE_COOLDOWN_KEY);
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
        for (ElementEntry entry : entity.getAllElements(false)) {
            if (entry == null || entry.element().isInvalid()) {
                continue;
            }
            if (entry.element().immuneOnDeath(entity)) {
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
        for (ElementEntry entry : entity.getAllElements(false)) {
            if (entry == null || entry.element().isInvalid()) {
                continue;
            }
            entry.element().afterInjury(entity, source, amount);
        }
        for (Element element : Element.getRegistrySet()) {
            if (element == null || element.isInvalid()) {
                continue;
            }
            if (element.shouldAddEffectAfterInjury(entity, source, amount)) {
                element.addEffect(entity, source.getAttacker());
            }
        }
        if (attacker instanceof LivingEntity living && !source.isIndirect()) {
            for (ElementEntry entry : living.getMainHandStack().getElements()) {
                if (entry == null || entry.element().isInvalid()) {
                    continue;
                }
                entry.element().postHit(entity, living, amount);
            }
        }
        if (this.healTick > 0 && !entity.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK)) {
            this.healTick = 0;
        }
        if (attacker instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.literal("本次伤害: " + amount), true);
        }
    }

    //最终伤害
    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    public void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        float finalDamage = cir.getReturnValueF();
        if (source.getAttacker() instanceof LivingEntity attacker) {
            float baseDamage = cir.getReturnValueF() + getExtraDamage(attacker, source.getSource(), entity, amount);
            finalDamage = baseDamage * getDamageMultiplier(attacker, source.getSource(), entity);
        }
        float protectionMultiplier = 1.0f;
        for (ElementEntry entry : entity.getAllElements(true)) {
            float multiplier = entry.element().getDamageProtectionMultiplier(source, entity);
            protectionMultiplier = MathHelper.clamp(protectionMultiplier, 0.0f, multiplier);
        }
        finalDamage *= protectionMultiplier;
        cir.setReturnValue(Float.valueOf(String.format("%.2f", finalDamage)));
    }

    private float getDamageMultiplier(LivingEntity attacker, Entity sourceEntity, LivingEntity target) {
        float multiplier = 1.0f;
        for (ElementEntry entry : attacker.getAllElements(true)) {
            if (entry == null || entry.element().isInvalid()) {
                continue;
            }
            float f = entry.element().getDamageMultiplier(attacker.world, attacker, target);
            multiplier += entry.getLevelMultiplier(f);
        }
        if (sourceEntity instanceof ArrowEntity arrow) {
            Potion potion = ((ArrowEntityAccessor) arrow).getPotion();
            if (potion instanceof Element.ElementPotion elementPotion) {
                Element element = elementPotion.getElement();
                if (element != null && !element.isInvalid()) {
                    float f = element.getDamageMultiplier(attacker.world, attacker, target);
                    multiplier += new ElementEntry(element, element.getMaxLevel()).getLevelMultiplier(f);
                }
            }
        } else {
            ItemStack stack = getAttackStack(attacker, sourceEntity);
            for (ElementEntry entry : stack.getElements()) {
                Element element = entry.element();
                if (element == null || element.isInvalid()) {
                    continue;
                }
                float f = element.getDamageMultiplier(attacker.world, attacker, target);
                multiplier += entry.getLevelMultiplier(f);
            }
        }
        Map<Predicate<LivingEntity>, Float> map = new HashMap<>();
        for (Element element : Element.getRegistrySet()) {
            element.getDamageMultiplier(map);
        }
        for (Map.Entry<Predicate<LivingEntity>, Float> entry : map.entrySet()) {
            if (entry.getKey().test(target)) {
                multiplier += entry.getValue();
            }
        }
        return multiplier;
    }

    private float getExtraDamage(LivingEntity attacker, Entity sourceEntity, LivingEntity target, float amount) {
        float damage = 0.0f;
        for (ElementEntry element : attacker.getAllElements(false)) {
            damage += element.element().getExtraDamage(target, amount);
        }
        if (sourceEntity instanceof ArrowEntity arrow) {
            Potion potion = ((ArrowEntityAccessor) arrow).getPotion();
            if (potion instanceof Element.ElementPotion elementPotion) {
                damage += elementPotion.getElement().getExtraDamage(target, amount);
            }
        } else {
            ItemStack stack = getAttackStack(attacker, sourceEntity);
            for (ElementEntry entry : stack.getElements()) {
                damage += entry.element().getExtraDamage(target, amount);
            }
        }
        return damage;
    }

    private static ItemStack getAttackStack(LivingEntity attacker, Entity sourceEntity) {
        return sourceEntity instanceof TridentEntity trident ? ((TridentEntityInvoker) trident).asItemStack() : attacker.getMainHandStack();
    }

    @Override
    public List<ElementEntry> getAllElements(boolean repeat) {
        LivingEntity entity = (LivingEntity) (Object) this;
        List<ElementEntry> list = new ArrayList<>(entity.getElements());
        for (ItemStack armorItem : entity.getArmorItems()) {
            for (ElementEntry entry : armorItem.getElements()) {
                if (repeat || list.stream().noneMatch(instance1 -> instance1.isElementEqual(entry))) {
                    list.add(entry);
                }
            }
        }
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            try {
                Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(entity);
                trinketComponent.ifPresent(component -> {
                    for (Pair<SlotReference, ItemStack> stackPair : component.getAllEquipped()) {
                        for (ElementEntry entry : stackPair.getRight().getElements()) {
                            if (repeat || list.stream().noneMatch(instance1 -> instance1.isElementEqual(entry))) {
                                list.add(entry);
                            }
                        }
                    }
                });
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
            success = addElement(createRandomFor(entity));
        } while (!success && this.elements.size() < Element.getRegistrySet().size());
    }

    @Override
    public boolean hasElement(Element element) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getAllElements(false).stream().anyMatch(entry -> entry.element().isOf(element));
    }

    @Override
    public boolean hasOneOf(Element... elements) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getAllElements(false).stream().anyMatch(entry -> entry.element().isIn(elements));
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
