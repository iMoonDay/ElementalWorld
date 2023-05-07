package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.ElementEntry;
import com.imoonday.elemworld.init.EWItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntityMixin {

    private static final TrackedData<ItemStack> ELEMENTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        player.getDataTracker().startTracking(ELEMENTS, new ItemStack(Items.PLAYER_HEAD).withElements(elements));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.world.isClient) {
            return;
        }
        player.getDataTracker().set(ELEMENTS, new ItemStack(Items.PLAYER_HEAD).withElements(elements));
    }

    @Override
    public Set<ElementEntry> getElements() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return player.getDataTracker().get(ELEMENTS).getElements();
    }

    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult interact(Entity entry, PlayerEntity player, Hand hand) {
        return player.getStackInHand(hand).isOf(EWItems.ELEMENT_DETECTOR) ? ActionResult.PASS : entry.interact(player, hand);
    }

    @Inject(method = "onDeath",at = @At("TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci){

    }
}
