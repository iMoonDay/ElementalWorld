package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.EWRegister;
import com.imoonday.elemworld.blocks.ElementSmithingTableBlock;
import net.minecraft.block.Block;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWBlocks {

    public static final Block ELEMENT_SMITHING_TABLE = EWRegister.register("element_smithing_table", new ElementSmithingTableBlock());

    public static void register() {
        LOGGER.info("Loading Blocks");
    }
}
