package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.World;

import java.awt.*;

public class WoodElement extends Element {
    public WoodElement() {
        super(3, 1, 40, -0.25f, -0.25f, 0.0f, 0.25f);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        if (state.isIn(BlockTags.LOGS)) {
            return 0.5f;
        }
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public Color getColor() {
        return Color.DARK_GRAY;
    }
}
