package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ArenaState implements Listener {
    protected final GameManager gameManager;
    protected final Arena arena;

    protected ArenaState(GameManager gameManager, Arena arena) {
        this.gameManager = gameManager;
        this.arena = arena;
    }

    public void onEnable(MMHunt plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable(MMHunt plugin) {
        HandlerList.unregisterAll(this);
    }

    /**
     * Fires every time a player joins an arena, setting the default
     * state of the player.
     * @param player
     * Player joining
     */
    public void onPlayerJoin(Player player){
        gameManager.getArenaManager().setPlayerArena(player,arena);
        arena.getHunters().add(player.getUniqueId());
        setPlayerJoinState(player);
    }

    /**
     * Fires every time a player leaves an arena, restoring the player
     * items and removing the scoreboard
     * @param player
     * Player leaving the arena
     */
    public void onPlayerLeave(Player player){
        player.setPlayerListName(player.getName());
        gameManager.getArenaManager().removeFromArena(player);
        arena.getHunters().remove(player.getUniqueId());
        arena.getSpectators().remove(player.getUniqueId());
        gameManager.getRollbackManager().restore(player, false);
        gameManager.getPlugin().getScoreboardManager().resetScoreboard(player);
    }

    /**
     * Fires every time a player leaves an arena, restoring the player
     * items and removing the scoreboard
     * @param player
     * Player leaving the arena
     */
    public void onSpectatorJoin(Player player){
        player.setPlayerListName(Colorize.format("&7[&fS&7]" + player.getName()));
        arena.getSpectators().add(player.getUniqueId());
        setSpectatorJoinState(player);
    }

    public abstract GameState getGameStateEnum();

    public abstract void setDefaultPlayersStates();
    public abstract void setDefaultPlayerState(Player player);


    //TODO: teleport players to waiting lobby
    private void setPlayerJoinState(Player player){
        gameManager.getRollbackManager().save(player);
        player.getInventory().clear();
        player.getInventory();
        player.getEquipment().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        gameManager.getPlugin().getLobbyHandler().removeLobbyPlayer(player);
        player.setPlayerListName(Colorize.format("&7[&eW&7]&f" + player.getName()));
    }

    private void setSpectatorJoinState(Player player){
        gameManager.getRollbackManager().save(player);
        player.teleport(arena.getSpectatorSpawnLocation());
        player.getInventory().clear();
        player.getEquipment().clear();
        player.setGameMode(GameMode.SPECTATOR);
        gameManager.getPlugin().getLobbyHandler().removeLobbyPlayer(player);
    }


}
