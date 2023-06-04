package com.imoonday.elemworld.init;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.MeteoriteEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class EWCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(Element.Command::register);
        CommandRegistrationCallback.EVENT.register(MeteoriteEntity::registerCommand);
    }

}
