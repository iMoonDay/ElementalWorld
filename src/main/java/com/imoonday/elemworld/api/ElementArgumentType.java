package com.imoonday.elemworld.api;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class ElementArgumentType extends EnumArgumentType<Element> {

    public ElementArgumentType() {
        super(Element.CODEC, Element::values);
    }

    public static EnumArgumentType<Element> element() {
        return new ElementArgumentType();
    }

    public static Element getElement(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, Element.class);
    }
}
