package com.imoonday.elemworld.items;

import com.imoonday.elemworld.api.Element;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ElementFragmentItem extends Item {


    private final int rareLevel;

    public ElementFragmentItem(int rareLevel) {
        super(new FabricItemSettings().maxCount(16));
        this.rareLevel = rareLevel;
    }

    @Override
    public Text getName() {
        Formatting formatting = Element.getFormatting(this.rareLevel);
        Text name = super.getName();
        if (formatting == null) {
            return name;
        }
        return name.copy().formatted(formatting);
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }
}
