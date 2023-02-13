package me.s4rtox.mmhunt.util;

import org.bukkit.ChatColor;

public class Colorize {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String stripColor(String colorizedText) {
        return ChatColor.stripColor(colorizedText);
    }

}
