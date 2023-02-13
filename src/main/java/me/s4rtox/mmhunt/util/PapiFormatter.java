package me.s4rtox.mmhunt.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class PapiFormatter {
    private static boolean papiStatus;

    public static String formatText(String string, Player player) {
        if (papiStatus) {
            string = PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

    public static List<String> formatText(List<String> string, Player player) {
        if (papiStatus) {
            string = PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

    public static void setPapiStatus(boolean status) {
        papiStatus = status;
    }

    public static boolean getPapiStatus() {
        return papiStatus;
    }
}
