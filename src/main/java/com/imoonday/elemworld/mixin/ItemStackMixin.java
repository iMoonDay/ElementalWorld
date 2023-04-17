package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWItemStack;
import com.imoonday.elemworld.api.Element;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EWItemStack {

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
    public boolean addElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.getElements().contains(element)) {
            List<Integer> elements = Arrays.stream(stack.getOrCreateNbt().getIntArray(ELEMENTS_KEY)).boxed().collect(Collectors.toList());
            elements.add(element.getId());
            stack.getOrCreateNbt().putIntArray(ELEMENTS_KEY, elements);
            return true;
        }
        return false;
    }

    @Inject(method = "onCraft", at = @At("TAIL"))
    public void onCraft(World world, PlayerEntity player, int amount, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageable()) {
            addRandomElements(stack);
        }
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageable() && stack.getElements().size() == 0) {
            addRandomElements(stack);
        }
        if (stack.getElements().size() > 1 && stack.getElements().contains(Element.INVALID)) {
            ArrayList<Element> elements = new ArrayList<>(stack.getElements());
            elements.remove(Element.INVALID);
            stack.setElements(elements);
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void elementsTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getElements().size() == 0) {
            return;
        }
        MutableText text = Element.getElementsText(stack.getElements());
        if (text != null) {
            List<Text> list = cir.getReturnValue();
            list.add(1, text);
            cir.setReturnValue(list);
        }
    }

    @Inject(method = "postHit", at = @At("TAIL"))
    public void postHit(LivingEntity target, PlayerEntity attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            target.addStatusEffect(new StatusEffectInstance(Element.getEffect(element), 5 * 20, 0), attacker);
        }
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

    private static void addRandomElements(ItemStack stack) {
        addRandomElement(stack);
        if (Random.create().nextFloat() < 0.5f) {
            addRandomElement(stack);
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
            stack.addElement(Element.INVALID);
        }
    }

    private static void addRandomElement(ItemStack stack, int level) {
        while (true) {
            boolean success = stack.addElement(Element.createRandom(level));
            if (success) break;
        }
    }
}
