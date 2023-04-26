package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWItemStack;
import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWElements;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.imoonday.elemworld.init.EWElements.EMPTY;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EWItemStack {

    private static final String ELEMENTS_KEY = "Elements";

    @NotNull
    @Override
    public ArrayList<Element> getElements() {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.hasNbt()) {
            return new ArrayList<>();
        }
        ArrayList<Element> elements = new ArrayList<>();
        for (NbtElement nbtElement : stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbt = (NbtCompound) nbtElement;
            Element element = Element.fromNbt(nbt);
            elements.add(element);
        }
        return elements;
    }

    @Override
    public void setElements(ArrayList<Element> elements) {
        ItemStack stack = (ItemStack) (Object) this;
        NbtList list = new NbtList();
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            if (!element.isSuitableFor(stack)) {
                continue;
            }
            NbtCompound nbt = element.toNbt();
            list.add(nbt);
        }
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
        if (!element.isSuitableFor(stack)) {
            return false;
        }
        NbtList list = stack.getOrCreateNbt().getList(ELEMENTS_KEY, NbtElement.COMPOUND_TYPE);
        if (!stack.hasElement(element)) {
            list.add(element.toNbt());
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
        if (hasSuitableElement()) {
            stack.addRandomElements();
        }
    }

    @Override
    public boolean hasSuitableElement() {
        ItemStack stack = (ItemStack) (Object) this;
        return Element.getRegistrySet()
                .stream().anyMatch(element -> element.isSuitableFor(stack));
    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (hasSuitableElement() && stack.getElements().size() == 0) {
            stack.addRandomElements();
        }
        if (stack.getElements().size() > 1 && stack.hasElement(EMPTY)) {
            ArrayList<Element> elements = new ArrayList<>(stack.getElements());
            elements.remove(EMPTY);
            stack.setElements(elements);
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void elementsTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getElements().size() == 0) {
            return;
        }
        List<Text> texts = Element.getElementsText(stack.getElements(), false, true);
        if (!texts.isEmpty() && player != null) {
            int index = 1;
            for (Text text : texts) {
                list.add(index++, text);
            }
            float damage = 1.0f;
            float protect = 1.0f;
            float mine = 1.0f;
            for (Element element : stack.getElements()) {
                if (element.getMaxLevel() == 0.0f) {
                    continue;
                }
                float f = element.getDamageMultiplier(player.world, player, null) - 1;
                damage += element.getLevelMultiplier(f);
                f = element.getProtectionMultiplier(player.world, player) - 1;
                protect += element.getLevelMultiplier(f);
                f = element.getMiningSpeedMultiplier(player.world, player, player.getSteppingBlockState()) - 1;
                mine += element.getLevelMultiplier(f);
            }
            if (damage != 1.0f || protect != 1.0f || mine != 1.0f) {
                list.add(index++, Text.literal("元素增幅：").formatted(Formatting.GRAY));
                if (damage != 1.0f) {
                    list.add(index++, Text.literal("×" + String.format("%.2f", damage) + " 攻击伤害").formatted(Formatting.DARK_GREEN));
                }
                if (protect != 1.0f) {
                    list.add(index++, Text.literal("×" + String.format("%.2f", protect) + " 护甲值").formatted(Formatting.BLUE));
                }
                if (mine != 1.0f) {
                    list.add(index, Text.literal("×" + String.format("%.2f", mine) + " 挖掘速度").formatted(Formatting.RED));
                }
            }
        }
    }

    //攻击后执行
    @Inject(method = "postHit", at = @At("TAIL"))
    public void postHit(LivingEntity target, PlayerEntity attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            if (element == null) {
                continue;
            }
            if (element.isOf(EMPTY)) {
                continue;
            }
            element.addEffect(target, attacker);
            element.postHit(target, attacker);
        }
    }

    @Inject(method = "postMine", at = @At("TAIL"))
    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            element.postMine(world, state, pos, miner);
        }
    }

    @Inject(method = "useOnBlock", at = @At("TAIL"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            element.useOnBlock(context);
        }
    }

    @Inject(method = "useOnEntity", at = @At("TAIL"))
    public void useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            element.useOnEntity(user, entity, hand);
        }
    }

    @Inject(method = "usageTick", at = @At("TAIL"))
    public void usageTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        for (Element element : stack.getElements()) {
            element.usageTick(world, user, remainingUseTicks);
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
        boolean success = addRandomElement(stack);
        Random random = Random.create();
        float chance = 0.5f;
        while (random.nextFloat() < chance && success) {
            success = addRandomElement(stack);
            chance /= 2;
        }
    }

    @Override
    public void addNewRandomElement() {
        ItemStack stack = (ItemStack) (Object) this;
        stack.addElement(Element.createRandom(stack, stack.getElements()));
    }

    private static boolean addRandomElement(ItemStack stack) {
        for (int i = 0; i < Element.getRegistryMap().size(); i++) {
            Element element = Element.createRandom(stack);
            boolean success = stack.addElement(element);
            int maxSize = Element.getRegistrySet()
                    .stream().filter(element1 -> element1.getRareLevel() == element.getRareLevel())
                    .toList().size();
            boolean full = stack.getElements()
                    .stream().filter(Objects::nonNull)
                    .toList().size() >= maxSize;
            if (success) {
                return true;
            } else if (full) {
                return false;
            }
        }
        return false;
    }
}
