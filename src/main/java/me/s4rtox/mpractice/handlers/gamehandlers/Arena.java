package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import javax.xml.stream.Location;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Arena {
    private Location spectatorSpawnLocation;
    private Location[] spawnLocations;
    private Location centerLocation;
    private List<UUID> activePlayers;
    private List<UUID> spectators;

    private int spectatorRadius;

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
