package me.s4rtox.mpractice.handlers.gamehandlers;

import me.s4rtox.mpractice.MPractice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getServer;

public class WorldTablistManager implements Listener {
    private final MPractice plugin;

    public WorldTablistManager(MPractice plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    /**
     * Call the 'showAndHidePlayers(...)' method when a player joins the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        showAndHidePlayers(e.getPlayer());
    }

    /**
     * Call the 'showAndHidePlayers(...)' method when a player changes the world
     */
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        showAndHidePlayers(e.getPlayer());
    }

    /**
     * This method goes through every player on the server and checks if they are in the same world.
     * If not, both will be hidden from each other. Otherwise both will be shown each other.
     * Using the 'hidePlayer(...)' method because sending Packets would break custom/colored names in the TabList.
     */
    private void showAndHidePlayers(Player player) {
        getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (Player players : getServer().getOnlinePlayers()) {
                if (player.getWorld().equals(players.getWorld())) {
                    player.showPlayer(players);
                    players.showPlayer(player);
                } else {
                    player.hidePlayer(players);
                    players.hidePlayer(player);
                }
            }
        }, 1);
    }
}
