package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.api.ElementArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EWCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("element").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
                .then(literal("add").then(argument("entity", EntityArgumentType.entity()).then(argument("element", ElementArgumentType.element()).executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    Entity entity = EntityArgumentType.getEntity(context, "entity");
                    Element element = ElementArgumentType.getElement(context, "element");
                    if (entity instanceof LivingEntity livingEntity && element != null && player != null) {
                        boolean success = livingEntity.addElement(element);
                        player.sendMessage(Text.literal(success ? "添加成功" : "元素已存在"));
                    }
                    return 0;
                }))))
                .then(literal("remove").then(argument("entity", EntityArgumentType.entity()).then(argument("element", ElementArgumentType.element()).executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    Entity entity = EntityArgumentType.getEntity(context, "entity");
                    Element element = ElementArgumentType.getElement(context, "element");
                    if (entity instanceof LivingEntity livingEntity && element != null && player != null) {
                        boolean exist = livingEntity.getElements().contains(element);
                        livingEntity.removeElement(element);
                        player.sendMessage(Text.literal(exist ? "删除成功" : "元素不存在"));
                    }
                    return 0;
                }))))
                .then(literal("get").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    Entity entity = EntityArgumentType.getEntity(context, "entity");
                    if (entity instanceof LivingEntity livingEntity && player != null) {
                        player.sendMessage(Element.getElementsText(livingEntity.getElements()));
                    }
                    return 0;
                })))
                .then(literal("clear").then(argument("entity", EntityArgumentType.entity()).executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    Entity entity = EntityArgumentType.getEntity(context, "entity");
                    if (entity instanceof LivingEntity livingEntity && player != null) {
                        livingEntity.clearElements();
                        player.sendMessage(Text.literal("清除成功"));
                    }
                    return 0;
                }))))
        );
    }
}
