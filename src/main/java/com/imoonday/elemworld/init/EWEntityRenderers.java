package com.imoonday.elemworld.init;

import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEntityRenderers {

    public static final String MAIN = "main";

    static EntityModelLayer registerMain(String id) {
        return new EntityModelLayer(id(id), MAIN);
    }

    public static void registerClient() {
        EWEntities.ENERGY_BALLS.forEach((type, id) -> EntityRendererRegistry.register(type, ctx -> new AbstractElementalEnergyBallEntity.EnergyBallEntityRenderer<>(ctx, id)));
    }
}
