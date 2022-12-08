package me.s4rtox.mskywars.handlers;

import me.s4rtox.mskywars.MSkywars;
import me.s4rtox.mskywars.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class BuildMode implements Listener, CommandExecutor {
    private final ConfigUtil config;


    final HashMap<UUID, Boolean> buildMode = new HashMap<>();

    public BuildMode(MSkywars plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfigUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(config.MS_CONSOLE_COMMAND_EXECUTOR_ERROR());
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission(config.P_ADMIN_BUILDMODE())) {
            player.sendMessage(config.MS_NO_PERMISSION());
            return true;
        }

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
            buildMode.put(((Player)sender).getUniqueId(), false);
        }
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(event.getBlockPlaced().isEmpty()) return;
        event.setCancelled(buildChecker(event.getPlayer()));
        event.getPlayer().sendMessage(String.valueOf(buildChecker(event.getPlayer())));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }

    }

    @EventHandler
    public void onMultipleBlockPlace(BlockMultiPlaceEvent event) {
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(event.getBlockPlaced().isEmpty()) return;
        event.setCancelled(buildChecker(event.getPlayer()));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(!config.C_LOBBYWORLD_BUILDMODE_INTERACTIONS()) return;
        if(event.getClickedBlock() == null) return;
        event.setCancelled(buildChecker(event.getPlayer()));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        if(event.getBlock().isEmpty()) return;
        event.setCancelled(buildChecker(event.getPlayer()));
        if(event.isCancelled()){
            event.getPlayer().updateInventory();
        }
    }
    @EventHandler
    public void onJoinRegister(PlayerJoinEvent event){
        if(!config.C_LOBBYWORLD_BUILDMODE_ENABLED()) return;
        buildMode.put(event.getPlayer().getUniqueId(), false);
    }

    private boolean buildChecker(Player player){
        //Checks if the world is in the config, if it is checks if the player is in the hashmap, if it is it gets the value of the variable. If any of the before say false it shows true;
        return !(buildMode.containsKey(player.getUniqueId()) && buildMode.get(player.getUniqueId()))  ;
    }
}
