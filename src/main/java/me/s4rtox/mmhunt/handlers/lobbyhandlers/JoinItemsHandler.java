package me.s4rtox.mmhunt.handlers.lobbyhandlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class JoinItemsHandler implements Listener {

    private final ConfigManager config;

    public JoinItemsHandler(MMHunt plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfigManager();
    }

    // ---------------  ANTI MOVE CUSTOM ITEMS INVENTORY  ---------------
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!tryCancelMoveEvent(event, event.getCurrentItem())) {
            if (event.getHotbarButton() == -1) return;
            tryCancelMoveEvent(event, event.getClickedInventory().getItem(event.getHotbarButton()));
        }
    }

    private boolean tryCancelMoveEvent(InventoryClickEvent event, ItemStack item) {
        //Tries to check most items moves on the inventory (of the custom items)
        HumanEntity player;
        NBTItem nbtItem;
        boolean flag = (player = event.getWhoClicked()) instanceof Player
                && item != null
                && item.getAmount() != 0
                && item.getType() != Material.AIR
                && player.getGameMode() != GameMode.CREATIVE
                && (nbtItem = new NBTItem(item)).hasCustomNbtData()
                && nbtItem.hasTag("LobbyItem")
                && nbtItem.getBoolean("LobbyItem");
        if (flag) {
            event.setCancelled(true);
        }
        return flag;
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        //Blocks Dropping Custom Items
        Player human = event.getPlayer();
        NBTItem nbtItem;
        ItemStack item = event.getItemDrop().getItemStack();
        boolean flag = human.getGameMode() != GameMode.CREATIVE
                && item != null
                && item.getType() != Material.AIR
                && item.getAmount() != 0
                && (nbtItem = new NBTItem(item)).hasCustomNbtData()
                && nbtItem.hasTag("LobbyItem")
                && nbtItem.getBoolean("LobbyItem");
        if (flag) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapOffHand(PlayerSwapHandItemsEvent event){
        //Blocks Swapping Custom Items 1.9+
        Player human = event.getPlayer();
        ItemStack item = event.getOffHandItem();
        NBTItem nbtItem;
        boolean flag = item != null
                && human.getGameMode() != GameMode.CREATIVE
                && item.getItemMeta() != null
                && (nbtItem = new NBTItem(item)).hasCustomNbtData()
                && nbtItem.hasTag("LobbyItem")
                && nbtItem.getBoolean("LobbyItem");
        if (flag) {
            event.setCancelled(true);
        }
    }

    // --------------- END ANTI MOVE CUSTOM ITEMS INVENTORY  ---------------


    // --------------- JOIN/RESPAWN CUSTOM ITEM GIVER  ---------------
    /*
    @EventHandler(priority = EventPriority.LOW)
    public void joinWorld(PlayerChangedWorldEvent event) {
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(event.getPlayer().getWorld().getName())) return;
        event.getPlayer().getInventory().clear();
        giveJoinItems(event.getPlayer().getInventory());
    }
     */

    @EventHandler(priority = EventPriority.LOW)
    public void joinInvItems(PlayerJoinEvent event) {
        if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(event.getPlayer().getWorld().getName())) return;
        event.getPlayer().getInventory().clear();
        giveJoinItems(event.getPlayer().getInventory());

    }

    private void giveJoinItems(Inventory inv) {
        // EnderButt Enabled
    }

}
// --------------- END JOIN/RESPAWN CUSTOM ITEM GIVER  ---------------

