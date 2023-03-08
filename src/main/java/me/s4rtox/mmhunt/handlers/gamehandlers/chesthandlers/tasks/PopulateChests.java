package me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers.tasks;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.util.FastBlockPlacer;
import me.s4rtox.mmhunt.util.PlacableBlock;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
    public PopulateChests(MMHunt plugin,World world, Integer spread, Integer randomness, Consumer<Set<Location>> onEnd){
        this.world = world;
        this.spread = spread;
        this.randomness = randomness;
        this.onEnd = onEnd;
        this.plugin = plugin;
    }
    @Override
    public void run() {
        int maxDistance = (int)world.getWorldBorder().getSize();
        int numZones = maxDistance / spread;
        Bukkit.getLogger().info("MaxDistance: "+ maxDistance + " NumZones: " + numZones + " Spread: " + spread);
        for (int i = 0; i < numZones; i++) {
            for (int j = 0; j < numZones; j++) {
                int x = i * spread + ThreadLocalRandom.current().nextInt(-randomness,randomness);
                int z = j * spread + ThreadLocalRandom.current().nextInt(-randomness,randomness);
                int finalI = i;
                int finalJ = j;
                    int y = world.getHighestBlockYAt(x,z) + 1;
                    //int y = 100;
                    chestLocations.add(new Location(world,x,y,z));
                    plugin.getWorkloadRunnable().addWorkload(new PlacableBlock(new Location(world,x,y,z),Material.CHEST));
            }
        }
         onEnd.accept(chestLocations);
    }


}
