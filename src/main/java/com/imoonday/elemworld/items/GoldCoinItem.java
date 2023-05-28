package com.imoonday.elemworld.items;

import com.imoonday.elemworld.init.EWTranslationKeys;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoldCoinItem extends Item {
    public GoldCoinItem() {
        super(new FabricItemSettings().rarity(Rarity.RARE));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable(EWTranslationKeys.GOLD_COIN_TOOLTIP).formatted(Formatting.GOLD));
    }
}
