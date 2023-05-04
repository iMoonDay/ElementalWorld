package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.init.EWItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.imoonday.elemworld.init.EWElements.SPACE;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntityMixin {

    private static final TrackedData<Boolean> HAS_SPACE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        player.getDataTracker().startTracking(HAS_SPACE, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.world.isClient) {
            return;
        }
        player.getDataTracker().set(HAS_SPACE, player.hasElement(SPACE));
    }

    @Override
    public boolean hasSpace() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return player.getDataTracker().get(HAS_SPACE);
    }

    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult interact(Entity instance, PlayerEntity player, Hand hand) {
        return player.getStackInHand(hand).isOf(EWItems.ELEMENT_DETECTOR) ? ActionResult.PASS : instance.interact(player, hand);
    }
}
