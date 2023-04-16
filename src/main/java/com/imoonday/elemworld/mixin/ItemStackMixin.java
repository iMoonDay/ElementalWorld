package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWItemStack;
import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EWItemStack {

    private ArrayList<Element> elements = new ArrayList<>();

    @Override
    public ArrayList<Element> getElements() {
        return elements;
    }

    @Override
    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    @Override
    public boolean addElement(Element element) {
        if (!this.elements.contains(element)) {
            this.elements.add(element);
            return true;
        }
        return false;
    }

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    public void getName(CallbackInfoReturnable<Text> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        MutableText text = cir.getReturnValue().copy();
        for (Element element : stack.getElements()) {
            text.append(" [" + element.getName() + "]");
        }
        cir.setReturnValue(text);
    }

    @Inject(method = "onCraft", at = @At("TAIL"))
    public void onCraft(World world, PlayerEntity player, int amount, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageable()) {
            addRandomElement(stack);
        }
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageable() && stack.getElements().size() == 0) {
            addRandomElement(stack);
        }
    }

    private static void addRandomElement(ItemStack stack) {
        Random random = Random.create();
        stack.addElement(Element.createRandom(1));
        if (random.nextFloat() < 0.5f) {
            float v = random.nextFloat();
            if (v < 0.1f) {
                addRandomElement(stack, 3);
            } else if (v < 0.3f) {
                addRandomElement(stack, 2);
            } else if (v < 0.75f) {
                addRandomElement(stack, 1);
            }
        }
    }

    private static void addRandomElement(ItemStack stack, int level) {
        while (true) {
            boolean success = stack.addElement(Element.createRandom(level));
            if (success) break;
        }
    }
}
