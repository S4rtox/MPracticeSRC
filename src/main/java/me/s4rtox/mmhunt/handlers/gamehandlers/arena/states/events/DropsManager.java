package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events.animations.FireworksDownAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DropsManager {
    private final ActiveArenaState state;
    private final Arena arena;
    private final List<UUID> alivePlayers;

    public DropsManager(ActiveArenaState state) {
        this.state = state;
        this.alivePlayers = this.state.getAlivePlayers();
        this.arena = state.getArena();
    }

    public void newDrop() {
        Location dropLocation = getDropLocation();
        Bukkit.getLogger().info(dropLocation.toString());
        arena.sendPlayersMessage("&a&lUN DROP HA CAIDO EN " + dropLocation.getBlockX() + " - " + dropLocation.getBlockZ());
        new FireworksDownAnimation(arena.getGameManager().getPlugin(), dropLocation.clone().add(0,84,0),20*4,() -> placeDrop(dropLocation)).start();
    }

    private Location getDropLocation(){
        int i = ThreadLocalRandom.current().nextInt(0, this.alivePlayers.size());
        if(i == 0){
            Location playerLoc = arena.getCenterLocation();
            return variateLocation(playerLoc);
        }
        Location playerLoc = Bukkit.getPlayer(alivePlayers.get(i)).getLocation();
        return variateLocation(playerLoc);
    }
    private Location variateLocation(Location location){
        int x = ThreadLocalRandom.current().nextInt(-300, 300);
        int z = ThreadLocalRandom.current().nextInt(-300, 300);
        location.add(x,0,z);
        location.setY(location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()) + 2);
        return location;
    }

    private void placeDrop(Location location){
        Block chest = location.getBlock();
        chest.setType(Material.CHEST);
        Bukkit.getLogger().info(location.toString());
        location.clone().add(0,-1,0).getBlock().setType(Material.BEACON);
        Bukkit.getLogger().info(location.toString());
        Inventory chestInventory = ((Chest) chest.getState()).getInventory();
        chestInventory.setContents(getRandomDropItems());
    }

    @Nullable
    private ItemStack[] getRandomDropItems() {
        File f = new File(arena.getGameManager().getPlugin().getDataFolder().getAbsolutePath() + "\\Drops");
        if(!f.exists()){
            f.mkdir();
        }
        File[] files = f.listFiles();
        if(files == null) return null;
        FileConfiguration c = YamlConfiguration.loadConfiguration(files[ThreadLocalRandom.current().nextInt(0, files.length -1)]);
        return ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
    }
}
