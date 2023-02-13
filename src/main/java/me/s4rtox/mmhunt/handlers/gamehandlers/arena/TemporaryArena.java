package me.s4rtox.mmhunt.handlers.gamehandlers.arena;

import lombok.Data;
import lombok.NonNull;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemporaryArena {
    private final GameManager gameManager;
    private String name;
    private String displayName;
    private Location centerLocation;
    private int worldBorderRadius;
    private Location spectatorSpawnLocation;
    private Location spawnLocation;
    private Location waitingLobby;

    private List<Location> chest = new ArrayList<>();


    public TemporaryArena(@NonNull GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public TemporaryArena(@NonNull GameManager gameManager, Arena arena) {
        this.gameManager = gameManager;
        this.name = arena.getName();
        this.displayName = arena.getDisplayName();
        this.centerLocation = arena.getCenterLocation();
        this.worldBorderRadius = arena.getWorldBorderRadius();
        this.spectatorSpawnLocation = arena.getSpectatorSpawnLocation();
        this.spawnLocation = arena.getSpawnLocation();
        this.waitingLobby = arena.getWaitingLobby();
    }


    public void addchest(Location location) {
        this.chest.add(location);
    }

    public void removeChest() {
        if (chest.isEmpty()) {
            return;
        }
        this.chest.remove(chest.size() - 1);
    }


    public Arena toArena() {
        return new Arena(gameManager, this.name, this.displayName,this.worldBorderRadius, this.centerLocation, this.spectatorSpawnLocation, this.spawnLocation, this.waitingLobby, this.chest);
    }
}
