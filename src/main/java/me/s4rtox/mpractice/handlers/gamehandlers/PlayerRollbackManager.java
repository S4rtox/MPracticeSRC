package me.s4rtox.mpractice.handlers.gamehandlers;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRollbackManager {

    private static final Map<UUID, ItemStack[]> previousInventoryContents = new HashMap<>();
    private static final Map<UUID, ItemStack[]> previousArmorContents = new HashMap<>();
    private static final Map<UUID, GameMode> previousGamemode = new HashMap<>();

    private static final Map<UUID, Location> previousLocation= new HashMap<>();

    public static void save(Player player){
        previousInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
        previousArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
        previousGamemode.put(player.getUniqueId(),player.getGameMode());
        previousLocation.put(player.getUniqueId(), player.getLocation());
    }

    public static void restore(Player player){
        player.getInventory().clear();
        ItemStack[] inventoryContent = previousInventoryContents.get(player.getUniqueId());
        if(inventoryContent != null){
            player.getInventory().setContents(inventoryContent);
            previousInventoryContents.remove(player.getUniqueId());
        }

        ItemStack[] armorContent = previousArmorContents.get(player.getUniqueId());
        if(armorContent != null){
            player.getInventory().setArmorContents(armorContent);
            previousArmorContents.remove(player.getUniqueId());
        }

        GameMode gameMode = previousGamemode.get(player.getUniqueId());
        if(gameMode != null){
            player.setGameMode(gameMode);
            previousGamemode.remove(player.getUniqueId());
        }

        Location location = previousLocation.get(player.getUniqueId());
        if(location != null){
            player.teleport(location);
            previousLocation.remove(player.getUniqueId());
        }

    }




}
