package com.imoonday.elemworld.mixin;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow
    public Camera camera;

    private static final SpriteIdentifier ICE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/ice"));

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && living.isInFreeze()) {
            renderIce(matrices, vertexConsumers, entity);
        }
    }

    private void renderIce(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
        Sprite sprite = ICE.getSprite();
        matrices.push();
        float f = entity.getWidth();
        matrices.scale(f, f, f);
        float g = 0.5f;
        float i = entity.getHeight() / f;
        float j = 0.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-this.camera.getYaw()));
        matrices.translate(0.0f, 0.0f, -0.3f + (float) ((int) i) * 0.02f);
        float k = 0.0f;
        int l = 0;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
        MatrixStack.Entry entry = matrices.peek();
        while (i > 0.0f) {
            float m = sprite.getMinU();
            float n = sprite.getMinV();
            float o = sprite.getMaxU();
            float p = sprite.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }
            drawIceVertex(entry, vertexConsumer, g - 0.0f, 0.0f - j, k, o, p);
            drawIceVertex(entry, vertexConsumer, -g - 0.0f, 0.0f - j, k, m, p);
            drawIceVertex(entry, vertexConsumer, -g - 0.0f, 1.4f - j, k, m, n);
            drawIceVertex(entry, vertexConsumer, g - 0.0f, 1.4f - j, k, o, n);
            i -= 0.45f;
            j -= 0.45f;
            g *= 0.9f;
            k += 0.03f;
            ++l;
        }
        matrices.pop();
    }

    private static void drawIceVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(entry.getPositionMatrix(), x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(0, 10).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(entry.getNormalMatrix(), 0.0f, 1.0f, 0.0f).next();
    }
}
