package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.gui.ElementRendererGui;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static ElementRendererGui gui;

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z", opcode = Opcodes.GETFIELD, args = {"log=false"}))
    private void beforeRenderDebugScreen(MatrixStack stack, float f, CallbackInfo ci) {
        if (gui == null) {
            gui = new ElementRendererGui();
        }
        gui.onRenderGameOverlayPost(stack);
    }
}
