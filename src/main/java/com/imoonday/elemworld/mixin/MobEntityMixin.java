package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.effects.DizzyEffect;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Inject(method = "canMoveVoluntarily", at = @At("RETURN"), cancellable = true)
    public void canMoveVoluntarily(CallbackInfoReturnable<Boolean> cir) {
        MobEntity entity = (MobEntity) (Object) this;
        if (entity.hasStatusEffect(EWEffects.FREEZE)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAffectedByDaylight", at = @At("HEAD"), cancellable = true)
    public void isAffectedByDaylight(CallbackInfoReturnable<Boolean> cir) {
        MobEntity entity = (MobEntity) (Object) this;
        if (entity.hasElement(EWElements.LIGHT)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityType<? extends MobEntity> entityType, World world, CallbackInfo ci) {
        MobEntity entity = (MobEntity) (Object) this;
        if (world != null && !world.isClient) {
            this.goalSelector.add(0, new DizzyEffect.DizzyGoal(entity));
        }
    }
}
