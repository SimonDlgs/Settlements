package com.huskydreaming.settlements.commands;

import org.bukkit.entity.Player;

public interface CommandInterface {

    default CommandLabel getLabel() {
        return getClass().getAnnotation(Command.class).label();
    }

    default String[] getAliases() {
        return getClass().getAnnotation(Command.class).aliases();
    }

    default boolean requiresPermissions() {
        return getClass().getAnnotation(Command.class).requiresPermissions();
    }

    default boolean isDebug() {
        return getClass().getAnnotation(Command.class).debug();
    }

    void run(Player player, String[] strings);
}
