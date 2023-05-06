package com.imoonday.elemworld.items;

import com.imoonday.elemworld.api.ElementEntry;
import com.imoonday.elemworld.init.EWItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ElementBookItem extends Item {

    public static final String STORED_ELEMENTS_KEY = "StoredElements";

    public ElementBookItem() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtList nbtList = getElementsNbt(stack);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            ElementEntry.fromNbt(nbtCompound).ifPresent(e -> tooltip.add(e.getName()));
        }
    }

    public static NbtList getElementsNbt(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            stack.getOrCreateNbt().put(STORED_ELEMENTS_KEY, new NbtList());
        }
        return stack.getOrCreateNbt().getList(STORED_ELEMENTS_KEY, NbtElement.COMPOUND_TYPE);
    }

    public static Set<ElementEntry> getElements(ItemStack stack) {
        Set<ElementEntry> entries = new HashSet<>();
        NbtList nbtList = getElementsNbt(stack);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            ElementEntry.fromNbt(nbtCompound).ifPresent(entries::add);
        }
        return entries;
    }

    public static boolean addElement(ItemStack stack, ElementEntry entry) {
        NbtList nbtList = getElementsNbt(stack);
        boolean noneMatch = true;
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Optional<ElementEntry> optional = ElementEntry.fromNbt(nbtCompound);
            if (optional.isEmpty() || !optional.get().isElementEqual(entry)) {
                continue;
            }
            if (optional.get().level() < entry.level()) {
                nbtCompound.putInt(ElementEntry.LEVEL_KEY, entry.level());
            } else {
                return false;
            }
            noneMatch = false;
            break;
        }
        if (noneMatch) {
            nbtList.add(entry.toNbt());
        }
        stack.getOrCreateNbt().put(STORED_ELEMENTS_KEY, nbtList);
        return true;
    }

    public static ItemStack fromElement(ElementEntry entry) {
        ItemStack stack = new ItemStack(EWItems.ELEMENT_BOOK);
        addElement(stack, entry);
        return stack;
    }
}
