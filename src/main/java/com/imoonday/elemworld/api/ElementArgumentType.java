package com.imoonday.elemworld.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ElementArgumentType implements ArgumentType<Element> {

    public static final DynamicCommandExceptionType INVALID_ELEMENT_EXCEPTION = new DynamicCommandExceptionType(element -> Text.translatable("element.elemworld.invalid", element));

    @Contract(value = " -> new", pure = true)
    public static @NotNull ElementArgumentType element() {
        return new ElementArgumentType();
    }

    public static Element getElement(@NotNull CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, Element.class);
    }

    @Override
    public Element parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        Optional<Element> element = Element.byName(string);
        if (element.isEmpty()) {
            throw INVALID_ELEMENT_EXCEPTION.create(string);
        }
        return element.get();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Element.getRegistryMap().keySet(), builder);
    }
}
