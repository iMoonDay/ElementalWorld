package com.imoonday.elemworld.mixin;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "getOverlay", at = @At("HEAD"), cancellable = true)
    private static void modifyOverlay(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> cir) {
        int uv = OverlayTexture.packUv(OverlayTexture.getU(entity.isInFreeze() ? 0.5f : whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
        cir.setReturnValue(uv);
    }

    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void isShaking(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.isInFreeze()) {
            cir.setReturnValue(true);
        }
    }
}
