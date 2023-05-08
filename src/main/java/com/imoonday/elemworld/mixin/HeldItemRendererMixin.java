package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.init.EWItems;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Redirect(method = "getUsingItemHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean isOf$1(ItemStack instance, Item item) {
        return instance.isOf(EWItems.ELEMENT_BOW) || instance.isOf(Items.BOW) || instance.isOf(Items.CROSSBOW);
    }

    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean isOf$2(ItemStack instance, Item item) {
        return item == Items.BOW ? instance.isOf(item) || instance.isOf(EWItems.ELEMENT_BOW) : instance.isOf(item);
    }
}
