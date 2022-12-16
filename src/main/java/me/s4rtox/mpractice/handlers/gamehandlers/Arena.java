package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

@Data
public class Arena {
    private String name;
    private Location centerLocation;
    private Location corner1Location;
    private Location corner2Location;
    private Location spectatorSpawnLocation;
    private List<Location> spawnLocations;
    private List<Location> islandChests;
    private List<Location> centerChests;

    private List<UUID> activePlayers;
    private List<UUID> spectators;

    public Arena(String name,
                 Location centerLocation ,
                 Location corner1Location ,
                 Location corner2Location,
                 Location spectatorSpawnLocation,
                 List<Location> spawnLocations,
                 List<Location> islandChests,
                 List<Location> centerChests){

        this.name = name;
        this.centerLocation = centerLocation;
        this.corner1Location = corner1Location;
        this.corner2Location = corner2Location;
        this.spectatorSpawnLocation = spectatorSpawnLocation;
        this.spawnLocations = spawnLocations;
        this.islandChests = islandChests;
        this.centerChests = centerChests;
    }

    public void addPlayer(Player player){
        activePlayers.add(player.getUniqueId());
    }

    public void removePlayer(Player player){
        activePlayers.add(player.getUniqueId());
    }

    public boolean isPlaying(Player player){
        return activePlayers.contains(player.getUniqueId());
    }









}
