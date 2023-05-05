package com.imoonday.elemworld.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Item.class)
public class ItemMixin {

    /**
     * @author iMoonDay
     * @reason Modify maxDamage
     */
    @Overwrite
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13.0f - (float) stack.getDamage() * 13.0f / (float) stack.getMaxDamage());
    }

    /**
     * @author iMoonDay
     * @reason Modify maxDamage
     */
    @Overwrite
    public int getItemBarColor(ItemStack stack) {
        float f = Math.max(0.0f, ((float) stack.getMaxDamage() - (float) stack.getDamage()) / (float) stack.getMaxDamage());
        return MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f);
    }
}
