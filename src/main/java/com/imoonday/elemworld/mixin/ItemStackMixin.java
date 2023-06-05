package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWItems;
import com.imoonday.elemworld.interfaces.EWItemStack;
import com.imoonday.elemworld.interfaces.BaseElement;
import com.imoonday.elemworld.items.ElementBookItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.init.EWElements.EMPTY;
import static com.imoonday.elemworld.init.EWTranslationKeys.*;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EWItemStack {

    private static final String ELEMENTS_KEY = "Elements";

    @Override
    public Set<Element.Entry> getElements() {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.hasNbt()) {
            return new HashSet<>();
        }
        Set<Element.Entry> elements = new HashSet<>();
        for (NbtElement nbtElement : stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbt = (NbtCompound) nbtElement;
            Optional<Element.Entry> entry = Element.Entry.fromNbt(nbt);
            if (entry.isEmpty()) {
                continue;
            }
            elements.add(entry.get());
        }
        return elements;
    }

    @Override
    public Set<Element.Entry> getStoredElementsIfBook() {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.isOf(EWItems.ELEMENT_BOOK) ? ElementBookItem.getElements(stack) : stack.getElements();
    }

    @Override
    public void setElements(Set<Element.Entry> entries) {
        if (entries == null) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        NbtList list = new NbtList();
        for (Element.Entry entry : entries) {
            if (entry == null) {
                continue;
            }
            NbtCompound nbt = entry.toNbt();
            list.add(nbt);
        }
        stack.getOrCreateNbt().put(ELEMENTS_KEY, list);
    }

    @Override
    public ItemStack withElements(Set<Element.Entry> entries) {
        ItemStack stack = (ItemStack) (Object) this;
        stack.setElements(entries);
        return stack;
    }

    @Override
    public ItemStack withElements(Element... elements) {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.withElements(Arrays.stream(elements).map(Element::withRandomLevel).collect(Collectors.toSet()));
    }

    @Override
    public boolean hasElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.getElements().stream().anyMatch(entry -> entry.element().isOf(element));
    }

    @Override
    public boolean hasElement() {
        ItemStack stack = (ItemStack) (Object) this;
        Set<Element.Entry> entries = stack.getElements();
        if (entries.size() == 1) {
            return !entries.stream().findFirst().get().element().isInvalid();
        }
        return entries.size() > 0;
    }

    @Override
    public boolean addElement(Element.Entry entry) {
        Element element = entry.element();
        ItemStack stack = (ItemStack) (Object) this;
        if (element.isOf(EMPTY) && stack.getElements().size() >= 1) {
            return false;
        }
        if (!element.isSuitableFor(stack)) {
            return false;
        }
        if (stack.hasElement(element)) {
            return false;
        }
        NbtList list = stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE);
        list.add(entry.toNbt());
        stack.getOrCreateNbt().put(ELEMENTS_KEY, list);
        return true;
    }

    @Override
    public void addStoredElementIfBook(Element.Entry entry) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isOf(EWItems.ELEMENT_BOOK)) {
            ElementBookItem.addElement(stack, entry);
        } else {
            stack.addElement(entry);
        }
    }

    @Override
    public void removeElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        Set<Element.Entry> entries = stack.getElements();
        entries.removeIf(entry -> entry.element().isOf(element));
        stack.setElements(entries);
    }

    @Override
    public void removeInvalidElements() {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isOf(EWItems.ELEMENT_BOOK)) {
            Set<Element.Entry> entries = ElementBookItem.getElements(stack);
            if (entries.size() > 1) {
                entries.removeIf(entry -> entry.element().isInvalid());
                stack.getOrCreateNbt().remove(ElementBookItem.STORED_ELEMENTS_KEY);
                entries.forEach(entry -> ElementBookItem.addElement(stack, entry));
            }
        }
        Set<Element.Entry> entries = stack.getElements();
        if (entries.size() >= 1) {
            entries.removeIf(entry -> entry.element().isInvalid());
            stack.setElements(entries);
        }
    }

    @Override
    public Optional<Element.Entry> getElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.hasNbt()) {
            return Optional.empty();
        }
        for (NbtElement nbtElement : stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbt = (NbtCompound) nbtElement;
            Optional<Element.Entry> optional = Element.Entry.fromNbt(nbt);
            if (optional.isEmpty()) {
                continue;
            }
            if (optional.get().element().isOf(element)) {
                return optional;
            }
        }
        return Optional.empty();
    }

    @Inject(method = "onCraft", at = @At("TAIL"))
    public void onCraft(World world, PlayerEntity player, int amount, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.hasSuitableElement()) {
            stack.addRandomElements();
        }
    }

    @Override
    public boolean hasSuitableElement() {
        ItemStack stack = (ItemStack) (Object) this;
        return Element.getRegistrySet(false).stream().anyMatch(element -> element.isSuitableFor(stack));
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        checkElement();
    }

    @Override
    public void checkElement() {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.hasSuitableElement() && stack.getElements().size() == 0) {
            stack.addRandomElements();
        }
        stack.removeInvalidElements();
        if (stack.getItem() instanceof BaseElement baseElement) {
            baseElement.checkBaseElement(stack);
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"))
    public void elementsTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        List<Text> list = cir.getReturnValue();
        if (list == null) {
            return;
        }
        if (player == null) {
            return;
        }
        Set<Element.Entry> elements = stack.getElements();
        if (hasSuitableElement() && elements.size() == 0) {
            list.add(1, Text.translatable(ELEMENT_NOT_GENERATED).formatted(Formatting.GRAY));
            return;
        }
        List<MutableText> texts = Element.getElementsText(elements, false, elements.size() > 5);
        if (texts.isEmpty()) {
            return;
        }
        int index = 1;
        for (Text text : texts) {
            list.add(index++, text);
        }
        float damage = stack.getDamageMultiplier(player);
        float maxHealth = stack.getMaxHealthMultiplier(player);
        float mine = stack.getMiningSpeedMultiplier(player);
        float durability = stack.getDurabilityMultiplier();
        if (damage != 1.0f || maxHealth != 1.0f || mine != 1.0f || durability != 1.0f) {
            list.add(index++, Text.translatable(ELEMENT_BUFFS, elements.size()).formatted(Formatting.GRAY));
            if (damage != 1.0f) {
                list.add(index++, Text.translatable(ELEMENT_DAMAGE, round(damage)).formatted(Formatting.DARK_GREEN));
            }
            if (maxHealth != 1.0f && (stack.getItem() instanceof Equipment || stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof Equipment)) {
                list.add(index++, Text.translatable(ELEMENT_MAX_HEALTH, round(maxHealth)).formatted(Formatting.BLUE));
            }
            if (mine != 1.0f) {
                list.add(index++, Text.translatable(ELEMENT_MINING_SPEED, round(mine)).formatted(Formatting.RED));
            }
            if (durability != 1.0f) {
                list.add(index++, Text.translatable(ELEMENT_DURABILITY, round(durability)).formatted(Formatting.WHITE));
            }
        }
        if (stack.hasEnchantments()) {
            list.add(index, Text.literal("------------").formatted(Formatting.GRAY));
        }
    }

    private static String round(float damage) {
        return String.format("%.2f", damage);
    }

    @Override
    public float getDamageMultiplier(LivingEntity entity) {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getDamageMultiplier(entity.world, entity, null);
            multiplier += entry.getLevelMultiplier(f);
        }
        return multiplier;
    }

    @Override
    public float getMaxHealthMultiplier(LivingEntity entity) {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getMaxHealthMultiplier(entity.world, entity);
            multiplier += entry.getLevelMultiplier(f);
        }
        return multiplier;
    }

    @Override
    public float getMiningSpeedMultiplier(LivingEntity entity) {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getMiningSpeedMultiplier(entity.world, entity, entity.getSteppingBlockState());
            multiplier += entry.getLevelMultiplier(f);
        }
        return multiplier;
    }

    @Override
    public float getDurabilityMultiplier() {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getDurabilityMultiplier();
            multiplier += entry.getLevelMultiplier(f);
        }
        return multiplier;
    }

    //攻击后执行
    @Inject(method = "postHit", at = @At("TAIL"))
    public void postHit(LivingEntity target, PlayerEntity attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            element.postHit(target, attacker);
        }
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            if (attacker.getRandom().nextFloat() < element.getEffectChance(stack)) {
                element.addEffect(target, attacker);
            }
        }
    }

    @Inject(method = "postMine", at = @At("TAIL"))
    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            element.postMine(world, state, pos, miner);
        }
    }

    @Inject(method = "useOnBlock", at = @At("TAIL"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            element.useOnBlock(context);
        }
    }

    @Inject(method = "useOnEntity", at = @At("TAIL"))
    public void useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            element.useOnEntity(user, entity, hand);
        }
    }

    @Inject(method = "usageTick", at = @At("TAIL"))
    public void usageTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            element.usageTick(world, user, remainingUseTicks);
        }
    }

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element.Entry entry : stack.getElements()) {
            Element element = entry.element();
            if (element.isInvalid()) {
                continue;
            }
            float f = element.getDurabilityMultiplier();
            multiplier += entry.getLevelMultiplier(f);
        }
        int value = (int) (cir.getReturnValueI() * multiplier);
        cir.setReturnValue(value);
    }

    @Override
    public void addRandomElements() {
        ItemStack stack = (ItemStack) (Object) this;
        boolean success = addRandomElement(stack);
        Random random = Random.create();
        float chance = 0.5f;
        while (random.nextFloat() < chance && success) {
            success = addRandomElement(stack);
            chance /= 2;
        }
    }

    @Override
    public boolean addNewRandomElement() {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.addElement(Element.Entry.createRandom(element -> !element.isIn(Element.Entry.getElementSet(stack.getElements())) && element.isSuitableFor(stack) || stack.isOf(EWItems.ELEMENT_BOOK) && !element.isInvalid(), element -> element.getWeight(stack)));
    }

    private static boolean addRandomElement(ItemStack stack) {
        for (int i = 0; i < Element.getRegistrySet(true).size(); i++) {
            Element.Entry entry = Element.Entry.createRandom(element1 -> element1.isSuitableFor(stack) || stack.isOf(EWItems.ELEMENT_BOOK) && !element1.isInvalid(), element -> element.getWeight(stack));
            if (stack.addElement(entry)) {
                return true;
            } else {
                int size = Element.getSizeOf(element -> element.rareLevel == entry.element().rareLevel);
                if (stack.getElements().size() >= size) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasAllElements() {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.getElements().size() >= Element.getSizeOf(element -> element.isSuitableFor(stack) && !element.isInvalid());
    }
}
