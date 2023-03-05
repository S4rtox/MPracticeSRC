package me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PopulateChests implements Runnable {
    private final Plugin plugin;
    private final World world;
    private final Integer spread;
    private final Integer randomness;
    private final Set<Location> chestLocations = new HashSet<>();
    private final Consumer<Set<Location>> onEnd;
    public PopulateChests(Plugin plugin,World world, Integer spread, Integer randomness, Consumer<Set<Location>> onEnd){
        this.world = world;
        this.spread = spread;
        this.randomness = randomness;
        this.onEnd = onEnd;
        this.plugin = plugin;
    }
    @Override
    public void run() {
        int maxDistance = (int) Math.ceil(Math.sqrt(world.getWorldBorder().getSize())) / 2;
        int numZones = maxDistance / spread;

        for (int i = 0; i < numZones; i++) {
            for (int j = 0; j < numZones; j++) {
                int x = i * spread + ThreadLocalRandom.current().nextInt(-randomness,randomness);
                int z = j * spread + ThreadLocalRandom.current().nextInt(-randomness,randomness);

                Bukkit.getScheduler().runTask(plugin,runnable->{
                    int y = world.getHighestBlockYAt(x,z) + 1;
                    chestLocations.add(new Location(world,x,y,z));
                    world.getBlockAt(x, y, z).setType(Material.CHEST);
                });
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getScheduler().runTask(plugin,runnable-> onEnd.accept(chestLocations));
    }


}
