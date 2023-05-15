package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> KEEP_ELEMENTS = register("keepElements", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true), "Keep elements on death", "死亡时保留元素");

    @NotNull
    private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> ruleType, String en_us, String zh_cn) {
        ElementalWorldData.addTranslation("gamerule." + name, en_us, zh_cn);
        return GameRuleRegistry.register(name, category, ruleType);
    }

    public static void register() {
        LOGGER.info("Loading Gamerules");
    }
}
