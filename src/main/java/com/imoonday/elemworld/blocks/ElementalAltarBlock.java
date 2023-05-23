package com.imoonday.elemworld.blocks;

import com.imoonday.elemworld.blocks.entities.ElementalAltarBlockEntity;
import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWBlocks;
import com.imoonday.elemworld.init.EWTranslationKeys;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
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

import java.util.List;

public class ElementalAltarBlock extends BlockWithEntity {

    public static final Property<Direction> FACING = HorizontalFacingBlock.FACING;
    private static final String MATERIAL = "Material";

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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        if (stack.getNbt() != null && stack.getNbt().contains(MATERIAL)) {
            ItemStack itemStack = ItemStack.fromNbt(stack.getNbt().getCompound(MATERIAL));
            if (itemStack != null && !itemStack.isEmpty()) {
                tooltip.add(Text.literal("材料: ").append(itemStack.getName()).append(" " + itemStack.getCount()));
            }
        }
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
        if (!(world.getBlockEntity(pos) instanceof ElementalAltarBlockEntity entity)) {
            return ActionResult.FAIL;
        }
        ItemStack stack = player.getMainHandStack();
        ItemStack material = entity.getMaterial();
        if (!stack.isOf(Items.DIAMOND)) {
            if (stack.isEmpty()) {
                if (!world.isClient) {
                    Vec3d centerPos = pos.toCenterPos();
                    if (player.isSneaking()) {
                        ItemEntity itemEntity = new ItemEntity(world, centerPos.getX(), centerPos.getY(), centerPos.getZ(), material);
                        itemEntity.setToDefaultPickupDelay();
                        world.spawnEntity(itemEntity);
                        entity.setMaterial(ItemStack.EMPTY);
                    } else {
                        ItemEntity itemEntity = new ItemEntity(world, centerPos.getX(), centerPos.getY(), centerPos.getZ(), material.copyWithCount(1));
                        itemEntity.setToDefaultPickupDelay();
                        world.spawnEntity(itemEntity);
                        entity.increment(-1);
                    }
                }
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        }
        if (material != null && material.getCount() >= material.getMaxCount()) {
            return ActionResult.FAIL;
        }
        int cost = 5;
        if (!player.isCreative()) {
            if (player.experienceLevel < cost) {
                player.sendMessage(Text.translatable(EWTranslationKeys.LEVEL_LESS_THAN, cost), true);
                return ActionResult.SUCCESS;
            }
        }
        int removedCount;
        if (material != null && material.isItemEqual(stack)) {
            removedCount = stack.getCount() + material.getCount() <= material.getMaxCount() ? stack.getCount() : material.getMaxCount() - material.getCount();
            entity.increment(removedCount);
        } else {
            entity.setMaterial(stack.copy());
            removedCount = stack.getCount();
        }
        if (!player.isCreative()) {
            stack.decrement(removedCount);
            player.experienceLevel -= cost;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (itemStack.getNbt() == null || !itemStack.getNbt().contains(MATERIAL)) {
            return;
        }
        ItemStack material = ItemStack.fromNbt(itemStack.getNbt().getCompound(MATERIAL));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElementalAltarBlockEntity entity) {
            entity.setMaterial(material);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (player.isCreative()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ElementalAltarBlockEntity entity)) {
            return;
        }
        ItemStack material = entity.getMaterial();
        ItemStack drop = new ItemStack(this.asItem());
        drop.getOrCreateNbt().put(MATERIAL, material.writeNbt(new NbtCompound()));
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
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        if (living.hasAllElements()) {
            return;
        }
        if (!(world.getBlockEntity(pos) instanceof ElementalAltarBlockEntity altarBlockEntity)) {
            return;
        }
        if (altarBlockEntity.getMaterial() == null || altarBlockEntity.getMaterial().isEmpty()) {
            return;
        }
        int count = altarBlockEntity.getMaterial().getCount();
        if (count < 8) {
            return;
        }
        if (world.random.nextInt(64) < count) {
            Element.Entry entry = Element.Entry.createRandom(element -> !element.isInvalid() && element.isSuitableFor(living), Element::getWeight);
            if (living.addElement(entry) && !entry.element().isInvalid()) {
                if (living instanceof PlayerEntity player) {
                    player.sendMessage(Text.translatable(EWTranslationKeys.GET_NEW_ELEMENT, Text.translatable(entry.element().getTranslationKey()).formatted(entry.element().getFormatting())));
                }
                world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
                int maxRareLevel = Element.getRegistrySet(false).stream().mapToInt(element -> element.rareLevel).max().orElse(3);
                if (entry.element().rareLevel > world.random.nextInt(maxRareLevel)) {
                    world.breakBlock(pos, false);
                    return;
                }
            }
        }
        altarBlockEntity.increment(-8);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
