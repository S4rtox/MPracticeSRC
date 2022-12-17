package me.s4rtox.mpractice.handlers.gamehandlers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.s4rtox.mpractice.MPractice;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ArenaConfigurationManager {
    private final YamlDocument arenasConfig;

    public ArenaConfigurationManager(MPractice plugin){

        this.arenasConfig = plugin.getArenaConfig();
    }

    //TODO: Fix any possible error that this might cause?. (getting the world from the centerLocation)
    public void saveArena(Arena arena){
        //if the arena is present, it makes its content null to overwrite it.
      if(arenasConfig.isSection(arena.name())){
          arenasConfig.set(arena.name(), null);
      }

      Section arenaSection = arenasConfig.getSection(arena.name());
      arenaSection.set("DisplayName", arena.displayName());
      arenaSection.set("World", arena.centerLocation().getWorld().getName());
      arenaSection.set("ArenaCenter", getLocationString(arena.centerLocation(), false));
      arenaSection.set("ArenaCorner1", getLocationString(arena.corner1Location(), false));
      arenaSection.set("ArenaCorner2", getLocationString(arena.corner1Location(), false));
      arenaSection.set("SpectatorSpawn", getLocationString(arena.spectatorSpawnLocation(), true));

      List<String> spawnsList = new ArrayList<>();
      for (Location spawn : arena.spawnLocations()) {
          spawnsList.add(getLocationString(spawn, true));
      }
      arenaSection.set("PlayerSpawns", spawnsList);

      List<String> islandChests = new ArrayList<>();
      for (Location chest : arena.islandChests()) {
          islandChests.add(getLocationString(chest, false));
      }
      arenaSection.set("IslandChests", islandChests);

      List<String> middleChests = new ArrayList<>();
      for (Location chest : arena.middleChests()) {
          middleChests.add(getLocationString(chest, false));
      }
      arenaSection.set("MiddleChests", middleChests);
    }
    public List<Arena> loadArenas(){
        List<Arena> arenas = new ArrayList<>();

        for (String arena : arenasConfig.getRoutesAsStrings(false)) {
            Bukkit.getLogger().info(arena);
            Bukkit.getLogger().info(arenasConfig.getSection(arena).toString());
            Section arenaSection = arenasConfig.getSection(arena);
            String displayName = arenaSection.getString("DisplayName");
            String worldArena = arenaSection.getString("World");

            String[] centerloc = arenaSection.getString("ArenaCenter").split(",");
            Location arenaCenter = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(centerloc[0]), Double.parseDouble(centerloc[1]), Double.parseDouble(centerloc[2]));

            String[] cornerloc = arenaSection.getString("ArenaCorner1").split(",");
            Location arenaCorner = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(cornerloc[0]), Double.parseDouble(cornerloc[1]), Double.parseDouble(cornerloc[2]));

            String[] cornerloc2 = arenaSection.getString("ArenaCorner2").split(",");
            Location arenaCorner2 = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(cornerloc2[0]), Double.parseDouble(cornerloc2[1]), Double.parseDouble(cornerloc2[2]));

            String[] spectatorloc = arenaSection.getString("SpectatorSpawn").split(",");
            Location spectatorSpawn = new Location(Bukkit.getWorld(worldArena), Double.parseDouble(spectatorloc[0]),  Double.parseDouble(spectatorloc[1]),  Double.parseDouble(spectatorloc[2]), Float.parseFloat(spectatorloc[3]), Float.parseFloat(spectatorloc[4]));

            List<String> spawnList =  arenaSection.getStringList("PlayerSpawns");
            List<Location> playerSpawns = new ArrayList<>();
            for (String spawnString : spawnList){
                String[] spawn = spawnString.split(",");
                playerSpawns.add(new Location(Bukkit.getWorld(worldArena), Double.parseDouble(spawn[0]),  Double.parseDouble(spawn[1]),  Double.parseDouble(spawn[2]), Float.parseFloat(spawn[3]), Float.parseFloat(spawn[4])));
            }

            List<String> islandCList =  arenaSection.getStringList("IslandChests");
            List<Location> islandChests = new ArrayList<>();
            for (String chestLoc : islandCList){
                String[] chest = chestLoc.split(",");
                islandChests.add(new Location(Bukkit.getWorld(worldArena), Double.parseDouble(chest[0]),  Double.parseDouble(chest[1]),  Double.parseDouble(chest[2])));
            }

            List<String> middleCList =  arenaSection.getStringList("MiddleChests");
            List<Location> middleChests = new ArrayList<>();
            for (String chestLoc : middleCList){
                String[] chest = chestLoc.split(",");
                middleChests.add(new Location(Bukkit.getWorld(worldArena), Double.parseDouble(chest[0]),  Double.parseDouble(chest[1]),  Double.parseDouble(chest[2])));
            }

            arenas.add(new Arena(arena, displayName, arenaCenter, arenaCorner, arenaCorner2, spectatorSpawn, playerSpawns, islandChests, middleChests)) ;
        }
        return arenas;
    }

    public void deleteArena(Arena arena){
        if(arenasConfig.isSection(arena.name())){
            arenasConfig.set(arena.name(), null);
        }
    }
     private String getLocationString(Location location, boolean fullLocation){
        String value;
        if(fullLocation){
            value = location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        }else {
            value = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        }
        return value;
     }


}
