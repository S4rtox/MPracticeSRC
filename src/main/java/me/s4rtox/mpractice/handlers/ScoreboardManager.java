package me.s4rtox.mpractice.handlers;

import fr.mrmicky.fastboard.FastBoard;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

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


    public void updateScoreboard(Player player, String title, String... lines){
        FastBoard scoreboard = scoreboards.get(player.getUniqueId());
        List<String> coloredLines = new ArrayList<>();
        for (String line : lines){
            coloredLines.add(Colorize.format(line));
        }
        scoreboard.updateTitle(Colorize.format(title));
        scoreboard.updateLines(coloredLines);
    }

    public void updateScoreboard(Player player, String title, Collection<String> lines){
        FastBoard scoreboard = scoreboards.get(player.getUniqueId());
        scoreboard.updateTitle(title);
        scoreboard.updateLines(lines);
    }

    public void updateLine(Player player, int line, String text){
        text = Colorize.format(text);
       scoreboards.get(player.getUniqueId()).updateLine(line, text);
    }

    public void resetScoreboard(Player player){
        scoreboards.replace(player.getUniqueId(), new FastBoard(player));
    }
}
