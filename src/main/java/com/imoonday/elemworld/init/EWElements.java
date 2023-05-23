package com.imoonday.elemworld.init;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.elements.*;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWElements {

    public static final Element EMPTY = Element.register("empty", new EmptyElement(), null, null);
    public static final Element GOLD = Element.register("gold", new GoldElement(), "Gold", "金");
    public static final Element WOOD = Element.register("wood", new WoodElement(), "Wood", "木");
    public static final Element WATER = Element.register("water", new WaterElement(), "Water", "水");
    public static final Element FIRE = Element.register("fire", new FireElement(), "Fire", "火");
    public static final Element EARTH = Element.register("earth", new EarthElement(), "Earth", "土");
    public static final Element WIND = Element.register("wind", new WindElement(), "Wind", "风");
    public static final Element THUNDER = Element.register("thunder", new ThunderElement(), "Thunder", "雷");
    public static final Element ROCK = Element.register("rock", new RockElement(), "Rock", "岩");
    public static final Element GRASS = Element.register("grass", new GrassElement(), "Grass", "草");
    public static final Element ICE = Element.register("ice", new IceElement(), "Ice", "冰");
    public static final Element LIGHT = Element.register("light", new LightElement(), "Light", "光明");
    public static final Element DARKNESS = Element.register("darkness", new DarknessElement(), "Darkness", "黑暗");
    public static final Element TIME = Element.register("time", new TimeElement(), "Time", "时间");
    public static final Element SPACE = Element.register("space", new SpaceElement(), "Space", "空间");
    public static final Element SOUND = Element.register("sound", new SoundElement(), "Sound", "声音");

    public static void register() {
        LOGGER.info("Loading Elements");
    }

}
