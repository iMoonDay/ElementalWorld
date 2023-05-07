package com.imoonday.elemworld.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> KEEP_ELEMENTS = GameRuleRegistry.register("keepElements", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

    public static void register(){
        LOGGER.info("Loading Gamerules");
    }
}
