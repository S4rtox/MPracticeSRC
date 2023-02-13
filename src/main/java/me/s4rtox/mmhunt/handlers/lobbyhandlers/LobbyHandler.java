package me.s4rtox.mmhunt.handlers.lobbyhandlers;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.config.ConfigManager;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LobbyHandler implements Listener {
    private final MMHunt plugin;
    private final ConfigManager config;
    private final Set<UUID> lobbyPlayers = new HashSet<>();

    public LobbyHandler(MMHunt plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        config = plugin.getConfigManager();
        setInitalWorldsGamerules();
    }

    @EventHandler
    public void disableHunger(FoodLevelChangeEvent event) {
        if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(event.getEntity().getWorld().getName())) return;
        if (!config.isC_LOBBYWORLD_DISABLE_HUNGER()) return;
        event.setCancelled(true);

    }

    @EventHandler
    public void disableWeather(WeatherChangeEvent event) {
        if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(event.getWorld().getName())) return;
        if (!config.isC_LOBBYWORLD_DISABLE_RAIN()) return;
        if (event.getWorld().hasStorm()) {
            event.getWorld().setStorm(false);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void disableFallDamage(EntityDamageEvent event) {
        if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(event.getEntity().getWorld().getName())) return;
        if (!config.isC_LOBBYWORLD_DISABLE_FALLDAMAGE()) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    // [CHANGES]
    public void WorldGameruleSetter(WorldLoadEvent event) {
        setWorldGamerules(event.getWorld());
    }

    public void setWorldGamerules(World world) {
        if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(world.getName())) return;
        if (config.isC_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE()) {
            world.setGameRuleValue("doDaylightCycle", "false");
        } else {
            world.setGameRuleValue("doDaylightCycle", "true");
        }
        if (config.isC_LOBBYWORLD_DISABLE_MOBSPAWNING()) {
            world.setGameRuleValue("doMobSpawning", "false");
        } else {
            world.setGameRuleValue("doMobSpawning", "false");
        }
        if (config.isC_LOBBYWORLD_DISABLE_MOBGRIEFING()) {
            world.setGameRuleValue("doMobGriefing", "false");
        } else {
            world.setGameRuleValue("doMobGriefing", "false");
        }

        if (config.isC_LOBBYWORLD_DISABLE_MOBSPAWNING()) {
            world.setGameRuleValue("doFireTick", "false");
        } else {
            world.setGameRuleValue("doFireTick", "false");
        }

        if (config.isC_LOBBYWORLD_KEEPINVENTORY()) {
            world.setGameRuleValue("keepInventory", "true");
        } else {
            world.setGameRuleValue("keepInventory", "false");
        }
    }

    public void setInitalWorldsGamerules() {
        for (World world : plugin.getServer().getWorlds()) {
            if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(world.getName())) continue;
            setWorldGamerules(world);
        }
    }

    @EventHandler
    public void pvpDisabler(EntityDamageByEntityEvent event) {
        if (!config.isC_LOBBYWORLD_DISABLE_PVP()) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(event.getEntity().getWorld().getName())) return;
        if (!(event.getDamager() instanceof Player)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        lobbyPlayers.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event){
        lobbyPlayers.remove(event.getPlayer().getUniqueId());
    }

    //OPTIONAL
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event){
        if(config.getC_LOBBYWORLD_ENABLEDWORLDS().contains(event.getPlayer().getWorld().getName())){
            lobbyPlayers.add(event.getPlayer().getUniqueId());
        }else{
            lobbyPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void LobbyChatHandler(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (lobbyPlayers.contains(player.getUniqueId())) {
            event.getRecipients().clear();
            lobbyPlayers.forEach(playerUUID -> {
                Player arenaPlayer = Bukkit.getPlayer(playerUUID);
                if(arenaPlayer != null){
                    event.getRecipients().add(arenaPlayer);
                }
            });
            event.setFormat(Colorize.format("&f" + player.getDisplayName() + "&7:&f ") + event.getMessage());
        }
    }

    @EventHandler
    public void disableJoinMessage(PlayerJoinEvent event){
        event.setJoinMessage("");
    }
    @EventHandler
    public void disableQuitMessage(PlayerQuitEvent event){
        event.setQuitMessage("");
    }

    @EventHandler
    public void disableKickMessage(PlayerKickEvent event){
        event.setLeaveMessage("");
    }

    public Set<UUID> getLobbyPlayers(){
        return lobbyPlayers;
    }
    public void removeLobbyPlayer(Player player){
        lobbyPlayers.remove(player.getUniqueId());
    }

    public void addLobbyPlayer(Player player){
        lobbyPlayers.add(player.getUniqueId());
    }

}
