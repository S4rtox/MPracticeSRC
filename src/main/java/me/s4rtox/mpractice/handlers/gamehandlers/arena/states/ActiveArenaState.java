package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.tasks.ActiveArenaEvents;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActiveArenaState extends ArenaState {
    private final List<UUID> alivePlayers = new ArrayList<>();
    private ActiveArenaEvents arenaEvents;
    private boolean finishingState = false;

    public ActiveArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onEnable(MPractice plugin) {
        super.onEnable(plugin);
        arena.fillChests();
        alivePlayers.addAll(arena.players());
        arenaEvents = new ActiveArenaEvents(arena, () -> tryFinishGame(null), 60 * 5, 30, 2);
        arenaEvents.runTaskTimer(gameManager.plugin(),0,20);
        //Code when they fall to the void
        // This is just a safeguard, as if for some reason the two threads manage to finish at
        // The same time it can cause problems.
        // Dont think it'll be ever be executed, but there is a chance, and I aint making no mistakes
        // if you are sure this just straight up doesnt work or know smth better just remove and change the finishing arena state.
        //If the alive arena is empty, meaning that the last players died at the same time(in 8 ticks time)
        //Meaning we got a tie, also if for some reason the last player ends up being null(Shouldnt happen, just in case
        BukkitTask arenaStatesChecker = new BukkitRunnable() {
            @Override
            public void run() {
                if (finishingState) {
                    cancel();
                }
                for (int i = alivePlayers.size() - 1; i >= 0; --i) {
                    Player player = Bukkit.getPlayer(alivePlayers.get(i));
                    if (player != null) {
                        if (player.getLocation().getBlockY() <= 0) {
                            //Code when they fall to the void
                            killPlayer(player);
                        }
                    }
                }
                if (alivePlayers.size() <= 1) {
                    // This is just a safeguard, as if for some reason the two threads manage to finish at
                    // The same time it can cause problems.
                    // Dont think it'll be ever be executed, but there is a chance, and I aint making no mistakes
                    // if you are sure this just straight up doesnt work or know smth better just remove and change the finishing arena state.
                    //If the alive arena is empty, meaning that the last players died at the same time(in 8 ticks time)
                    //Meaning we got a tie, also if for some reason the last player ends up being null(Shouldnt happen, just in case)
                    if (!finishingState) {
                        arenaEvents.cancel();
                    }
                    if (alivePlayers.isEmpty()) {
                        tryFinishGame(null);
                    } else {
                        Player lastAlivePlayer = Bukkit.getPlayer(alivePlayers.get(0));
                        tryFinishGame(lastAlivePlayer);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 8);
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena has already started!"));
    }

    @Override
    public void onPlayerLeave(Player player) {
        killPlayer(player);
        super.onPlayerLeave(player);
    }

    public void tryFinishGame(Player winner){
        if(!finishingState){
            finishingState = true;
            arena.setArenaState(new FinishingArenaState(gameManager, arena, winner));
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.sendMessage("This got executed");
        player.spigot().respawn();
        killPlayer(player);
        Player killer = player.getKiller();
        if (killer != null) {
            if (killer != player) {
                //Code for the killer
                arena.sendPlayersMessage("&6" + player.getName() + " &cwas killed by " + killer.getName() + "&c.");
                killer.setHealth(killer.getMaxHealth());
                killer.giveExpLevels(3);
            } else {
                //Code if they commited suicide
                arena.sendPlayersMessage("&6" + player.getName() + " &cdecided to commit die");
            }
        } else {
            arena.sendPlayersMessage("&6" + player.getName() + " &cwas killed by mystical events.");
            //Code if there is no killer(void)
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        killPlayer(event.getPlayer());
        arena.sendPlayersMessage("&6" + event.getPlayer().getName() + " &cwas killed by mystical events.");
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void onKick (PlayerKickEvent event) {
        killPlayer(event.getPlayer());
        arena.sendPlayersMessage("&6" + event.getPlayer().getName() + " &cwas killed by mystical events.");
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void activeChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.allPlayers().forEach(playerUUID -> {
                Player arenaPlayer = Bukkit.getPlayer(playerUUID);
                if(arenaPlayer != null){
                    event.getRecipients().add(arenaPlayer);
                }
            });
            if (alivePlayers.contains(player.getUniqueId())) {
                event.setFormat(Colorize.format("&7[&aALIVE&7]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            } else if (arena.isPlaying(player)) {
                event.setFormat(Colorize.format("&7[DEAD]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            } else if (arena.isSpectating(player)) {
                event.setFormat(Colorize.format("&7[SPECTATING]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
            }
        }
    }

    public void killPlayer(Player player) {
        if (player != null) {
            if (alivePlayers.contains(player.getUniqueId())) {
                alivePlayers.remove(player.getUniqueId());
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(arena.spectatorSpawnLocation());
            }
        }
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }
}
