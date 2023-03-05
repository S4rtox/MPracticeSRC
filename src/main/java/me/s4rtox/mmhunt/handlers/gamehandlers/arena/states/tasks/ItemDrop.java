package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemDrop implements Runnable{
    private final ActiveArenaState state;
    private final Arena arena;
    private final List<UUID> alivePlayers;
    private Location dropLocation;
    private int frames = 0;

    public ItemDrop(ActiveArenaState state) {
        this.state = state;
        this.alivePlayers = this.state.getAlivePlayers();
        this.arena = state.getArena();
        this.dropLocation = getDropLocation();
    }

    @Override
    public void run() {
        arena.sendPlayersMessage("&a&lUN DROP HA CAIDO EN " + dropLocation.getBlockX() + " - " + dropLocation.getBlockZ());
        frames++;
    }

    private Location getDropLocation(){
        int i = ThreadLocalRandom.current().nextInt(0, this.alivePlayers.size());
        if(i == 0){
            return variateLocation(arena.getCenterLocation());
        }
        Location playerLoc = Bukkit.getPlayer(alivePlayers.get(i)).getLocation();
        playerLoc.setY(playerLoc.getWorld().getHighestBlockYAt(playerLoc.getBlockX(), playerLoc.getBlockZ()));
        return variateLocation(playerLoc);
    }
    private Location variateLocation(Location location){
        int x = ThreadLocalRandom.current().nextInt(-300, 300);
        int z = ThreadLocalRandom.current().nextInt(-300, 300);
        location.add(x,0,z);
        return location;
    }
}
