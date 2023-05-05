package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.api.ElementArgumentType;
import com.imoonday.elemworld.api.ElementInstance;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EWCommands {

    private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(slot -> Text.translatable("commands.item.source.no_such_slot", slot));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("element").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
                .then(literal("entity")
                        .then(literal("add").then(argument("entity", EntityArgumentType.entity()).then(argument("element", ElementArgumentType.element()).then(argument("level", IntegerArgumentType.integer(0)).executes(context -> {
                            entityAdd(context);
                            return 0;
                        })))))
                        .then(literal("addall").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                            entityAddAll(context);
                            return 0;
                        })))
                        .then(literal("remove").then(argument("entity", EntityArgumentType.entity()).then(argument("element", ElementArgumentType.element()).executes(context -> {
                            entityRemove(context);
                            return 0;
                        }))))
                        .then(literal("get").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                            entityGet(context);
                            return 0;
                        })))
                        .then(literal("clear").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                            entityClear(context);
                            return 0;
                        }))))
                .then(literal("item")
                        .then(literal("add").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).then(argument("element", ElementArgumentType.element()).then(argument("level", IntegerArgumentType.integer(0)).executes(context -> {
                            itemAdd(context);
                            return 0;
                        }))))))
                        .then(literal("addall").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).executes(context -> {
                            itemAddAll(context);
                            return 0;
                        }))))
                        .then(literal("remove").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).then(argument("element", ElementArgumentType.element()).executes(context -> {
                            itemRemove(context);
                            return 0;
                        })))))
                        .then(literal("clear").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).executes(context -> {
                            itemClear(context);
                            return 0;
                        }))))))
        );
    }

    private static void itemClear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
        ItemStack stackInSlot = getStackInSlot(entity, slot);
        if (player != null && isValidStack(player, stackInSlot)) {
            stackInSlot.setElements(new HashSet<>(Collections.singleton(ElementInstance.EMPTY)));
            player.sendMessage(Text.translatable("text.eleworld.commands.clear.success"));
        }
    }

    private static void itemRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        Element element = ElementArgumentType.getElement(context, "element");
        int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
        ItemStack stackInSlot = getStackInSlot(entity, slot);
        if (element != null && player != null && isValidStack(player, stackInSlot)) {
            boolean exist = stackInSlot.hasElement(element);
            stackInSlot.removeElement(element);
            player.sendMessage(Text.translatable(exist ? "text.eleworld.commands.remove.success" : "text.eleworld.commands.remove.fail"));
        }
    }

    private static void itemAddAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
        ItemStack stackInSlot = getStackInSlot(entity, slot);
        if (player != null && isValidStack(player, stackInSlot)) {
            stackInSlot.setElements(new HashSet<>());
            for (Element element : Element.getRegistrySet()) {
                stackInSlot.addElement(new ElementInstance(element, element.getMaxLevel()));
            }
            player.sendMessage(Text.translatable("text.eleworld.commands.add.success"));
        }
    }

    private static void itemAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        Element element = ElementArgumentType.getElement(context, "element");
        int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
        ItemStack stackInSlot = getStackInSlot(entity, slot);
        int level = IntegerArgumentType.getInteger(context, "level");
        if (element != null && player != null && isValidStack(player, stackInSlot)) {
            boolean success = stackInSlot.addElement(new ElementInstance(element, level));
            player.sendMessage(Text.translatable(success ? "text.eleworld.commands.add.success" : "text.eleworld.commands.add.fail"));
            if (element.isOf(EWElements.EMPTY)) {
                player.sendMessage(Text.translatable("text.eleworld.commands.add.success.empty"));
            }
        }
    }

    private static void entityClear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        if (entity instanceof LivingEntity livingEntity && player != null) {
            livingEntity.clearElements();
            player.sendMessage(Text.translatable("text.eleworld.commands.clear.success"));
        }
    }

    private static void entityGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        if (entity instanceof LivingEntity livingEntity && player != null) {
            List<Text> texts = Element.getElementsText(livingEntity.getElements(), false, true);
            if (texts.isEmpty()) {
                player.sendMessage(Text.translatable("text.eleworld.commands.get.empty"));
            } else {
                texts.forEach(player::sendMessage);
            }
        }
    }

    private static void entityRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        Element element = ElementArgumentType.getElement(context, "element");
        if (entity instanceof LivingEntity livingEntity && element != null && player != null) {
            boolean exist = livingEntity.hasElement(element);
            livingEntity.removeElement(element);
            player.sendMessage(Text.translatable(exist ? "text.eleworld.commands.remove.success" : "text.eleworld.commands.remove.fail"));
        }
    }

    private static void entityAddAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        if (entity instanceof LivingEntity livingEntity && player != null) {
            livingEntity.clearElements();
            for (Element element : Element.getRegistrySet()) {
                livingEntity.addElement(new ElementInstance(element, element.getMaxLevel()));
            }
            player.sendMessage(Text.translatable("text.eleworld.commands.add.success"));
        }
    }

    private static void entityAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        Element element = ElementArgumentType.getElement(context, "element");
        int level = IntegerArgumentType.getInteger(context, "level");
        if (entity instanceof LivingEntity livingEntity && element != null && player != null) {
            boolean success = livingEntity.addElement(new ElementInstance(element, level));
            player.sendMessage(Text.translatable(success ? "text.eleworld.commands.add.success" : "text.eleworld.commands.add.fail"));
            if (element.isOf(EWElements.EMPTY)) {
                player.sendMessage(Text.translatable("text.eleworld.commands.add.success.empty"));
            }
        }
    }

    private static boolean isValidStack(ServerPlayerEntity player, ItemStack stackInSlot) {
        if (stackInSlot.isEmpty()) {
            player.sendMessage(Text.translatable("text.eleworld.commands.item.invalid").formatted(Formatting.RED));
            return false;
        }
        if (!stackInSlot.isDamageable()) {
            player.sendMessage(Text.translatable("text.eleworld.commands.item.unsupport").formatted(Formatting.RED));
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
