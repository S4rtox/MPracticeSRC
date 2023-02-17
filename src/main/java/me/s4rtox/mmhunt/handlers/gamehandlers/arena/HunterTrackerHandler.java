package me.s4rtox.mmhunt.handlers.gamehandlers.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HunterTrackerHandler implements Listener {
    private final Map<UUID, UUID> targetLocation = new HashMap<>();

    public void setTarget(@NotNull Player player, @NotNull Player target){
        targetLocation.put(player.getUniqueId(),target.getUniqueId());
    }
    private Location getCompassTargetLocation(@NotNull Player player){
        Player target =  Bukkit.getPlayer(targetLocation.get(player.getUniqueId()));
        if(target == null) return player.getCompassTarget();
        return target.getLocation();
    }

    public @Nullable String getCompassTargetName(@NotNull Player player){
        Player target =  Bukkit.getPlayer(targetLocation.get(player.getUniqueId()));
        if(target == null) return null;
        return target.getName();
    }

    public int getBlocksAway(Player player){
        return (int) getCompassTargetLocation(player).distance(player.getLocation());
    }
    public void setCompassToTarget(Player player){
        Location target = getCompassTargetLocation(player);
        if(target.getWorld() == player.getWorld()){
            player.setCompassTarget(target);
        }
    }
}
