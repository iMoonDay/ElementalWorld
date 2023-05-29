package com.imoonday.elemworld.init;

import com.google.common.collect.ImmutableMap;
import com.imoonday.elemworld.ElementalWorld;
import com.imoonday.elemworld.ElementalWorldData;
import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.*;
import com.imoonday.elemworld.entities.energy_balls.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.data.client.Model;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

import java.util.*;
import java.util.function.Predicate;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEntities {

    private static final Map<EntityType<? extends AbstractElementalEnergyBallEntity>, String> ENERGY_BALLS = new HashMap<>();
    private static final Set<EntityType<? extends Entity>> ENTITIES_WITHOUT_RENDERER = new HashSet<>();
    public static final Model TEMPLATE_SPAWN_EGG_MODEL = new Model(Optional.of(new Identifier("minecraft", "item/template_spawn_egg")), Optional.empty());
    public static final String MAIN = "main";

    public static final EntityType<DarknessElementalEnergyBallEntity> DARKNESS_ELEMENTAL_ENERGY_BALL = registerEnergyBall(DarknessElementalEnergyBallEntity::new);
    public static final EntityType<EarthElementalEnergyBallEntity> EARTH_ELEMENTAL_ENERGY_BALL = registerEnergyBall(EarthElementalEnergyBallEntity::new);
    public static final EntityType<FireElementalEnergyBallEntity> FIRE_ELEMENTAL_ENERGY_BALL = registerEnergyBall(FireElementalEnergyBallEntity::new);
    public static final EntityType<GoldElementalEnergyBallEntity> GOLD_ELEMENTAL_ENERGY_BALL = registerEnergyBall(GoldElementalEnergyBallEntity::new);
    public static final EntityType<GrassElementalEnergyBallEntity> GRASS_ELEMENTAL_ENERGY_BALL = registerEnergyBall(GrassElementalEnergyBallEntity::new);
    public static final EntityType<IceElementalEnergyBallEntity> ICE_ELEMENTAL_ENERGY_BALL = registerEnergyBall(IceElementalEnergyBallEntity::new);
    public static final EntityType<LightElementalEnergyBallEntity> LIGHT_ELEMENTAL_ENERGY_BALL = registerEnergyBall(LightElementalEnergyBallEntity::new);
    public static final EntityType<RockElementalEnergyBallEntity> ROCK_ELEMENTAL_ENERGY_BALL = registerEnergyBall(RockElementalEnergyBallEntity::new);
    public static final EntityType<SoundElementalEnergyBallEntity> SOUND_ELEMENTAL_ENERGY_BALL = registerEnergyBall(SoundElementalEnergyBallEntity::new);
    public static final EntityType<SpaceElementalEnergyBallEntity> SPACE_ELEMENTAL_ENERGY_BALL = registerEnergyBall(SpaceElementalEnergyBallEntity::new);
    public static final EntityType<ThunderElementalEnergyBallEntity> THUNDER_ELEMENTAL_ENERGY_BALL = registerEnergyBall(ThunderElementalEnergyBallEntity::new);
    public static final EntityType<TimeElementalEnergyBallEntity> TIME_ELEMENTAL_ENERGY_BALL = registerEnergyBall(TimeElementalEnergyBallEntity::new);
    public static final EntityType<WaterElementalEnergyBallEntity> WATER_ELEMENTAL_ENERGY_BALL = registerEnergyBall(WaterElementalEnergyBallEntity::new);
    public static final EntityType<WindElementalEnergyBallEntity> WIND_ELEMENTAL_ENERGY_BALL = registerEnergyBall(WindElementalEnergyBallEntity::new);
    public static final EntityType<WoodElementalEnergyBallEntity> WOOD_ELEMENTAL_ENERGY_BALL = registerEnergyBall(WoodElementalEnergyBallEntity::new);

    public static final EntityType<ElementalElfEntity> ELEMENTAL_ELF = ElementalElfEntity.register();
    public static final EntityModelLayer MODEL_ELEMENTAL_ELF_LAYER = registerModelLayer("elemental_elf");

    public static final EntityType<GoblinEntity> GOBLIN = GoblinEntity.register();
    public static final EntityType<GoblinTraderEntity> GOBLIN_TRADER = GoblinTraderEntity.register();
    public static final EntityType<ApolloEntity> APOLLO = ApolloEntity.register();

    public static void register() {
        ElementalWorld.LOGGER.info("Loading Entities");
    }

    public static void registerClient() {
        ENERGY_BALLS.forEach((type, id) -> EntityRendererRegistry.register(type, ctx -> new AbstractElementalEnergyBallEntity.EnergyBallEntityRenderer<>(ctx, id)));
        EntityModelLayerRegistry.registerModelLayer(MODEL_ELEMENTAL_ELF_LAYER, ElementalElfEntity.Model::getTexturedModelData);
        EntityRendererRegistry.register(ELEMENTAL_ELF, ElementalElfEntity.Renderer::new);
        EntityRendererRegistry.register(GOBLIN, GoblinEntity.Renderer::new);
        EntityRendererRegistry.register(GOBLIN_TRADER, GoblinTraderEntity.Renderer::new);
        ENTITIES_WITHOUT_RENDERER.forEach(entityType -> EntityRendererRegistry.register(entityType, ctx -> new EntityRenderer<Entity>(ctx) {
            @Override
            public Identifier getTexture(Entity entity) {
                return null;
            }
        }));
    }

    public static EntityModelLayer registerModelLayer(String id) {
        return new EntityModelLayer(id(id), MAIN);
    }

    public static <T extends Entity> EntityType<T> register(String id, EntityType<T> type, String en_us, String zh_cn) {
        ElementalWorldData.addTranslation(type, en_us, zh_cn);
        return Registry.register(Registries.ENTITY_TYPE, id(id), type);
    }

    public static <T extends MobEntity> EntityType<T> register(String id, EntityType<T> type, String en_us, String zh_cn, DefaultAttributeContainer.Builder attributes, SpawnRestriction.Location spawnLocation, Heightmap.Type spawnType, SpawnRestriction.SpawnPredicate<T> spawnPredicate, Predicate<BiomeSelectionContext> spawnBiomePredicate, SpawnGroup spawnGroup, int spawnWeight, int spawnMinGroupSize, int spawnMaxGroupSize, int spawnEggPrimaryColor, int spawnEggSecondaryColor) {
        EntityType<T> register = register(id, type, en_us, zh_cn);
        registerSpawnRestrictions(type, spawnLocation, spawnType, spawnPredicate);
        addSpawns(type, spawnBiomePredicate, spawnGroup, spawnWeight, spawnMinGroupSize, spawnMaxGroupSize);
        SpawnEggItem item = EWItems.register(id + "_spawn_egg", new SpawnEggItem(type, spawnEggPrimaryColor, spawnEggSecondaryColor, new FabricItemSettings()), ItemGroups.SPAWN_EGGS, TEMPLATE_SPAWN_EGG_MODEL, en_us + " Spawn Egg", zh_cn + "刷怪蛋");
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(entries -> entries.add(item));
        registerAttributes(type, attributes);
        return register;
    }

    public static <T extends MobEntity> EntityType<T> registerWithoutRenderer(String id, EntityType<T> type, String en_us, String zh_cn, DefaultAttributeContainer.Builder attributes, SpawnRestriction.Location spawnLocation, Heightmap.Type spawnType, SpawnRestriction.SpawnPredicate<T> spawnPredicate, Predicate<BiomeSelectionContext> spawnBiomePredicate, SpawnGroup spawnGroup, int spawnWeight, int spawnMinGroupSize, int spawnMaxGroupSize, int spawnEggPrimaryColor, int spawnEggSecondaryColor) {
        ENTITIES_WITHOUT_RENDERER.add(type);
        return register(id, type, en_us, zh_cn, attributes, spawnLocation, spawnType, spawnPredicate, spawnBiomePredicate, spawnGroup, spawnWeight, spawnMinGroupSize, spawnMaxGroupSize, spawnEggPrimaryColor, spawnEggSecondaryColor);
    }

    private static <T extends MobEntity> void registerSpawnRestrictions(EntityType<T> type, SpawnRestriction.Location spawnLocation, Heightmap.Type spawnType, SpawnRestriction.SpawnPredicate<T> spawnPredicate) {
        SpawnRestriction.register(type, spawnLocation, spawnType, spawnPredicate);
    }

    private static <T extends MobEntity> void registerAttributes(EntityType<T> type, DefaultAttributeContainer.Builder attributes) {
        FabricDefaultAttributeRegistry.register(type, attributes);
    }

    private static <T extends MobEntity> void addSpawns(EntityType<T> type, Predicate<BiomeSelectionContext> biomeSelector, SpawnGroup spawnGroup, int spawnWeight, int spawnMinGroupSize, int spawnMaxGroupSize) {
        BiomeModifications.addSpawn(biomeSelector, spawnGroup, type, spawnWeight, spawnMinGroupSize, spawnMaxGroupSize);
    }

    public static <T extends AbstractElementalEnergyBallEntity> EntityType<T> registerEnergyBall(EntityType.EntityFactory<T> factory) {
        EntityType<T> type = FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).dimensions(EntityDimensions.fixed(1.0f, 1.0f)).trackRangeChunks(8).trackedUpdateRate(10).forceTrackedVelocityUpdates(true).build();
        Element element = factory.create(type, null).getElement();
        String id = element.getName() + "_elemental_energy_ball";
        if (ENERGY_BALLS.containsValue(id)) {
            throw new IllegalStateException(id + " already exists!");
        }
        ENERGY_BALLS.put(type, id);
        return register(id, type, element.getTranslation().get() + " Elemental Energy Ball", element.getTranslation().get("zh_cn") + "元素能量球");
    }

    public static ImmutableMap<EntityType<? extends AbstractElementalEnergyBallEntity>, String> getEnergyBalls() {
        return ImmutableMap.copyOf(ENERGY_BALLS);
    }
}
