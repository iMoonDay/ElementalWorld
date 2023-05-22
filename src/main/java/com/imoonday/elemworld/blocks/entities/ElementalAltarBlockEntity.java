package com.imoonday.elemworld.blocks.entities;

import com.imoonday.elemworld.blocks.ElementalAltarBlock;
import com.imoonday.elemworld.init.EWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
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

    public ElementalAltarBlockEntity(BlockPos pos, BlockState state) {
        super(EWBlocks.ELEMENTAL_ALTAR_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ElementalAltarBlockEntity blockEntity) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }

    public ItemStack getMaterial() {
        return material;
    }

    public void setMaterial(ItemStack material) {
        this.material = material;
    }

    public void addCount(int count) {
        this.material.setCount(this.material.getCount() + count);
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
        private final TextRenderer textRenderer;

        public Renderer(BlockEntityRendererFactory.Context ctx) {
            itemRenderer = ctx.getItemRenderer();
            textRenderer = ctx.getTextRenderer();
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
            matrices.translate(0.5, 0.57, 0.5);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.multiply(blockEntity.getCachedState().get(ElementalAltarBlock.FACING).getOpposite().getRotationQuaternion());
            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.world, blockEntity.getPos().up());
            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, lightAbove, overlay, matrices, vertexConsumers, blockEntity.world, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.translate(0, 0, -0.18);
            matrices.scale(0.05f, 0.05f, 0.05f);
            textRenderer.draw(matrices, String.valueOf(stack.getCount()), -8, -8, 16777215);
            matrices.pop();
        }
    }
}
