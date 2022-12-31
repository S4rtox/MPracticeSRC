package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
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

    public void onEnable(MPractice plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable(MPractice plugin) {
        HandlerList.unregisterAll(this);
    }

    public void onPlayerJoin(Player player){
        arena.players().add(player.getUniqueId());
        arena.allPlayers().add(player.getUniqueId());
        setPlayerJoinState(player);
    }

    public void onPlayerLeave(Player player){
        arena.players().remove(player.getUniqueId());
        arena.allPlayers().remove(player.getUniqueId());
        arena.spectators().remove(player.getUniqueId());
        gameManager.rollbackManager().restore(player, false);
    }

    public void onSpectatorJoin(Player player){
        arena.spectators().add(player.getUniqueId());
        arena.allPlayers().add(player.getUniqueId());
        setSpectatorJoinState(player);
    }

    private void setPlayerJoinState(Player player){
        gameManager.rollbackManager().save(player);
        player.teleport(arena.spawnLocations().get(arena.players().indexOf(player.getUniqueId())));
        player.getInventory().clear();
        player.getInventory();
        player.getEquipment().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        gameManager.plugin().getLobbyHandler().removeLobbyPlayer(player);

    }

    private void setSpectatorJoinState(Player player){
        gameManager.rollbackManager().save(player);
        player.teleport(arena.spectatorSpawnLocation());
        player.getInventory().clear();
        player.getEquipment().clear();
        player.setGameMode(GameMode.SPECTATOR);
        gameManager.plugin().getLobbyHandler().removeLobbyPlayer(player);
    }


}
