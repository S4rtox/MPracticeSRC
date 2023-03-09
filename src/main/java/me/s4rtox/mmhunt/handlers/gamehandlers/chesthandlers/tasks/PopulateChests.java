package me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers.tasks;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.util.PlacableBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PopulateChests implements Runnable {
    private final MMHunt plugin;
    private final World world;
    private final Integer spread;
    private final Integer randomness;
    private final Set<Location> chestLocations = new HashSet<>();
    private final Consumer<Set<Location>> onEnd;

    public PopulateChests(MMHunt plugin, World world, Integer spread, Integer randomness, Consumer<Set<Location>> onEnd){
        this.world = world;
        this.spread = spread;
        this.randomness = randomness;
        this.onEnd = onEnd;
        this.plugin = plugin;
    }
    @Override
    public void run() {
        double radius = world.getWorldBorder().getSize() / 2;
        double centerX = world.getWorldBorder().getCenter().getX();
        double centerZ = world.getWorldBorder().getCenter().getZ();

        int numZones = (int) Math.ceil(radius / spread);

        double maxX;
        double maxy;
        double minx;
        double miny;

        Bukkit.getLogger().info(" NumZones: " + numZones + " Spread: " + spread);

        for (int i = -numZones; i <= numZones; i++) {
            for (int j = -numZones; j <= numZones; j++) {
                double x = centerX + i * spread + ThreadLocalRandom.current().nextInt(-randomness, randomness + 1) + 0.5;
                double z = centerZ + j * spread + ThreadLocalRandom.current().nextInt(-randomness, randomness + 1) + 0.5;

                if (Math.sqrt(x * x + z * z) <= radius) {
                    int y = world.getHighestBlockYAt((int) x, (int) z) + 1;

                    //plugin.getWorkloadRunnable().addWorkload(new PlacableBlock(new Location(world,x,y,z),Material.CHEST));
                    chestLocations.add(new Location(world, x, y, z));
                }
            }
        }

        onEnd.accept(chestLocations);

    }


    /*
    int maxDistance = (int)world.getWorldBorder().getSize()/2;
        int numZones = maxDistance / spread;
        Bukkit.getLogger().info("MaxDistance: "+ maxDistance + " NumZones: " + numZones + " Spread: " + spread);
        for (int i = -numZones; i < numZones; i++) {
            for (int j = -numZones; j < numZones; j++) {
                int x = i * spread + ThreadLocalRandom.current().nextInt(-randomness,randomness);
                int z = j * spread + ThreadLocalRandom.current().nextInt(-randomness,randomness);
                int y = world.getHighestBlockYAt(x,z) + 1;

                //int y = 100;
                chestLocations.add(new Location(world,x,y,z));
                plugin.getWorkloadRunnable().addWorkload(new PlacableBlock(new Location(world,x,y,z),Material.CHEST));
            }
        }
        onEnd.accept(chestLocations);
     */
    /*
    // Get the size of the world border
        int maxDistance = (int) world.getWorldBorder().getSize()/2;

        // Calculate the number of zones based on the spread value
        int numZones = maxDistance / spread;

        // Calculate the number of chests to place per zone
        int numChestsPerZone = (int) Math.ceil((double) numChests / (numZones * numZones));

        // Get the center of the world border
        Location center = world.getWorldBorder().getCenter();

        // Output some debugging information
        Bukkit.getLogger().info("MaxDistance: " + maxDistance + " NumZones: " + numZones + " Spread: " + spread + " NumChestsPerZone: " + numChestsPerZone);

        // Loop through each zone
        for (int i = -numZones; i < numZones; i++) {
            for (int j = -numZones; j < numZones; j++) {
                // Calculate the center of the current zone with a random offset
                int x = i * spread + spread / 2 + ThreadLocalRandom.current().nextInt(-randomness, randomness + 1);
                int z = j * spread + spread / 2 + ThreadLocalRandom.current().nextInt(-randomness, randomness + 1);

                // Create a location object at the center of the current zone
                Location loc = new Location(world, x, 0, z);

                // Check if the location is within the world border
                if (loc.distance(center) <= maxDistance / 2) {
                    // Loop through the number of chests to place per zone
                    for (int k = 0; k < numChestsPerZone; k++) {
                        // Calculate the y coordinate for the chest
                        int y = world.getHighestBlockYAt(x, z) + 1;

                        // Add the chest location to the set of chest locations
                        chestLocations.add(new Location(world, x, y, z));

                        // Add a workload to place a chest block at the chest location
                        plugin.getWorkloadRunnable().addWorkload(new PlacableBlock(new Location(world, x, y, z), Material.CHEST));
                    }
                }
            }
        }

        // Call the onEnd consumer with the set of chest locations
        onEnd.accept(chestLocations);
     */

}
