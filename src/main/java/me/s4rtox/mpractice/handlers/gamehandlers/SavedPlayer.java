package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class SavedPlayer {
    private final ItemStack[] inventoryItems;
    private final ItemStack[] inventoryArmor;
    private final GameMode gameMode;
    private final Location location;
    private final int foodLevel;
    private final int totalXP;

    public SavedPlayer(@NonNull Player player) {
        this.inventoryItems = player.getInventory().getContents();
        this.inventoryArmor = player.getInventory().getArmorContents();
        this.gameMode = player.getGameMode();
        this.foodLevel = player.getFoodLevel();
        this.totalXP = player.getTotalExperience();
        this.location = player.getLocation().clone();

    }
}
