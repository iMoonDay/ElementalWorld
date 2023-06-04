package com.imoonday.elemworld.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    public int getFrozenTicks(ClientPlayerEntity instance) {
        return instance.isInFreeze() ? 20 : instance.getFrozenTicks();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFreezingScale()F"))
    public float getFreezingScale(ClientPlayerEntity instance) {
        return instance.isInFreeze() ? 1.0f : instance.getFreezingScale();
    }
}
