package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> KEEP_ELEMENTS = GameRuleRegistry.register("keepElements", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

    public static void register() {
        ElementalWorldData.addTranslation("gamerule.keepElements", "Keep elements on death", "死亡时保留元素");
        LOGGER.info("Loading Gamerules");
    }
}
