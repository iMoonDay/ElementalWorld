package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

public class EWIdentifiers {

    public static final Identifier DISPLAY_SCREEN = id("display");
    public static final Identifier CHANGE_LEVEL = id("change_level");

    @Contract(value = "_ -> new", pure = true)
    public static Identifier id(String id) {
        return new Identifier(ElementalWorld.MOD_ID, id);
    }
}
