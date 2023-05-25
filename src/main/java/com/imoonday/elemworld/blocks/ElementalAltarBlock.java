package com.imoonday.elemworld.blocks;

import com.imoonday.elemworld.blocks.entities.ElementalAltarBlockEntity;
import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWBlocks;
import com.imoonday.elemworld.init.EWIdentifiers;
import com.imoonday.elemworld.init.EWItems;
import com.imoonday.elemworld.init.EWTags;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.imoonday.elemworld.init.EWTranslationKeys.GET_NEW_ELEMENT;
import static com.imoonday.elemworld.init.EWTranslationKeys.LEVEL_LESS_THAN;

public class ElementalAltarBlock extends BlockWithEntity {

    public ElementalAltarBlock() {
        super(FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE).nonOpaque());
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 1.35, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.5, 0, -0.5, 1.5, 0.4 / 3, 1.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.375, 0, -0.375, 1.375, 0.4 / 3 * 2, 1.375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.25, 0, -0.25, 1.25, 0.4, 1.25));
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND) {
            return ActionResult.PASS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ElementalAltarBlockEntity altar)) {
            return ActionResult.FAIL;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack material = altar.getMaterial().copy();
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isIn(EWTags.COMBINED_ELEMENT_FRAGMENTS)) {
            ActionResult result = tryAddElement(world, pos, player, altar, material, stack);
            if (result != null) return result;
        } else if (stack.hasSuitableElement() || stack.isEmpty()) {
            replaceMaterial(player, world, pos, hand, altar, material, stack);
        }
        return ActionResult.CONSUME;
    }

    protected void replaceMaterial(PlayerEntity player, World world, BlockPos pos, Hand hand, ElementalAltarBlockEntity altar, ItemStack material, ItemStack stack) {
        if (material == null || material.isEmpty()) {
            if (!stack.isEmpty()) {
                altar.setMaterial(stack.copy());
                player.setStackInHand(hand, ItemStack.EMPTY);
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.VOICE);
            }
        } else {
            altar.setMaterial(stack.copy());
            player.setStackInHand(hand, material);
            world.playSound(null, pos, stack.isEmpty() ? SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM : SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, SoundCategory.VOICE);
        }
    }

    @Nullable
    protected ActionResult tryAddElement(World world, BlockPos pos, PlayerEntity player, ElementalAltarBlockEntity altar, ItemStack material, ItemStack stack) {
        if (material != null && !material.isEmpty()) {
            if (material.hasAllElements()) {
                return ActionResult.CONSUME;
            }
            boolean success;
            Element.Entry entry = Element.Entry.createRandom(element -> element.isSuitableFor(material), Element::getWeight);
            int rareLevel = entry.element().rareLevel;
            if (stack.isOf(EWItems.BASE_ELEMENT_FRAGMENT)) {
                ActionResult result = checkLevel(player, 3);
                if (result != null) return result;
                success = rareLevel <= 1 && material.addElement(entry);
            } else if (stack.isOf(EWItems.ADVANCED_ELEMENT_FRAGMENT)) {
                ActionResult result = checkLevel(player, 5);
                if (result != null) return result;
                success = rareLevel <= 2 && material.addElement(entry);
            } else {
                ActionResult result = checkLevel(player, 10);
                if (result != null) return result;
                success = stack.isOf(EWItems.RARE_ELEMENT_FRAGMENT) ? rareLevel <= 3 && material.addElement(entry) : material.addElement(entry);
            }
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            if (success) {
                altar.setMaterial(material);
                player.sendMessage(Text.translatable(GET_NEW_ELEMENT, material.getName(), entry.getName()), true);
                world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
                if (altar.getAvailableTimes() != -1 && !player.isCreative()) {
                    altar.addAvailableTimes(-rareLevel);
                }
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS);
            }
        }
        return null;
    }

    @Nullable
    protected ActionResult checkLevel(PlayerEntity player, int requiredLevel) {
        if (!player.isCreative()) {
            if (player.experienceLevel < requiredLevel) {
                player.sendMessage(Text.translatable(LEVEL_LESS_THAN, requiredLevel), true);
                return ActionResult.CONSUME;
            }
            changeLevel(player, requiredLevel);
        }
        return null;
    }

    /**
     * <p>Only run on server side
     */
    protected void changeLevel(PlayerEntity player, int requiredLevel) {
        player.experienceLevel -= requiredLevel;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(-requiredLevel);
        ServerPlayNetworking.send((ServerPlayerEntity) player, EWIdentifiers.CHANGE_LEVEL, buf);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ElementalAltarBlockEntity entity) {
                ItemStack material = entity.getMaterial();
                if (material != null && !material.isEmpty()) {
                    dropStack(world, pos, material);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
