package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.api.ElementArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EWCommands {

    private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(slot -> Text.translatable("commands.item.source.no_such_slot", slot));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("element").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
                .then(literal("entity")
                        .then(literal("add").then(argument("entity", EntityArgumentType.entity()).then(argument("element", ElementArgumentType.element()).then(argument("level", IntegerArgumentType.integer(0)).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            Element element = ElementArgumentType.getElement(context, "element");
                            int level = IntegerArgumentType.getInteger(context, "level");
                            if (entity instanceof LivingEntity livingEntity && element != null && player != null) {
                                boolean success = livingEntity.addElement(element, level);
                                player.sendMessage(Text.translatable(success ? "text.eleworld.commands.add.success" : "text.eleworld.commands.add.fail"));
                                if (element.isOf(EWElements.EMPTY)) {
                                    player.sendMessage(Text.translatable("text.eleworld.commands.add.success.empty"));
                                }
                            }
                            return 0;
                        })))))
                        .then(literal("addall").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            if (entity instanceof LivingEntity livingEntity && player != null) {
                                livingEntity.clearElements();
                                for (Element element : Element.getRegistrySet()) {
                                    livingEntity.addElement(element, element.getMaxLevel());
                                }
                                player.sendMessage(Text.translatable("text.eleworld.commands.add.success"));
                            }
                            return 0;
                        })))
                        .then(literal("remove").then(argument("entity", EntityArgumentType.entity()).then(argument("element", ElementArgumentType.element()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            Element element = ElementArgumentType.getElement(context, "element");
                            if (entity instanceof LivingEntity livingEntity && element != null && player != null) {
                                boolean exist = livingEntity.hasElement(element);
                                livingEntity.removeElement(element);
                                player.sendMessage(Text.translatable(exist ? "text.eleworld.commands.remove.success" : "text.eleworld.commands.remove.fail"));
                            }
                            return 0;
                        }))))
                        .then(literal("get").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
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
                            return 0;
                        })))
                        .then(literal("clear").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            if (entity instanceof LivingEntity livingEntity && player != null) {
                                livingEntity.clearElements();
                                player.sendMessage(Text.translatable("text.eleworld.commands.clear.success"));
                            }
                            return 0;
                        }))))
                .then(literal("item")
                        .then(literal("add").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).then(argument("element", ElementArgumentType.element()).then(argument("level", IntegerArgumentType.integer(0)).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            Element element = ElementArgumentType.getElement(context, "element");
                            int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
                            ItemStack stackInSlot = getStackInSlot(entity, slot);
                            int level = IntegerArgumentType.getInteger(context, "level");
                            if (element != null && player != null && isValidStack(player, stackInSlot)) {
                                boolean success = stackInSlot.addElement(element, level);
                                player.sendMessage(Text.translatable(success ? "text.eleworld.commands.add.success" : "text.eleworld.commands.add.fail"));
                                if (element.isOf(EWElements.EMPTY)) {
                                    player.sendMessage(Text.translatable("text.eleworld.commands.add.success.empty"));
                                }
                            }
                            return 0;
                        }))))))
                        .then(literal("addall").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
                            ItemStack stackInSlot = getStackInSlot(entity, slot);
                            if (player != null && isValidStack(player, stackInSlot)) {
                                stackInSlot.setElements(new HashMap<>());
                                for (Element element : Element.getRegistrySet()) {
                                    stackInSlot.addElement(element, element.getMaxLevel());
                                }
                                player.sendMessage(Text.translatable("text.eleworld.commands.add.success"));
                            }
                            return 0;
                        }))))
                        .then(literal("remove").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).then(argument("element", ElementArgumentType.element()).executes(context -> {
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
                            return 0;
                        })))))
                        .then(literal("clear").then(argument("entity", EntityArgumentType.entity()).then(argument("slot", ItemSlotArgumentType.itemSlot()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            Entity entity = EntityArgumentType.getEntity(context, "entity");
                            int slot = ItemSlotArgumentType.getItemSlot(context, "slot");
                            ItemStack stackInSlot = getStackInSlot(entity, slot);
                            if (player != null && isValidStack(player, stackInSlot)) {
                                HashMap<Element, Integer> map = new HashMap<>();
                                map.put(EWElements.EMPTY, 0);
                                stackInSlot.setElements(map);
                                player.sendMessage(Text.translatable("text.eleworld.commands.clear.success"));
                            }
                            return 0;
                        }))))))
        );
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
