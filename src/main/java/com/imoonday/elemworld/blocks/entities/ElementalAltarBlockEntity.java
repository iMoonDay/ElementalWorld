package com.imoonday.elemworld.blocks.entities;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementalAltarBlockEntity extends BlockEntity {

    private ItemStack material = ItemStack.EMPTY;

    public ElementalAltarBlockEntity(BlockPos pos, BlockState state) {
        super(EWBlocks.ELEMENTAL_ALTAR_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ElementalAltarBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        if (!world.isReceivingRedstonePower(pos)) {
            return;
        }
        if (blockEntity.material == null || blockEntity.material.isEmpty()) {
            return;
        }
        if (world.random.nextInt(64) >= blockEntity.material.getCount()) {
            return;
        }
        PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 2, false);
        if (player == null || !player.getSteppingPos().equals(pos)) {
            return;
        }
        player.addElement(Element.Entry.createRandom(element -> !element.isInvalid() && !element.isIn(Element.Entry.getElementSet(player.getElements())), Element::getWeight));
        blockEntity.material = ItemStack.EMPTY;
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
    }

    public ItemStack getMaterial() {
        return material;
    }

    @Environment(value = EnvType.CLIENT)
    public static class Renderer implements BlockEntityRenderer<ElementalAltarBlockEntity> {

        private final ModelPart main;
        private final ItemRenderer itemRenderer;

        public Renderer(BlockEntityRendererFactory.Context ctx) {
            ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.PLAYER);
            main = modelPart.getChild("main");
            itemRenderer = ctx.getItemRenderer();
        }

        @Override
        public void render(ElementalAltarBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
            ItemStack stack = blockEntity.getMaterial();
            if (stack.isEmpty()) {
                return;
            }
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.scale(0.5F, 0.5F, 0.5F);
            itemRenderer.renderInGui(matrices, stack, 0, 0);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(blockEntity.getCachedState(), true));
            main.render(matrices, vertexConsumer, light, overlay);
        }
    }
}
