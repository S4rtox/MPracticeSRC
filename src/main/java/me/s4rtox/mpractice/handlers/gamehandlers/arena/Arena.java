package me.s4rtox.mpractice.handlers.gamehandlers.arena;

import lombok.Data;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.ArenaState;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.StartingArenaState;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
public class Arena {
    private String name;
    private String displayName;

    private int maxPlayers;
    private Location centerLocation;
    private Location corner1Location;
    private Location corner2Location;
    private Location spectatorSpawnLocation;
    private List<Location> spawnLocations;
    private List<Location> islandChests;
    private List<Location> middleChests;

    private List<UUID> activePlayers;
    private List<UUID> spectators;

    private ArenaState arenaState;

    public Arena(
            String name,
            String displayName,
            Location centerLocation ,
            Location corner1Location ,
            Location corner2Location,
            Location spectatorSpawnLocation,
            List<Location> spawnLocations,
            List<Location> islandChests,
            List<Location> middleChests
    ){

        this.name = name;
        this.displayName = displayName;
        this.centerLocation = centerLocation;
        this.corner1Location = corner1Location;
        this.corner2Location = corner2Location;
        this.spectatorSpawnLocation = spectatorSpawnLocation;
        this.spawnLocations = spawnLocations;
        this.maxPlayers = spawnLocations.size();
        this.islandChests = islandChests;
        this.middleChests = middleChests;
    }

    public void addPlayer(Player player){
        activePlayers.add(player.getUniqueId());
        if(activePlayers.size() > (maxPlayers / 2) && !(arenaState instanceof StartingArenaState)){
            arenaState(new StartingArenaState(this));
        }
    }

    public void removePlayer(Player player){
        activePlayers.remove(player.getUniqueId());
        if(activePlayers.size() < (maxPlayers/2) && arenaState instanceof StartingArenaState) {
            StartingArenaState startingArenaState = (StartingArenaState) arenaState;
            startingArenaState.arenaStartingTask().cancel();

            sendArenaMessage("&cNot enough players!, Start cancelled");
        }

    }

    public boolean isPlaying(Player player){
        return activePlayers.contains(player.getUniqueId());
    }

    public void sendArenaMessage(String message){
        message = Colorize.format(message);
        for (UUID playerUUID : this.activePlayers()){
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null) player.sendMessage(message);
        }
    }









}
