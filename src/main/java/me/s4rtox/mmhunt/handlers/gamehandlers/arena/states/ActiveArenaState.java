package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.config.ConfigManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks.ActiveArenaEvents;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActiveArenaState extends ArenaState {
    private final List<UUID> alivePlayers = new ArrayList<>();
    private ActiveArenaEvents arenaEvents;
    private boolean finishingState = false;
    public static final String RUNNER = "&7[&aR&7]&f ";
    public static final String HUNTER = "&7[&cH&7]&f ";
    public static final String DEAD = "&7[&0D&7]&f ";
    public static final String SPECTATOR = "&7[SPEC]&f ";


    public ActiveArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onEnable(MMHunt plugin) {
        super.onEnable(plugin);
        // Stuff on start //
        arena.fillChests();
        setDefaultPlayersStates();

        // EVENTS //
        arenaEvents = new ActiveArenaEvents(this);
        arenaEvents.runTaskTimer(gameManager.getPlugin(),0,20);

    }


    @Override
    public void onPlayerLeave(Player player) {
        killPlayer(player);
        super.onPlayerLeave(player);
    }


    @Override
    public void setDefaultPlayersStates() {
        alivePlayers.addAll(arena.getHunters());
        arena.updateAllScoreboards("&e&lMMHunt",
                "",
                "&fAlive: &a" + alivePlayers.size(),
                "",
                "&fNext Event:",
                "&fIn:",
                "&fArena: &a" + arena.getDisplayName(),
                "&f" + ConfigManager.serverIP
        );
        arena.doHunterAction(player -> player.setPlayerListName(Colorize.format(ActiveArenaState.HUNTER + player.getName())));
        arena.doRunnerAction(player -> player.setPlayerListName(Colorize.format(ActiveArenaState.RUNNER + player.getName())));
    }

    @Override
    public void setDefaultPlayerState(Player player) {
        alivePlayers.add(player.getUniqueId());
        arena.doHunterAction(p -> p.setPlayerListName(Colorize.format(ActiveArenaState.HUNTER + p.getName())));
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

    public void killPlayer(Player player) {
        if (player != null) {
            if (alivePlayers.contains(player.getUniqueId())) {
                player.setPlayerListName(Colorize.format(ActiveArenaState.DEAD + player.getName()));
                alivePlayers.remove(player.getUniqueId());
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(arena.getSpectatorSpawnLocation());
            }
        }
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
            arena.doAllAction(player1 -> event.getRecipients().add(player1));
            if(arena.isSpectating(player)){
                event.setFormat(Colorize.format(ActiveArenaState.SPECTATOR + player.getDisplayName() + "&7:&7 ") + event.getMessage());
            }else if(!alivePlayers.contains(player.getUniqueId())){
                event.setFormat(Colorize.format(ActiveArenaState.DEAD + player.getDisplayName() + "&7:&7 ") + event.getMessage());

            }else if(arena.isRunner(player)){
                event.setFormat(Colorize.format(ActiveArenaState.RUNNER + player.getDisplayName() + "&7:&f ") + event.getMessage());
            }else{
                event.setFormat(Colorize.format(ActiveArenaState.HUNTER + player.getDisplayName() + "&7: ") + event.getMessage());
            }
        }
    }

    public void tryFinishGame(){
        if(!finishingState){
            finishingState = true;
            arena.setArenaState(new FinishingArenaState(gameManager, arena));
        }
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    @Override
    public GameState getGameStateEnum() {
        return GameState.ACTIVE;
    }
}
