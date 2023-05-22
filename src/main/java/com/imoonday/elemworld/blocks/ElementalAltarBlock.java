package com.imoonday.elemworld.blocks;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.blocks.entities.ElementalAltarBlockEntity;
import com.imoonday.elemworld.init.EWBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElementalAltarBlock extends BlockWithEntity {

    public static final Property<Direction> FACING = HorizontalFacingBlock.FACING;

    public ElementalAltarBlock() {
        super(FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
        return VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.75f, 0.9375f);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElementalAltarBlockEntity entity) {
            if (entity.getMaterial() != null && entity.getMaterial().getCount() >= entity.getMaterial().getMaxCount()) {
                return ActionResult.CONSUME;
            }
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            if (entity.getMaterial() != null && entity.getMaterial().isItemEqual(stack)) {
                entity.addCount(1);
            } else {
                entity.setMaterial(stack.copyWithCount(1));
            }
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Material")) {
            return;
        }
        ItemStack material = ItemStack.fromNbt(itemStack.getNbt().getCompound("Material"));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElementalAltarBlockEntity entity) {
            entity.setMaterial(material);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ElementalAltarBlockEntity entity)) {
            return;
        }
        ItemStack material = entity.getMaterial();
        ItemStack drop = new ItemStack(this.asItem());
        drop.getOrCreateNbt().put("Material", material.writeNbt(new NbtCompound()));
        Vec3d centerPos = pos.toCenterPos();
        ItemEntity itemEntity = new ItemEntity(world, centerPos.getX(), centerPos.getY(), centerPos.getZ(), drop);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if (world.isClient) {
            return;
        }
        if (!world.isReceivingRedstonePower(pos)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ElementalAltarBlockEntity altarBlockEntity)) {
            return;
        }
        if (altarBlockEntity.getMaterial() == null || altarBlockEntity.getMaterial().isEmpty()) {
            return;
        }
        int count = altarBlockEntity.getMaterial().getCount();
        altarBlockEntity.setMaterial(ItemStack.EMPTY);
        if (world.random.nextInt(64) >= count) {
            return;
        }
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        Element.Entry entry = Element.Entry.createRandom(element -> !element.isInvalid() && !element.isIn(Element.Entry.getElementSet(living.getElements())), Element::getWeight);
        boolean success = living.addElement(entry) && !entry.element().isInvalid();
        if (!success) {
            return;
        }
        if (living instanceof PlayerEntity player) {
            player.sendMessage(Text.literal("获得一个新元素: ").append(Text.translatable(entry.element().getTranslationKey())));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
