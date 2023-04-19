package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.Element;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Redirect(method = "processBlockBreakingAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
    public double distanceTo(Vec3d instance, Vec3d vec) {
        if (instance.squaredDistanceTo(vec) > ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE) {
            ServerPlayerEntity player = this.player;
            if (player.hasElement(Element.SPACE) || player.getMainHandStack().hasElement(Element.SPACE)) {
                double d = Math.sqrt(instance.squaredDistanceTo(vec)) - 3;
                return d * d;
            }
        }
        return instance.squaredDistanceTo(vec);
    }
}
