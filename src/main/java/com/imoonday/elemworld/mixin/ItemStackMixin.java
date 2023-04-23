package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWItemStack;
import com.imoonday.elemworld.api.Element;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.init.EWElements.EMPTY;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EWItemStack {

    /**
     * @see #postHit(LivingEntity, PlayerEntity, CallbackInfo)
     * 击中后方法
     **/

    private static final String ELEMENTS_KEY = "Elements";

    @NotNull
    @Override
    public ArrayList<Element> getElements() {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.hasNbt()) {
            return new ArrayList<>();
        }
        return stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.STRING_TYPE).stream().map(nbtElement -> Element.byName(nbtElement.asString())).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void setElements(ArrayList<Element> elements) {
        ItemStack stack = (ItemStack) (Object) this;
        NbtList list = new NbtList();
        List<NbtString> nbtStrings = new ArrayList<>();
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            NbtString string = NbtString.of(element.getName());
            nbtStrings.add(string);
        }
        list.addAll(nbtStrings);
        stack.getOrCreateNbt().put(ELEMENTS_KEY, list);
    }

    @Override
    public boolean hasElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.getElements().contains(element);
    }

    @Override
    public boolean addElement(Element element) {
        ItemStack stack = (ItemStack) (Object) this;
        if (element == null || element == EMPTY && stack.getElements().size() >= 1) {
            return false;
        }
        if (!stack.hasElement(element)) {
            NbtList list = stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.STRING_TYPE);
            list.add(NbtString.of(element.getName()));
            stack.getOrCreateNbt().put(ELEMENTS_KEY, list);
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
        if (stack.getElements().size() > 1 && stack.hasElement(EMPTY)) {
            ArrayList<Element> elements = new ArrayList<>(stack.getElements());
            elements.remove(EMPTY);
            stack.setElements(elements);
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void elementsTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getElements().size() == 0) {
            return;
        }
        Text text = Element.getElementsText(stack.getElements(), false);
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
            element.addEffect(target, attacker);
            element.postHit(target, attacker);
        }
    }

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        float multiplier = 1.0f;
        for (Element element : stack.getElements()) {
            if (element == null) {
                continue;
            }
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
            stack.addElement(EMPTY);
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
            boolean success = stack.addElement(Element.createRandom(level));
            int maxSize = level == 0 ? 1 : 5;
            List<Element> list = new ArrayList<>();
            for (Element element : stack.getElements()) {
                if (element == null) {
                    continue;
                }
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
