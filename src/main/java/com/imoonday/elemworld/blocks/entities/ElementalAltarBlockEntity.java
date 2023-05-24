package com.imoonday.elemworld.blocks.entities;

import com.imoonday.elemworld.init.EWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElementalAltarBlockEntity extends BlockEntity {

    private ItemStack material = ItemStack.EMPTY;
    private int availableTimes = 3;

    public ElementalAltarBlockEntity(BlockPos pos, BlockState state) {
        super(EWBlocks.ELEMENTAL_ALTAR_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ElementalAltarBlockEntity blockEntity) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        if (blockEntity.availableTimes == 0 || blockEntity.availableTimes < -1) {
            world.breakBlock(pos, false);
        }
    }

    public ItemStack getMaterial() {
        return material;
    }

    public void setMaterial(ItemStack material) {
        this.material = material;
    }

    public int getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(int availableTimes) {
        this.availableTimes = availableTimes;
    }

    public void addAvailableTimes(int count) {
        this.availableTimes = Math.max(this.availableTimes + count, 0);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (material != null) {
            nbt.put("Material", material.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Material")) {
            this.material = ItemStack.fromNbt(nbt.getCompound("Material"));
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Environment(value = EnvType.CLIENT)
    public static class Renderer implements BlockEntityRenderer<ElementalAltarBlockEntity> {

        private final ItemRenderer itemRenderer;

        public Renderer(BlockEntityRendererFactory.Context ctx) {
            itemRenderer = ctx.getItemRenderer();
        }

        @Override
        public void render(ElementalAltarBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
            if (blockEntity.world == null) {
                return;
            }
            ItemStack stack = blockEntity.getMaterial();
            if (stack.isEmpty()) {
                return;
            }
            matrices.push();
            double offset = Math.sin((blockEntity.world.getTime() + tickDelta) / 8.0) / 32.0;
            matrices.translate(0.5, 1.5 + offset, 0.5);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.world.getTime() + tickDelta) * 4));
            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.world, 0);
            matrices.pop();
        }
    }
}
