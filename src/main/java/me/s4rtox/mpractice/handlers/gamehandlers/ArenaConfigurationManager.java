package me.s4rtox.mpractice.handlers.gamehandlers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArenaConfigurationManager {
    private final GameManager gameManager;
    private final YamlDocument arenasConfig;

    public ArenaConfigurationManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.arenasConfig = gameManager.plugin().getArenaConfig();
    }

    public void saveArena(Arena arena) {
        //if the arena is present, it makes its content null to overwrite it.
        if (arenasConfig.isSection(arena.name())) {
            arenasConfig.remove(arena.name());
        }
        Section arenaSection = arenasConfig.createSection(arena.name());
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
        try {
            arenasConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Arena> loadArenas() {
        List<Arena> arenas = new ArrayList<>();

        for (String arena : arenasConfig.getRoutesAsStrings(false)) {
            Section arenaSection = arenasConfig.getSection(arena);
            String displayName = arenaSection.getString("DisplayName");
            String worldArena = arenaSection.getString("World");
            World world = Bukkit.getWorld(worldArena);
            if (world == null) {
                world = new WorldCreator(worldArena).generateStructures(false).type(WorldType.FLAT).generatorSettings("2;0;1;").createWorld();
            }
            String[] centerloc = arenaSection.getString("ArenaCenter").split(",");
            Location arenaCenter = new Location(world, Double.parseDouble(centerloc[0]), Double.parseDouble(centerloc[1]), Double.parseDouble(centerloc[2]));

            String[] cornerloc = arenaSection.getString("ArenaCorner1").split(",");
            Location arenaCorner = new Location(world, Double.parseDouble(cornerloc[0]), Double.parseDouble(cornerloc[1]), Double.parseDouble(cornerloc[2]));

            String[] cornerloc2 = arenaSection.getString("ArenaCorner2").split(",");
            Location arenaCorner2 = new Location(world, Double.parseDouble(cornerloc2[0]), Double.parseDouble(cornerloc2[1]), Double.parseDouble(cornerloc2[2]));

            String[] spectatorloc = arenaSection.getString("SpectatorSpawn").split(",");
            Location spectatorSpawn = new Location(world, Double.parseDouble(spectatorloc[0]), Double.parseDouble(spectatorloc[1]), Double.parseDouble(spectatorloc[2]), Float.parseFloat(spectatorloc[3]), Float.parseFloat(spectatorloc[4]));

            List<String> spawnList = arenaSection.getStringList("PlayerSpawns");
            List<Location> playerSpawns = new ArrayList<>();
            for (String spawnString : spawnList) {
                String[] spawn = spawnString.split(",");
                playerSpawns.add(new Location(world, Double.parseDouble(spawn[0]), Double.parseDouble(spawn[1]), Double.parseDouble(spawn[2]), Float.parseFloat(spawn[3]), Float.parseFloat(spawn[4])));
            }

            List<String> islandCList = arenaSection.getStringList("IslandChests");
            List<Location> islandChests = new ArrayList<>();
            for (String chestLoc : islandCList) {
                String[] chest = chestLoc.split(",");
                islandChests.add(new Location(world, Double.parseDouble(chest[0]), Double.parseDouble(chest[1]), Double.parseDouble(chest[2])));
            }

            List<String> middleCList = arenaSection.getStringList("MiddleChests");
            List<Location> middleChests = new ArrayList<>();
            for (String chestLoc : middleCList) {
                String[] chest = chestLoc.split(",");
                middleChests.add(new Location(world, Double.parseDouble(chest[0]), Double.parseDouble(chest[1]), Double.parseDouble(chest[2])));
            }

            arenas.add(new Arena(gameManager, arena, displayName, arenaCenter, arenaCorner, arenaCorner2, spectatorSpawn, playerSpawns, islandChests, middleChests));
        }
        return arenas;
    }

    public void deleteArena(Arena arena) {
        if (arenasConfig.isSection(arena.name())) {
            arenasConfig.remove(arena.name());
            try {
                arenasConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getLocationString(Location location, boolean fullLocation) {
        String value;
        if (fullLocation) {
            value = location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        } else {
            value = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        }
        return value;
    }


}
