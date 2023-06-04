package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.items.staffs.GrassElementalStaffItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Inject(method = "emptyFullComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void spawnGrassElementalStaff(Entity user, BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        GrassElementalStaffItem.spawnGrassElementalStaff(world, pos);
    }
}
