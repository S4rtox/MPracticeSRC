package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.PlayerRollbackManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.tasks.ArenaFinishingTask;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FinishingArenaState extends ArenaState {
    private final Player winningPlayer;

    public FinishingArenaState(GameManager gameManager, Arena arena, Player winningPlayer) {
        super(gameManager, arena);
        this.winningPlayer = winningPlayer;
    }

    @Override
    public void onEnable(MPractice plugin) {
        super.onEnable(plugin);
        //If there is a player alive don't let them die to the void
        if (winningPlayer != null) {
            winningPlayer.setFoodLevel(20);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arena == null || !(arena.arenaState() instanceof FinishingArenaState)) {
                        cancel();
                    }
                    if (winningPlayer.getLocation().getBlockY() <= 0) {
                        winningPlayer.teleport(arena.centerLocation().clone().add(0, 1, 0));
                    }
                }
            }.runTaskTimer(plugin, 0, 8);
        }
        //Start the task to make it go to arena
        List<UUID> allPlayers = new ArrayList<>(arena.players());
        allPlayers.addAll(arena.spectators());
        new ArenaFinishingTask(arena, () -> {
            for (UUID playerUUID : allPlayers) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    plugin.getSpawnSetter().teleport(player);
                    PlayerRollbackManager.restore(player, false);
                }
            }
            arena.players().clear();
            arena.spectators().clear();
            arena.allPlayers().clear();
            arena.setArenaState(new WaitingArenaState(gameManager, arena));
        }, 10, winningPlayer).runTaskTimer(plugin, 0, 20);
    }


    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            arena.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            if (!event.getPlayer().getWorld().getName().equals(arena.world().getName())) {
                arena.removePlayer(event.getPlayer());
            }
        }
    }
    @EventHandler
    private void finishingChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.allPlayers().forEach(playerUUID -> {
                Player arenaPlayer = Bukkit.getPlayer(playerUUID);
                if(arenaPlayer != null){
                    event.getRecipients().add(arenaPlayer);
                }
            });
            if (winningPlayer.equals(player)) {
                event.setFormat(Colorize.format("&7[&6WINNER&7]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            } else if (arena.isPlaying(player)) {
                event.setFormat(Colorize.format("&7[DEAD]&f  " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            } else if (arena.isSpectating(player)) {
                event.setFormat(Colorize.format("&7[SPECTATING]&f  " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            }
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }
}
