package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.Element;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.imoonday.elemworld.init.EWTranslationKeys.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EWCommands {

    private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(slot -> Text.translatable("commands.item.source.no_such_slot", slot));
    private static final String ENTITY = "entity";
    private static final String ADD = "add";
    private static final String ADDALL = "addall";
    private static final String REMOVE = "remove";
    private static final String GET = "get";
    private static final String CLEAR = "clear";
    private static final String ITEM = "item";
    private static final String ELEMENT = "element";
    private static final String LEVEL = "level";
    private static final String SLOT = "slot";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal(ELEMENT).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(literal(ENTITY)
                        .then(literal(ADD).then(argument(ENTITY, EntityArgumentType.entities()).then(argument(ELEMENT, Element.ElementArgumentType.element()).then(argument(LEVEL, IntegerArgumentType.integer(0)).executes(context -> {
                            entityAdd(context);
                            return 0;
                        })))))
                        .then(literal(ADDALL).then(argument(ENTITY, EntityArgumentType.entities()).executes(context -> {
                            entityAddAll(context);
                            return 0;
                        })))
                        .then(literal(REMOVE).then(argument(ENTITY, EntityArgumentType.entities()).then(argument(ELEMENT, Element.ElementArgumentType.element()).executes(context -> {
                            entityRemove(context);
                            return 0;
                        }))))
                        .then(literal(GET).then(argument(ENTITY, EntityArgumentType.entities()).executes(context -> {
                            entityGet(context);
                            return 0;
                        })))
                        .then(literal(CLEAR).then(argument(ENTITY, EntityArgumentType.entities()).executes(context -> {
                            entityClear(context);
                            return 0;
                        }))))
                .then(literal(ITEM)
                        .then(literal(ADD).then(argument(ENTITY, EntityArgumentType.entities()).then(argument(SLOT, ItemSlotArgumentType.itemSlot()).then(argument(ELEMENT, Element.ElementArgumentType.element()).then(argument(LEVEL, IntegerArgumentType.integer(0)).executes(context -> {
                            itemAdd(context);
                            return 0;
                        }))))))
                        .then(literal(ADDALL).then(argument(ENTITY, EntityArgumentType.entities()).then(argument(SLOT, ItemSlotArgumentType.itemSlot()).executes(context -> {
                            itemAddAll(context);
                            return 0;
                        }))))
                        .then(literal(REMOVE).then(argument(ENTITY, EntityArgumentType.entities()).then(argument(SLOT, ItemSlotArgumentType.itemSlot()).then(argument(ELEMENT, Element.ElementArgumentType.element()).executes(context -> {
                            itemRemove(context);
                            return 0;
                        })))))
                        .then(literal(CLEAR).then(argument(ENTITY, EntityArgumentType.entities()).then(argument(SLOT, ItemSlotArgumentType.itemSlot()).executes(context -> {
                            itemClear(context);
                            return 0;
                        }))))))
        );
    }

    private static void itemClear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        int slot = ItemSlotArgumentType.getItemSlot(context, SLOT);
        for (Entity entity : entities) {
            ItemStack stackInSlot = getStackInSlot(entity, slot);
            if (source != null && isValidStack(source, stackInSlot)) {
                stackInSlot.setElements(new HashSet<>(Collections.singleton(Element.Entry.EMPTY)));
                source.sendFeedback(Text.translatable(CLEAR_SUCCESS), false);
            }
        }
    }

    private static void itemRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        for (Entity entity : entities) {
            Element element = Element.ElementArgumentType.getElement(context, ELEMENT);
            int slot = ItemSlotArgumentType.getItemSlot(context, SLOT);
            ItemStack stackInSlot = getStackInSlot(entity, slot);
            if (element != null && source != null && isValidStack(source, stackInSlot)) {
                boolean exist = stackInSlot.hasElement(element);
                stackInSlot.removeElement(element);
                source.sendFeedback(Text.translatable(exist ? REMOVE_SUCCESS : REMOVE_FAIL), false);
            }
        }
    }

    private static void itemAddAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        for (Entity entity : entities) {
            int slot = ItemSlotArgumentType.getItemSlot(context, SLOT);
            ItemStack stackInSlot = getStackInSlot(entity, slot);
            if (source != null && isValidStack(source, stackInSlot)) {
                stackInSlot.setElements(new HashSet<>());
                for (Element element : Element.getRegistrySet(false)) {
                    stackInSlot.addElement(new Element.Entry(element, element.maxLevel));
                }
                source.sendFeedback(Text.translatable(ADD_SUCCESS), false);
            }
        }
    }

    private static void itemAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        for (Entity entity : entities) {
            Element element = Element.ElementArgumentType.getElement(context, ELEMENT);
            int slot = ItemSlotArgumentType.getItemSlot(context, SLOT);
            ItemStack stackInSlot = getStackInSlot(entity, slot);
            int level = IntegerArgumentType.getInteger(context, LEVEL);
            if (element != null && source != null && isValidStack(source, stackInSlot)) {
                boolean success = stackInSlot.addElement(new Element.Entry(element, level));
                source.sendFeedback(Text.translatable(success ? ADD_SUCCESS : ADD_FAIL), false);
                if (element.isOf(EWElements.EMPTY)) {
                    source.sendFeedback(Text.translatable(ADD_SUCCESS_EMPTY), false);
                }
            }
        }
    }

    private static void entityClear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && source != null) {
                livingEntity.clearElements();
                source.sendFeedback(Text.translatable(CLEAR_SUCCESS), false);
            }
        }
    }

    private static void entityGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && source != null) {
                List<MutableText> texts = Element.getElementsText(livingEntity.getElements(), false, true);
                if (texts.isEmpty()) {
                    source.sendFeedback(Text.translatable(GET_EMPTY), false);
                } else {
                    texts.forEach(message -> source.sendFeedback(message, false));
                }
            }
        }
    }

    private static void entityRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        Element element = Element.ElementArgumentType.getElement(context, ELEMENT);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && element != null && source != null) {
                boolean exist = livingEntity.hasElement(element);
                livingEntity.removeElement(element);
                source.sendFeedback(Text.translatable(exist ? REMOVE_SUCCESS : REMOVE_FAIL), false);
            }
        }
    }

    private static void entityAddAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && source != null) {
                livingEntity.clearElements();
                for (Element element : Element.getRegistrySet(false)) {
                    livingEntity.addElement(new Element.Entry(element, element.maxLevel));
                }
                source.sendFeedback(Text.translatable(ADD_SUCCESS), false);
            }
        }
    }

    private static void entityAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, ENTITY);
        Element element = Element.ElementArgumentType.getElement(context, ELEMENT);
        for (Entity entity : entities) {
            int level = IntegerArgumentType.getInteger(context, LEVEL);
            if (entity instanceof LivingEntity livingEntity && element != null && source != null) {
                boolean success = livingEntity.addElement(new Element.Entry(element, level));
                source.sendFeedback(Text.translatable(success ? ADD_SUCCESS : ADD_FAIL), false);
                if (element.isOf(EWElements.EMPTY)) {
                    source.sendFeedback(Text.translatable(ADD_SUCCESS_EMPTY), false);
                }
            }
        }
    }

    private static boolean isValidStack(ServerCommandSource source, ItemStack stackInSlot) {
        if (stackInSlot.isEmpty()) {
            source.sendError(Text.translatable(INVALID_ITEM).formatted(Formatting.RED));
            return false;
        }
        if (!stackInSlot.isDamageable()) {
            source.sendError(Text.translatable(UNSUPPORTED_ITEM).formatted(Formatting.RED));
            return false;
        }
        return true;
    }

    private static ItemStack getStackInSlot(Entity entity, int slotId) throws CommandSyntaxException {
        StackReference stackReference = entity.getStackReference(slotId);
        if (stackReference == StackReference.EMPTY) {
            throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(slotId);
        }
        return stackReference.get();
    }
}
