package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;

import static net.minecraft.registry.tag.DamageTypeTags.IS_FALL;

public class GrassElement extends Element {
    public GrassElement(int maxLevel, int rareLevel, int weight) {
        super(maxLevel, rareLevel, weight);
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        BlockState state = entity.getSteppingBlockState();
        return source.isIn(IS_FALL) && (state.isOf(Blocks.GRASS_BLOCK) || state.isIn(BlockTags.LEAVES)) ? 0.2f : 1.0f;
    }

    @Override
    public void tick(LivingEntity entity) {
        if (entity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
            return;
        }
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.setHealTick(entity.getHealTick() + 1);
            int healTick = entity.getHealTick();
            boolean onGrass = entity.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK);
            if (onGrass && healTick >= 5 * 20 || healTick >= 10 * 20) {
                entity.heal(1);
                entity.setHealTick(0);
                if (onGrass) {
                    entity.world.setBlockState(entity.getSteppingPos(), Blocks.DIRT.getDefaultState());
                }
            }
        }
    }
}
