package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.tasks.ArenaFinishingTask;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class FinishingArenaState extends ArenaState {
    private final Player winningPlayer;

    public FinishingArenaState(GameManager gameManager, Arena arena, Player winningPlayer) {
        super(gameManager, arena);
        this.winningPlayer = winningPlayer;
    }

    @Override
    public void onEnable(MMHunt plugin) {
        super.onEnable(plugin);
        //If there is a player alive don't let them die to the void
        arena.updateAllScoreboards("&e&lMSkywars",
                "",
                "&6WINNER: &f",
                "",
                "&fGoing back in:",
                "",
                "&fArena: &a" + arena.getDisplayName(),
                "&fip.example.com"
        );
        if (winningPlayer != null) {
            arena.updateAllScoreboardsLine(1,  "&6WINNER: &f" + winningPlayer.getName());
            winningPlayer.setFoodLevel(20);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arena == null || arena.getArenaState().getGameStateEnum() != GameState.FINISHING) {
                        cancel();
                    }
                    if (winningPlayer.getLocation().getBlockY() <= 0) {
                        winningPlayer.teleport(arena.getCenterLocation().clone().add(0, 1, 0));
                    }
                }
            }.runTaskTimer(plugin, 0, 8);
        }
        //Start the task to make it go to arena
        new ArenaFinishingTask(arena, () -> {
            // Ugly unregister event as if it gets teleported it just pulls a iterator error as the list gets
            // Shortenned
            // CHange this to a normal for each with a null check? that way iterator error is avoided and I can just
            // do this normally instead of this ugly aproach
            // Will do tomorrow
            // Probably
            // Yeah probably not happening :)
            PlayerQuitEvent.getHandlerList().unregister(this);
            PlayerKickEvent.getHandlerList().unregister(this);
            arena.resetScoreboards();
            for (Iterator<UUID> i = arena.getEveryone().iterator(); i.hasNext();) {
                Player player = Bukkit.getPlayer(i.next());
                if (player != null) {
                    arena.sendToLobby(player);
                }
            }
            arena.getHunters().clear();
            arena.getSpectators().clear();
            arena.getEveryone().clear();
            arena.clearChests();
            arena.setArenaState(new WaitingArenaState(gameManager, arena));
        }, 10, winningPlayer).runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena has already started!"));
    }

    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public GameState getGameStateEnum() {
        return GameState.FINISHING;
    }

    @Override
    public void setDefaultPlayersStates() {
    }

    @Override
    public void setDefaultPlayerState(Player player) {

    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            arena.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    private void onKick (PlayerKickEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void finishingChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.getEveryone().forEach(playerUUID -> {
                Player arenaPlayer = Bukkit.getPlayer(playerUUID);
                if(arenaPlayer != null){
                    event.getRecipients().add(arenaPlayer);
                }
            });
            if (winningPlayer.equals(player)) {
                event.setFormat(Colorize.format("&7[&6WINNER&7]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            } else if (arena.isPlaying(player)) {
                event.setFormat(Colorize.format("&7[DEAD]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            } else if (arena.isSpectating(player)) {
                event.setFormat(Colorize.format("&7[SPECTATING]&f "+ player.getDisplayName() + "&7:&f ") + event.getMessage());
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
