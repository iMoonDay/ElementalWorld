package com.imoonday.elemworld.blocks;

import com.imoonday.elemworld.blocks.entities.ElementalAltarBlockEntity;
import com.imoonday.elemworld.init.EWBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElementalAltarBlock extends BlockWithEntity {

    public ElementalAltarBlock() {
        super(FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE));
    }

    @Nullable
    @Override
    public ElementalAltarBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElementalAltarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, EWBlocks.ELEMENTAL_ALTAR_BLOCK_ENTITY, ElementalAltarBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getMainHandStack();
        if (!stack.isOf(Items.DIAMOND)) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
