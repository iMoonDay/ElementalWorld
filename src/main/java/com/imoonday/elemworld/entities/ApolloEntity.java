package com.imoonday.elemworld.entities;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.awt.*;

public class ApolloEntity extends SkeletonEntity implements BaseElement {
    public ApolloEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.LIGHT;
    }

    public static EntityType<ApolloEntity> register() {
        return EWEntities.registerWithoutRenderer("apollo",
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ApolloEntity::new)
                        .dimensions(EntityDimensions.fixed(0.75f, 1.9f)).build(),
                "Apollo",
                "阿波罗",
                SkeletonEntity.createAbstractSkeletonAttributes(),
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ApolloEntity::cannotSpawnInDark,
                BiomeSelectors.spawnsOneOf(EntityType.SKELETON),
                SpawnGroup.MONSTER,
                80,
                1,
                3,
                Color.YELLOW.getRGB(),
                Color.WHITE.getRGB());
    }

    public static boolean cannotSpawnInDark(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && !HostileEntity.isSpawnDark(world, pos, random) && HostileEntity.canMobSpawn(type, world, spawnReason, pos, random);
    }
}