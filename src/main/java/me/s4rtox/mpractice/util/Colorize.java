package me.s4rtox.mpractice.util;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.*;
import org.bukkit.ChatColor;

public class Colorize {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String stripColor(String colorizedText) {
        return ChatColor.stripColor(colorizedText);
    }

    public static String formatArenaState(ArenaState state) {
        if (state instanceof InitArenaState) {
            return "&6&lSetting up...";
        } else if (state instanceof WaitingArenaState) {
            return "&e&lWaiting...";
        } else if (state instanceof StartingArenaState) {
            return "&6&lStarting...";
        } else if (state instanceof ActiveArenaState) {
            return "&1&lOngoing";
        } else if (state instanceof FinishingArenaState) {
            return "&0&lFinishing...";
        } else {
            return "&7&lRestarting";
        }
    }
}
