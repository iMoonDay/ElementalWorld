package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorld;
import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.elements.*;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.concurrent.ConcurrentHashMap;

import static com.imoonday.elemworld.ElementalWorld.id;

public class EWElements {

    public static final ConcurrentHashMap<String, Element> ELEMENTS = new ConcurrentHashMap<>();

    public static final Element EMPTY = register("empty", new Element(0));
    public static final Element GOLD = register("gold", new GoldElement(1, 1.25f, 1.25f, 1.0f, 0.75f));
    public static final Element WOOD = register("wood", new WoodElement(1, 0.75f, 0.75f, 1.0f, 1.25f));
    public static final Element WATER = register("water", new WaterElement(1, 1.0f, 0.5f, 1.0f, 1.0f));
    public static final Element FIRE = register("fire", new FireElement(1, 1.0f, 1.5f, 1.0f, 1.0f));
    public static final Element EARTH = register("earth", new EarthElement(1, 1.0f, 1.0f, 1.0f, 1.5f));
    public static final Element WIND = register("wind", new WindElement(2));
    public static final Element THUNDER = register("thunder", new ThunderElement(2, 1.0f, 1.75f, 1.0f, 1.0f));
    public static final Element ROCK = register("rock", new RockElement(2, 1.0f, 1.0f, 1.5f, 2.0f));
    public static final Element GRASS = register("grass", new GrassElement(2));
    public static final Element ICE = register("ice", new IceElement(2, 1.0f, 1.0f, 1.0f, 0.75f));
    public static final Element LIGHT = register("light", new LightElement(3));
    public static final Element DARKNESS = register("darkness", new DarknessElement(3));
    public static final Element TIME = register("time", new TimeElement(3, 1.75f, 1.75f, 1.75f, 1.0f));
    public static final Element SPACE = register("space", new SpaceElement(3, 1.5f, 1.5f, 1.5f, 1.0f));
    public static final Element SOUND = register("sound", new SoundElement(3, 1.75f, 1.75f, 1.75f, 1.0f));

    public static Element register(String name, Element element) {
        int index = 1;
        String s = name;
        while (ELEMENTS.containsKey(name)) {
            name = s + "_" + index++;
        }
        Element value = element.withName(name);
        ELEMENTS.put(name, value);
        return value;
    }
}
