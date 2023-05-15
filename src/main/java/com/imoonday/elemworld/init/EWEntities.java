package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.FireElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.GoldElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.IceElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WindElementalEnergyBallEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.HashMap;
import java.util.Map;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEntities {

    public static final Map<EntityType<? extends AbstractElementalEnergyBallEntity>, String> ENERGY_BALLS = new HashMap<>();

    public static final EntityType<WindElementalEnergyBallEntity> WIND_ELEMENTAL_ENERGY_BALL = registerEnergyBall("wind_elemental_energy_ball", WindElementalEnergyBallEntity::new, "Wind Elemental Energy Ball", "风元素能量球");
    public static final EntityType<FireElementalEnergyBallEntity> FIRE_ELEMENTAL_ENERGY_BALL = registerEnergyBall("fire_elemental_energy_ball", FireElementalEnergyBallEntity::new, "Fire Elemental Energy Ball", "火元素能量球");
    public static final EntityType<IceElementalEnergyBallEntity> ICE_ELEMENTAL_ENERGY_BALL = registerEnergyBall("ice_elemental_energy_ball", IceElementalEnergyBallEntity::new, "Ice Elemental Energy Ball", "冰元素能量球");
    public static final EntityType<GoldElementalEnergyBallEntity> GOLD_ELEMENTAL_ENERGY_BALL = registerEnergyBall("gold_elemental_energy_ball", GoldElementalEnergyBallEntity::new, "Gold Elemental Energy Ball", "金元素能量球");

    public static void register() {
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> type, String en_us, String zh_cn) {
        ElementalWorldData.addTranslation(type, en_us, zh_cn);
        return Registry.register(Registries.ENTITY_TYPE, id(id), type);
    }

    private static <T extends AbstractElementalEnergyBallEntity> EntityType<T> registerEnergyBall(String id, EntityType.EntityFactory<T> factory, String en_us, String zh_cn) {
        EntityType<T> type = FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).dimensions(EntityDimensions.fixed(1.0f, 1.0f)).trackRangeChunks(8).trackedUpdateRate(10).forceTrackedVelocityUpdates(true).build();
        ENERGY_BALLS.put(type, id);
        return register(id, type, en_us, zh_cn);
    }
}
