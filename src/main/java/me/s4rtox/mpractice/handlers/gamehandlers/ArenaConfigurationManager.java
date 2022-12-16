package me.s4rtox.mpractice.handlers.gamehandlers;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.s4rtox.mpractice.MPractice;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArenaConfigurationManager {
    private final YamlDocument arenasConfig;

    public ArenaConfigurationManager(MPractice plugin){
        this.arenasConfig = plugin.getArenaConfig();
    }

    public List<Arena> loadArenas(){
        List<Arena> arenas = new ArrayList<>();
        Set<String> arenaPaths = arenasConfig.getRoutesAsStrings(false);
        for (String arena : arenaPaths) {
            String displayName = arenasConfig.getSection(arena).getString("DisplayName");
            String worldArena = arenasConfig.getSection(arena).getString("World");

            String[] centerloc = arenasConfig.getSection(arena).getString("ArenaCenter").split(",");
            Location arenaCenter = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(centerloc[0]), Double.parseDouble(centerloc[1]), Double.parseDouble(centerloc[2]));

            String[] cornerloc = arenasConfig.getSection(arena).getString("ArenaCorner").split(",");
            Location arenaCorner = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(cornerloc[0]), Double.parseDouble(cornerloc[1]), Double.parseDouble(cornerloc[2]));

            String[] cornerloc2 = arenasConfig.getSection(arena).getString("ArenaCorner2").split(",");
            Location arenaCorner2 = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(cornerloc2[0]), Double.parseDouble(cornerloc2[1]), Double.parseDouble(cornerloc2[2]));

            String[] spectatorloc = arenasConfig.getSection(arena).getString("SpectatorSpawn").split(",");
            Location spectatorSpawn = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(spectatorloc[0]),  Double.parseDouble(spectatorloc[1]),  Double.parseDouble(spectatorloc[2]), Float.parseFloat(spectatorloc[3]), Float.parseFloat(spectatorloc[4]));

            List<String> spawnList =  arenasConfig.getSection(arena).getStringList("PlayerSpawns");
            List<Location> playerSpawns = new ArrayList<>();
            for (String spawnString : spawnList){
                String[] spawn = spawnString.split(",");
                playerSpawns.add(new Location(Bukkit.getWorld(worldArena), Double.parseDouble(spawn[0]),  Double.parseDouble(spawn[1]),  Double.parseDouble(spawn[2]), Float.parseFloat(spawn[3]), Float.parseFloat(spawn[4])));
            }

            List<String> islandCList =  arenasConfig.getSection(arena).getStringList("IslandChests");
            List<Location> islandChests = new ArrayList<>();
            for (String chestLoc : islandCList){
                String[] chest = chestLoc.split(",");
                islandChests.add(new Location(Bukkit.getWorld(worldArena), Double.parseDouble(chest[0]),  Double.parseDouble(chest[1]),  Double.parseDouble(chest[2])));
            }

            List<String> middleCList =  arenasConfig.getSection(arena).getStringList("MiddleChests");
            List<Location> middleChests = new ArrayList<>();
            for (String chestLoc : middleCList){
                String[] chest = chestLoc.split(",");
                middleChests.add(new Location(Bukkit.getWorld(worldArena), Double.parseDouble(chest[0]),  Double.parseDouble(chest[1]),  Double.parseDouble(chest[2])));
            }

            arenas.add(new Arena(displayName, arenaCenter, arenaCorner, arenaCorner2, spectatorSpawn, playerSpawns, islandChests, middleChests)) ;
        }
        return arenas;
    }
}
