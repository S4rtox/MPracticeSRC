package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.tasks.ActiveArenaEvents;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
    public void onEnable(MMHunt plugin) {
        super.onEnable(plugin);
        // Stuff on start //
        arena.fillChests();
        alivePlayers.addAll(arena.getHunters());
        arena.updateAllScoreboards("&e&lMSkywars",
                "",
                "&fAlive: &a" + alivePlayers.size(),
                "",
                "&fRefill in: &7&l-",
                "",
                "&fArena: &a" + arena.getDisplayName(),
                "&fip.example.com"
        );
        alivePlayers.forEach(playerUUID -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null) player.setPlayerListName(Colorize.format("&7[&aA&7]" + player.getName()));
        });

        // EVENTS //
        arenaEvents = new ActiveArenaEvents(arena, () -> tryFinishGame(null), 60 * 5, 30, 2);
        arenaEvents.runTaskTimer(gameManager.getPlugin(),0,20);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (finishingState) {
                    cancel();
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

    @Override
    public GameState getGameStateEnum() {
        return GameState.ACTIVE;
    }

    @Override
    public void setDefaultPlayersStates() {

    }

    @Override
    public void setDefaultPlayerState(Player player) {

    }

    public void tryFinishGame(Player winner){
        if(!finishingState){
            finishingState = true;
            arena.setArenaState(new FinishingArenaState(gameManager, arena, winner));
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            if (killer != player) {
                //Code for the killer
                arena.sendAllMessage("&6" + player.getName() + " &cwas killed by " + killer.getName() + "&c.");
                killer.setHealth(killer.getMaxHealth());
                killer.giveExpLevels(3);
            } else {
                //Code if they commited suicide
                arena.sendAllMessage("&6" + player.getName() + " &cdecided to commit die");
            }
        } else {
            arena.sendAllMessage("&6" + player.getName() + " &cwas killed by mystical events.");
            //Code if there is no killer(void)
        }
        player.spigot().respawn();
        killPlayer(player);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            killPlayer(event.getPlayer());
            arena.sendAllMessage("&6" + event.getPlayer().getName() + " &cwas killed by mystical events.");
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void onKick (PlayerKickEvent event) {
        event.setLeaveMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            killPlayer(event.getPlayer());
            arena.sendAllMessage("&6" + event.getPlayer().getName() + " &cwas killed by mystical events.");
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void activeChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.getEveryone().forEach(playerUUID -> {
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
                player.setPlayerListName(Colorize.format("&7[&8D&7]" + player.getName()));
                alivePlayers.remove(player.getUniqueId());
                arena.updateAllScoreboardsLine(1,  "&fAlive: &a" + alivePlayers.size());
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(arena.getSpectatorSpawnLocation());
            }
        }
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }
}
