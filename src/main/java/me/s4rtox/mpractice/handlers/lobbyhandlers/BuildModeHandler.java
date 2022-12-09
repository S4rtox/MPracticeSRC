package me.s4rtox.mpractice.handlers.lobbyhandlers;

import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;
public class BuildModeHandler implements Listener {
    private final ConfigManager config;
    final HashMap<UUID, Boolean> buildMode = new HashMap<>();

    public BuildModeHandler(MPractice plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfigManager();
    }

    public void checkBuildMode(Player player){
        if(!(config.C_LOBBYWORLD_ENABLEDWORLDS().contains(player.getWorld().getName()))) return;
        if(buildMode.containsKey(player.getUniqueId())) {

            if (buildMode.get(player.getUniqueId())) {

                buildMode.put(player.getUniqueId(), false);
                player.sendMessage(config.MS_BUILDMODE_OFF());

            }
            else {

                buildMode.put(player.getUniqueId(), true);
                player.sendMessage(config.MS_BUILDMODE_ON());

            }
        }
        else{
            buildMode.put(player.getUniqueId(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(event.getBlockPlaced().isEmpty()) return;
        event.setCancelled(buildChecker(event.getPlayer()));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }

    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMultipleBlockPlace(BlockMultiPlaceEvent event) {
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(event.getBlockPlaced().isEmpty()) return;
        event.setCancelled(buildChecker(event.getPlayer()));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(!config.C_LOBBYWORLD_BUILDMODE_INTERACTIONS()) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        event.setCancelled(buildChecker(event.getPlayer()));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event){
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(event.getBlock().isEmpty()) return;
        event.setCancelled(buildChecker(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinRegister(PlayerJoinEvent event){
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        buildMode.put(event.getPlayer().getUniqueId(), false);
    }

    private boolean buildChecker(Player player){
        //Checks if the world is in the config, if it is checks if the player is in the hashmap, if it is it gets the value of the variable. If any of the before say false it shows true;
        return  !(config.C_LOBBYWORLD_ENABLEDWORLDS().contains(player.getWorld().getName())) || !(buildMode.containsKey(player.getUniqueId()) && buildMode.get(player.getUniqueId()));
    }
}
