package me.s4rtox.mpractice.handlers.gamehandlers.arena;

import com.grinderwolf.swm.api.world.SlimeWorld;
import lombok.Data;
import lombok.NonNull;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemporaryArena {
    private final GameManager gameManager;
    private String name;
    private String displayName;
    private Location centerLocation;
    private Location corner1Location;
    private Location corner2Location;
    private Location spectatorSpawnLocation;
    private SlimeWorld slimeWorld;
    private List<Location> spawnLocations = new ArrayList<>();
    private List<Location> islandChests = new ArrayList<>();
    private List<Location> middleChests = new ArrayList<>();

    public TemporaryArena(@NonNull GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public TemporaryArena(@NonNull GameManager gameManager, Arena arena) {
        this.gameManager = gameManager;
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

    public void addSpawnLocation(Location location) {
        this.spawnLocations.add(location);
    }

    public void addIslandChest(Location location) {
        this.islandChests.add(location);
    }

    public void addMiddleChest(Location location) {
        this.middleChests.add(location);
    }

    public void removeLastSpawnLocation() {
        if (spawnLocations.isEmpty()) {
            return;
        }
        this.spawnLocations.remove(spawnLocations.size() - 1);
    }

    public void removeLastIslandChest() {
        if (islandChests.isEmpty()) {
            return;
        }
        this.islandChests.remove(islandChests.size() - 1);
    }

    public void removeLastMiddleChest() {
        if (middleChests.isEmpty()) {
            return;
        }
        this.middleChests.remove(middleChests.size() - 1);
    }


    public Arena toArena() {
        return new Arena(gameManager, this.name, this.displayName, this.centerLocation, this.corner1Location, this.corner2Location, this.spectatorSpawnLocation, this.spawnLocations, this.islandChests, this.middleChests);
    }
}
