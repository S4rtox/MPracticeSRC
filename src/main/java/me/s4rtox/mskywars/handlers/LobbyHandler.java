package me.s4rtox.mskywars.handlers;

import me.s4rtox.mskywars.MSkywars;
import me.s4rtox.mskywars.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class LobbyHandler implements Listener {
    private final MSkywars plugin;
    private final ConfigUtil config;

    public LobbyHandler(MSkywars plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        config = plugin.getConfigUtil();
        setInitalWorldsGamerules();
    }

    @EventHandler
    public void disableHunger(FoodLevelChangeEvent event) {
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(event.getEntity().getWorld().getName())) return;
        if (!config.C_LOBBYWORLD_DISABLE_HUNGER()) return;
        event.setCancelled(true);

    }

    @EventHandler
    public void disableWeather(WeatherChangeEvent event) {
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(event.getWorld().getName())) return;
        if (!config.C_LOBBYWORLD_DISABLE_RAIN()) return;
        if (event.getWorld().hasStorm()) {
            event.getWorld().setStorm(false);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void disableFallDamage(EntityDamageEvent event){
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(event.getEntity().getWorld().getName())) return;
        if (!config.C_LOBBYWORLD_DISABLE_FALLDAMAGE()) return;
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    // [CHANGES]
    public void WorldGameruleSetter(WorldLoadEvent event) {
        setWorldGamerules(event.getWorld());
    }

    public void setWorldGamerules(World world) {
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(world.getName())) return;
        if (config.C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE()) {
            world.setGameRuleValue("doDaylightCycle", "false");
        } else {
            world.setGameRuleValue("doDaylightCycle", "true");
        }
        if (config.C_LOBBYWORLD_DISABLE_MOBSPAWNING()) {
            world.setGameRuleValue("doMobSpawning", "false");
        } else {
            world.setGameRuleValue("doMobSpawning", "false");
        }
        if (config.C_LOBBYWORLD_DISABLE_MOBGRIEFING()) {
            world.setGameRuleValue("doMobGriefing", "false");
        } else {
            world.setGameRuleValue("doMobGriefing", "false");
        }

        if (config.C_LOBBYWORLD_DISABLE_MOBSPAWNING()) {
            world.setGameRuleValue("doFireTick", "false");
        } else {
            world.setGameRuleValue("doFireTick", "false");
        }

        if (config.C_LOBBYWORLD_KEEPINVENTORY()) {
            world.setGameRuleValue("keepInventory", "true");
        } else {
            world.setGameRuleValue("keepInventory", "false");
        }
    }

    public void setInitalWorldsGamerules() {
        for (World world : plugin.getServer().getWorlds()) {
            if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(world.getName())) continue;
            setWorldGamerules(world);
        }
    }

    @EventHandler
    public void pvpDisabler(EntityDamageByEntityEvent event) {
        if (!config.C_LOBBYWORLD_DISABLE_PVP()) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!config.C_LOBBYWORLD_ENABLEDWORLDS().contains(event.getEntity().getWorld().getName())) return;
        if (!(event.getDamager() instanceof Player)) return;
        event.setCancelled(true);
    }
}
