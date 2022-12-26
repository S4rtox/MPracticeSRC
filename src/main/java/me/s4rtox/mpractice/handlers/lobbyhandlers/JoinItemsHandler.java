package me.s4rtox.mpractice.handlers.lobbyhandlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.config.ConfigManager;
import me.s4rtox.mpractice.util.ItemBuilder;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class JoinItemsHandler implements Listener {

    private final ConfigManager config;

    public JoinItemsHandler(MPractice plugin) {
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
                && (nbtItem = new NBTItem(event.getItemDrop().getItemStack())).hasCustomNbtData()
                && nbtItem.hasTag("LobbyItem")
                && nbtItem.getBoolean("LobbyItem");
        if (flag) {
            event.setCancelled(true);
        }
    }

    /* 1.9+ offhand handler
    @EventHandler
    public void onPlayerSwapOffHand(PlayerSwapHandItemsEvent event){
        //Blocks Swapping Custom Items 1.9+
        Player human = event.getPlayer();
        ItemStack item = event.getOffHandItem();
        boolean flag = item != null
                && human.getGameMode() != GameMode.CREATIVE
                && item.getItemMeta() != null
                && config.getBoolean("JoinItems.Enderbutt.Enabled", true)
                && Colorize.format( config.getString("JoinItems.Enderbutt.DisplayName", "&4EnderButt")).equals(item.getItemMeta().getDisplayName());
        if (flag) {
            event.setCancelled(true);
        }
    }
     */

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
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(event.getPlayer().getWorld().getName())) return;
        event.getPlayer().getInventory().clear();
        giveJoinItems(event.getPlayer().getInventory());

    }

    private void giveJoinItems(Inventory inv) {
        // EnderButt Enabled
        List<String> lore = Collections.singletonList("1");
        NBTItem enderButt = new NBTItem(ItemBuilder.getItem(new ItemStack(Material.ENDER_PEARL), "Test", true, lore));
        enderButt.setBoolean("LobbyItem", true);
        enderButt.setBoolean("EnderButt", true);
        inv.setItem(1, enderButt.getItem());
    }

}
// --------------- END JOIN/RESPAWN CUSTOM ITEM GIVER  ---------------

