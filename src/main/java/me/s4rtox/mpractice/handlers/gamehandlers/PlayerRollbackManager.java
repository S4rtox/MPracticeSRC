package me.s4rtox.mpractice.handlers.gamehandlers;

import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRollbackManager {

    private static final Map<UUID, SavedPlayer> savedPlayers = new HashMap<>();

    public static void save(Player player) {
        if (savedPlayers.containsKey(player.getUniqueId())) {
            savedPlayers.replace(player.getUniqueId(), new SavedPlayer(player));
        } else {
            savedPlayers.put(player.getUniqueId(), new SavedPlayer(player));
        }
    }

    public static void restore(Player player, boolean restoreLocation) {
        SavedPlayer savedPlayer = savedPlayers.get(player.getUniqueId());
        if (savedPlayer == null) {
            player.sendMessage(Colorize.format("&cError restoring your last state"));
            savedPlayers.remove(player.getUniqueId());
            return;
        }
        player.getInventory().clear();

        if (savedPlayer.inventoryItems() != null) {
            player.getInventory().setContents(savedPlayer.inventoryItems());
        }
        if (savedPlayer.inventoryArmor() != null) {
            player.getInventory().setArmorContents(savedPlayer.inventoryArmor());
        }
        if (savedPlayer.gameMode() != null) {
            player.setGameMode(savedPlayer.gameMode());
        }
        player.setFoodLevel(savedPlayer.foodLevel());
        player.setTotalExperience(savedPlayer.totalXP());
        if (restoreLocation) {
            if (savedPlayer.location() != null) {
                player.teleport(savedPlayer.location());
            }
        }
        savedPlayers.remove(player.getUniqueId());
    }


}
