package me.s4rtox.mskywars.util;

import me.s4rtox.mskywars.MSkywars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.IOException;
import java.util.logging.Level;

public class SpawnUtil implements Listener {
    private Location spawn;
    private final ConfigUtil config;
    private final MSkywars plugin;

    public SpawnUtil (MSkywars plugin){
        this.plugin = plugin;
        config = plugin.getConfigUtil();
        Bukkit.getPluginManager().registerEvents(this,plugin);
        String worldName = plugin.getSpawnConfig().getString("world");
        double x = plugin.getSpawnConfig().getDouble("x");
        double y = plugin.getSpawnConfig().getDouble("y");
        double z = plugin.getSpawnConfig().getDouble("z");
        float yaw = plugin.getSpawnConfig().getFloat("yaw");
        float pitch = plugin.getSpawnConfig().getFloat("pitch");
        //Checa si hay un valor en la config de el nombre de mundo
        if(worldName != null){
            //Pone una variable de tipo world(Tiene todos los datos de el mundo, y utiliza el nombre del mundo para almacenarlo (El nombre lo saca de la config))
            World world = Bukkit.getWorld(worldName);
            //Checa si hay un mundo con el nombre de la config, si no tira lo de abajo.
            if(world == null){
                Bukkit.getLogger().log(Level.SEVERE, "The world \"" + worldName + "\" does not exist.");
                return;
            }

            spawn = new Location(world,x,y,z,yaw,pitch);

        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(spawn != null){
            event.setRespawnLocation(spawn);
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.teleport(spawn);
    }

    public void teleport(Player player){
        if(spawn == null){
            player.sendMessage(config.MS_SPAWN_NOT_SET_YET());
            return;
        }

        player.teleport(spawn);
    }

    public void set(Location Spawn){
        this.spawn = Spawn;

        String worldName = spawn.getWorld().getName();
        double x = spawn.getX();
        double y = spawn.getY();
        double z = spawn.getZ();
        double yaw = spawn.getYaw();
        double pitch = spawn.getPitch();

        plugin.getSpawnConfig().set("world", worldName);
        plugin.getSpawnConfig().set("x", x);
        plugin.getSpawnConfig().set("y", y);
        plugin.getSpawnConfig().set("z", z);
        plugin.getSpawnConfig().set("yaw", yaw);
        plugin.getSpawnConfig().set("pitch", pitch);
        try {
            plugin.getSpawnConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
