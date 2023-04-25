package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.World;

public class WoodElement extends Element {
    public WoodElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public float getMiningSpeedMultiplier (World world, LivingEntity entity, BlockState state){
        if (state.isIn(BlockTags.LOGS)) {
            return 1.5f;
        }
        return super.getMiningSpeedMultiplier(world, entity, state);
    }
}
