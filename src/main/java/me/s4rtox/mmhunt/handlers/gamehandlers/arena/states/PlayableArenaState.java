package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PlayableArenaState extends ArenaState implements Listener {
    protected final List<UUID> alivePlayers;
    public static final String RUNNER = "&7[&aR&7]&f ";
    public static final String HUNTER = "&7[&cH&7]&7 ";
    public static final String DEAD = "&7[&0D&7]&7 ";
    public static final String SPECTATOR = "&7[&fSPEC]&7 ";
    protected PlayableArenaState(GameManager gameManager, Arena arena, List<UUID> alivePlayers) {
        super(gameManager, arena);
        this.alivePlayers = alivePlayers;
    }

    protected PlayableArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
        this.alivePlayers = new ArrayList<>();
    }

    @Override
    public void setDefaultPlayerState(Player player) {
        addAlivePlayer(player.getUniqueId());
        player.setPlayerListName(Colorize.format(HUNTER + player.getName()));
        gameManager.getTrackerHandler().setTarget(player, arena.getRunner());
    }


    public void killPlayer(Player player) {
        if (player != null) {
            if (alivePlayers.contains(player.getUniqueId())) {
                player.setPlayerListName(Colorize.format(ActiveArenaState.DEAD + player.getName()));
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(arena.getSpectatorSpawnLocation());
                alivePlayers.remove(player.getUniqueId());
                if(arena.isRunner(player)){
                    arena.sendAllSound(Sound.ENTITY_ENDER_DRAGON_DEATH,1,1);
                    arena.sendAllTitle("&aHunters WIN!", "", 20, 20*5 , 20);
                    tryFinishGame();
                }
            }
        }
    }

    public boolean revivePlayer(Player player){
        if(arena.getPlayers().contains(player.getUniqueId())){
            player.setGameMode(GameMode.SURVIVAL);
            addAlivePlayer(player.getUniqueId());
            return true;
        }
        return false;
    }


    public void tryFinishGame(){
        arena.setArenaState(new FinishingArenaState(gameManager, arena));
        try {
            cancelArenaEvents();
        }catch (Exception ignored){}
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            if (killer != player) {
                //Code for the killer
                arena.sendAllMessage("&6" + player.getName() + " &cwas killed by &6" + killer.getName() + "&c.");
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

    @Override
    public void onPlayerLeave(Player player) {
        killPlayer(player);
        super.onPlayerLeave(player);
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        setDefaultPlayerState(player);
    }

    @Override
    public void onSpectatorJoin(Player player) {
        super.onSpectatorJoin(player);
        player.setPlayerListName(Colorize.format(SPECTATOR + player.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            killPlayer(event.getPlayer());
            arena.sendAllMessage("&6" + event.getPlayer().getName() + " &cwas killed by mystical events.");
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    public void onKick (PlayerKickEvent event) {
        event.setLeaveMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            killPlayer(event.getPlayer());
            arena.sendAllMessage("&6" + event.getPlayer().getName() + " &cwas killed by mystical events.");
            arena.sendToLobby(event.getPlayer());
        }
    }

    //TODO: buscar como juntar los 3 tipos de tipos activos
    @EventHandler
    public void activeChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.doAllAction(player1 -> event.getRecipients().add(player1));
            if(arena.isSpectating(player)){
                event.setFormat(Colorize.format(SPECTATOR + player.getDisplayName() + "&7:&7 ") + event.getMessage());
            }else if(!alivePlayers.contains(player.getUniqueId())){
                // event.setFormat(Colorize.format(DEAD + player.getDisplayName() + "&7:&7 ") + event.getMessage());
                event.setCancelled(true);
            }else if(arena.isRunner(player)){
                event.setFormat(Colorize.format(RUNNER + player.getDisplayName() + "&7:&f ") + event.getMessage());
            }else{
                event.setFormat(Colorize.format(HUNTER + player.getDisplayName() + "&7: ") + event.getMessage());
            }
        }
    }

    @EventHandler
    public void avoidTeamDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager){
            if(arena.isInGame(player) && arena.isInGame(damager)){
                if(arena.isHunter(player) && arena.isHunter(damager)){
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void setDefaultPlayersStates() {
        arena.doHunterAction(player -> {
            player.setPlayerListName(Colorize.format(ActiveArenaState.HUNTER + player.getName()));
            addAlivePlayer(player.getUniqueId());
            gameManager.getTrackerHandler().setTarget(player, arena.getRunner());
        });
        arena.doRunnerAction(player -> player.setPlayerListName(Colorize.format(ActiveArenaState.RUNNER + player.getName())));
    }

    public void addAlivePlayer(UUID player){
        if(!alivePlayers.contains(player)){
            alivePlayers.add(player);
        }
    }

    public abstract void cancelArenaEvents();

    @Override
    public GameState getGameStateEnum() {
        return GameState.ACTIVE;
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

}
