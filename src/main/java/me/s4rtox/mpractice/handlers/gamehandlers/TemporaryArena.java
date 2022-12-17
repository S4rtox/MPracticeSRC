package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemporaryArena {

    private String name;
    private String displayName;
    private Location centerLocation;
    private Location corner1Location;
    private Location corner2Location;
    private Location spectatorSpawnLocation;
    private List<Location> spawnLocations = new ArrayList<>();
    private List<Location> islandChests = new ArrayList<>();
    private List<Location> middleChests = new ArrayList<>();
    public TemporaryArena(){

    }

    public TemporaryArena(Arena arena){
        this.name = arena.name();
        this.displayName = arena.displayName();
        this.centerLocation = arena.centerLocation();
        this.corner1Location = arena.corner1Location();
        this.corner2Location = arena.corner2Location();
        this.spectatorSpawnLocation = arena.spectatorSpawnLocation();
        this.spawnLocations = arena.spawnLocations();
        this.islandChests = arena.islandChests();
        this.middleChests = arena.middleChests();
    }

    public void addSpawnLocation(Location location){
        this.spawnLocations.add(location);
    }

    public void addIslandChest(Location location){
        this.islandChests.add(location);
    }

    public void addMiddleChest(Location location){
        this.middleChests.add(location);
    }


    public Arena toArena(){
        return new Arena(name,displayName,centerLocation,corner1Location,corner2Location,spectatorSpawnLocation,spawnLocations,islandChests,middleChests);
    }
}
