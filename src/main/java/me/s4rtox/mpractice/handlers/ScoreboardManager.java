package me.s4rtox.mpractice.handlers;

import fr.mrmicky.fastboard.FastBoard;
import me.s4rtox.mpractice.MPractice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ScoreboardManager implements Listener {
    private final Map<UUID, FastBoard> scoreboards = new HashMap<>();

    public ScoreboardManager(MPractice plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        scoreboards.putIfAbsent(event.getPlayer().getUniqueId(), new FastBoard(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        FastBoard board =  this.scoreboards.remove(event.getPlayer().getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){
        FastBoard board =  this.scoreboards.remove(event.getPlayer().getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public FastBoard getPlayerScoreboard(Player player){
        return scoreboards.get(player.getUniqueId());
    }

    public void setDefaultScoreboard(Player player){
        FastBoard scoreboard = scoreboards.get(player.getUniqueId());
        scoreboard.updateLine(scoreboard.size() - 1, "Test");
    }
}
