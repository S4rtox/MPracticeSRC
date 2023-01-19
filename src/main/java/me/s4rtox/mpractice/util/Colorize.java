package me.s4rtox.mpractice.util;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Colorize {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String stripColor(String colorizedText) {
        return ChatColor.stripColor(colorizedText);
    }

}
