package me.s4rtox.mmhunt.handlers.gamehandlers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import org.bukkit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArenaConfigurationManager {
    private final GameManager gameManager;
    private final YamlDocument arenasConfig;

    public ArenaConfigurationManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.arenasConfig = gameManager.getPlugin().getArenaConfig();
    }

    public void saveArena(Arena arena) {
        //if the arena is present, it makes its content null to overwrite it.
        if (arenasConfig.isSection(arena.getName())) {
            arenasConfig.remove(arena.getName());
        }
        Section arenaSection = arenasConfig.createSection(arena.getName());
        arenaSection.set("DisplayName", arena.getDisplayName());
        arenaSection.set("World", arena.getCenterLocation().getWorld().getName());
        arenaSection.set("ArenaCenter", getLocationString(arena.getCenterLocation(), false));
        arenaSection.set("StartingBorder", arena.getWorldBorderRadius());
        arenaSection.set("SpectatorSpawn", getLocationString(arena.getSpectatorSpawnLocation(), true));
        arenaSection.set("PlayerSpawn", getLocationString(arena.getSpawnLocation(),true));
        arenaSection.set("WaitingLobby", getLocationString(arena.getWaitingLobby(),true));
        List<String> islandChests = new ArrayList<>();
        for (Location chest : arena.getChests()) {
            islandChests.add(getLocationString(chest, false));
        }
        arenaSection.set("Chests", islandChests);

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
                world = new WorldCreator(worldArena).createWorld();
            }
            int border = arenaSection.getInt("StartingBorder");

            Location arenaCenter = getLocationFromString(arenaSection.getString("ArenaCenter"), world);
            Location spectatorSpawn = getLocationFromString(arenaSection.getString("SpectatorSpawn"), world);
            Location playerSpawn = getLocationFromString(arenaSection.getString("PlayerSpawn"), world);
            Location waitingLobby = getLocationFromString(arenaSection.getString("WaitingLobby"), world);

            List<String> islandCList = arenaSection.getStringList("Chests");
            List<Location> chests = new ArrayList<>();
            for (String chestLoc : islandCList) {
                chests.add(getLocationFromString(chestLoc,world));
            }

            arenas.add(new Arena(gameManager, arena, displayName, border,arenaCenter,spectatorSpawn,playerSpawn,waitingLobby,chests));
        }
        return arenas;
    }

    public void deleteArena(Arena arena) {
        if (arenasConfig.isSection(arena.getName())) {
            arenasConfig.remove(arena.getName());
            try {
                arenasConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Location getLocationFromString(String string, World world){
        String[] playerSpawnLoc = string.split(",");
        if(playerSpawnLoc.length == 5){
            return new Location(world, Double.parseDouble(playerSpawnLoc[0]), Double.parseDouble(playerSpawnLoc[1]), Double.parseDouble(playerSpawnLoc[2]), Float.parseFloat(playerSpawnLoc[3]), Float.parseFloat(playerSpawnLoc[4]));
        }else{
            return new Location(world, Double.parseDouble(playerSpawnLoc[0]), Double.parseDouble(playerSpawnLoc[1]), Double.parseDouble(playerSpawnLoc[2]));

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
