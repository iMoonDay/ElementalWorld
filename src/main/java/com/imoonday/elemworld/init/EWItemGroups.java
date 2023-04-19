package com.imoonday.elemworld.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static com.imoonday.elemworld.ElementalWorld.id;

public class EWItemGroups {

    public static final ItemGroup ELEMENTAL_WORLD = FabricItemGroup.builder(id("elemental_world"))
            .displayName(Text.translatable("group.elemworld.name"))
            .icon(() -> new ItemStack(EWBlocks.ELEMENT_MODIFIER_BLOCK)).build();

    public static void register(){}
}
