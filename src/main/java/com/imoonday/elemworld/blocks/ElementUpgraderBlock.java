package com.imoonday.elemworld.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ElementUpgraderBlock extends Block {
    public ElementUpgraderBlock() {
        super(FabricBlockSettings.copyOf(Blocks.SMITHING_TABLE));
    }
}
