package me.s4rtox.mmhunt.handlers.gamehandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRollbackManager {

    private final Map<UUID, SavedPlayer> savedPlayers = new HashMap<>();
    public void save(Player player) {
        if (savedPlayers.containsKey(player.getUniqueId())) {
            savedPlayers.replace(player.getUniqueId(), new SavedPlayer(player));
        } else {
            savedPlayers.put(player.getUniqueId(), new SavedPlayer(player));
        }
    }

    public void restore(Player player, boolean restoreLocation) {
        if (!savedPlayers.containsKey(player.getUniqueId())){
            Bukkit.getLogger().info("The player " + player.getName() + " wasn't in the list");
            return;
        }
        player.setFireTicks(0);
        SavedPlayer savedPlayer = savedPlayers.get(player.getUniqueId());
        if(savedPlayer == null){
            player.sendMessage("&cError restoring your last state!");
            savedPlayers.remove(player.getUniqueId());
            return;
        }
        player.getInventory().clear();

        if (savedPlayer.getInventoryItems() != null) {
            player.getInventory().setContents(savedPlayer.getInventoryItems());
        }
        if (savedPlayer.getInventoryArmor() != null) {
            player.getInventory().setArmorContents(savedPlayer.getInventoryArmor());
        }
        if(!savedPlayer.getPotions().isEmpty()){
            player.addPotionEffects(savedPlayer.getPotions());
        }
        player.setGameMode(savedPlayer.getGameMode());
        player.setFoodLevel(savedPlayer.getFoodLevel());
        player.setTotalExperience(savedPlayer.getTotalXP());
        if (restoreLocation) {
            if (savedPlayer.getLocation() != null) {
                player.teleport(savedPlayer.getLocation());
            }
        }
        savedPlayers.remove(player.getUniqueId());
    }


}
