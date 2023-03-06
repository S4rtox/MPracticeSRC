package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events.animations.FireworksDownAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemDrop {
    private final ActiveArenaState state;
    private final Arena arena;
    private final List<UUID> alivePlayers;
    private final Location dropLocation;

    public ItemDrop(ActiveArenaState state) {
        this.state = state;
        this.alivePlayers = this.state.getAlivePlayers();
        this.arena = state.getArena();
        this.dropLocation = getDropLocation();
    }

    public void newDrop() {
        arena.sendPlayersMessage("&a&lUN DROP HA CAIDO EN " + dropLocation.getBlockX() + " - " + dropLocation.getBlockZ());
        new FireworksDownAnimation(arena.getGameManager().getPlugin(), dropLocation.clone().add(0,20,0),20*4,() ->{

        });
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

    private void placeDrop(Location location){
        Block chest = location.getBlock();
        chest.setType(Material.CHEST);
        location.add(0,-1,0).getBlock().setType(Material.BEACON);
        Inventory chestInventory = ((Chest) chest.getState()).getInventory();

    }
}
