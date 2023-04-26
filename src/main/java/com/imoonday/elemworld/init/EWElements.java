package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.elements.*;

public class EWElements {

    public static final Element EMPTY = Element.register("empty", new Element(0, 0, 50));
    public static final Element GOLD = Element.register("gold", new GoldElement(3, 1, 40, 1.25f, 1.25f, 1.0f, 0.75f));
    public static final Element WOOD = Element.register("wood", new WoodElement(3, 1, 40, 0.75f, 0.75f, 1.0f, 1.25f));
    public static final Element WATER = Element.register("water", new WaterElement(3, 1, 40, 1.0f, 1.25f, 1.0f, 1.0f));
    public static final Element FIRE = Element.register("fire", new FireElement(3, 1, 40, 1.0f, 1.5f, 1.0f, 1.0f));
    public static final Element EARTH = Element.register("earth", new EarthElement(3, 1, 40, 1.0f, 1.0f, 1.0f, 1.5f));
    public static final Element WIND = Element.register("wind", new WindElement(2, 2, 20));
    public static final Element THUNDER = Element.register("thunder", new ThunderElement(2, 2, 20, 1.0f, 1.75f, 1.0f, 1.0f));
    public static final Element ROCK = Element.register("rock", new RockElement(2, 2, 20, 1.0f, 1.0f, 1.5f, 2.0f));
    public static final Element GRASS = Element.register("grass", new GrassElement(2, 2, 20));
    public static final Element ICE = Element.register("ice", new IceElement(2, 2, 20, 1.0f, 1.0f, 1.0f, 0.75f));
    public static final Element LIGHT = Element.register("light", new LightElement(1, 3, 5));
    public static final Element DARKNESS = Element.register("darkness", new DarknessElement(1, 3, 5));
    public static final Element TIME = Element.register("time", new TimeElement(1, 3, 5, 1.75f, 1.75f, 1.75f, 1.0f));
    public static final Element SPACE = Element.register("space", new SpaceElement(1, 3, 5, 1.5f, 1.5f, 1.5f, 1.0f));
    public static final Element SOUND = Element.register("sound", new SoundElement(1, 3, 5, 1.75f, 1.75f, 1.75f, 1.0f));

    public static void register() {

    }

}
