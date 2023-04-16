package com.imoonday.elemworld.api;

import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.random.Random;

import java.util.function.IntFunction;

public enum Element {

    GOLD(1, "gold", 1),
    WOOD(2, "wood", 1),
    WATER(3, "water", 1),
    FIRE(4, "fire", 1),
    EARTH(5, "earth", 1),
    WIND(6, "wind", 2),
    THUNDER(7, "thunder", 2),
    ROCK(8, "rock", 2),
    GRASS(9, "grass", 2),
    ICE(10, "ice", 2),
    LIGHT(11, "light", 3),
    DARKNESS(12, "darkness", 3),
    TIME(13, "time", 3),
    SPACE(14, "space", 3),
    SOUND(15, "sound", 3);

    private static final IntFunction<Element> BY_ID;
    private final int id;
    private final String name;
    private final int level;

    Element(int id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public static Element createRandom() {
        return BY_ID.apply(Random.create().nextBetween(1, Element.values().length));
    }

    public static Element createRandom(int level) {
        Element element = createRandom();
        while (element.level != level) {
            element = createRandom();
        }
        return element;
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(Element::getId, Element.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}
