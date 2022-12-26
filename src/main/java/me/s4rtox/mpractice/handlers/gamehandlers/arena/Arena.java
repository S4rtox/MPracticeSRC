package me.s4rtox.mpractice.handlers.gamehandlers.arena;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.PlayerRollbackManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.*;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {
    private final GameManager gameManager;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String displayName;
    @Getter
    private final int maxPlayers;
    @Getter
    private final World world;
    @Getter
    @Setter
    private Location centerLocation;
    @Getter
    @Setter
    private Location corner1Location;

    @Getter
    @Setter
    private Location corner2Location;
    @Getter
    @Setter
    private Location spectatorSpawnLocation;
    @Getter
    @Setter
    private List<Location> spawnLocations;
    @Getter
    @Setter
    private List<Location> islandChests;
    @Getter
    @Setter
    private List<Location> middleChests;

    @Getter
    private final List<UUID> players;
    @Getter
    private final List<UUID> spectators;
    @Getter
    private final List<UUID> allPlayers;

    private ArenaState arenaState;

    public Arena(
            @NonNull GameManager gameManager,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Location centerLocation,
            @NonNull Location corner1Location,
            @NonNull Location corner2Location,
            @NonNull Location spectatorSpawnLocation,
            @NonNull List<Location> spawnLocations,
            List<Location> islandChests,
            List<Location> middleChests
    ) {
        this.gameManager = gameManager;

        this.name = name;
        this.displayName = displayName;
        this.centerLocation = centerLocation;
        this.world = centerLocation.getWorld();
        this.corner1Location = corner1Location;
        this.corner2Location = corner2Location;
        this.spectatorSpawnLocation = spectatorSpawnLocation;
        this.spawnLocations = spawnLocations;
        this.maxPlayers = spawnLocations.size();
        this.islandChests = islandChests;
        this.middleChests = middleChests;
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        allPlayers = new ArrayList<>();
        arenaState = new InitArenaState(gameManager, this);
        arenaState.onEnable(gameManager.plugin());
    }

    public void addSpectator(Player player) {
        if (gameManager.arenaManager().findPlayerArena(player).isPresent()) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, you're already in a game!"));
            return;
        }
        if (!(arenaState instanceof ActiveArenaState)) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
            return;
        }
        spectators.add(player.getUniqueId());
        allPlayers.add(player.getUniqueId());
        PlayerRollbackManager.save(player);
        player.teleport(spectatorSpawnLocation);
        player.getInventory().clear();
        player.getEquipment().clear();
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void addPlayer(Player player) {
        //Checks if the player is already in a game
        if (gameManager.arenaManager().findPlayerArena(player).isPresent()) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, you're already in a game!"));
            return;
        }
        //Checks if the arena is full
        if (players.size() >= maxPlayers) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, this arena is full!"));
            return;
        }
        //Checks if the arena hasn't started.
        if (!(arenaState instanceof WaitingArenaState) && !(arenaState instanceof StartingArenaState)) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, this arena has already started!"));
            return;
        }
        players.add(player.getUniqueId());
        allPlayers.add(player.getUniqueId());
        PlayerRollbackManager.save(player);
        player.teleport(spawnLocations.get(players.indexOf(player.getUniqueId())));
        player.getInventory().clear();
        player.getInventory();
        player.getEquipment().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        sendAllPlayersMessage("&7[&a+&7] &f" + player.getDisplayName());
        gameManager.plugin().getLobbyHandler().removeLobbyPlayer(player);
        //Condition to start the countdown (Currently if its half its maximum)
        if (players.size() > (maxPlayers / 2) && (arenaState instanceof WaitingArenaState)) {
            setArenaState(new StartingArenaState(gameManager, this));
        }
    }

    public void removePlayer(Player player) {
        if (!isInGame(player)) {
            return;
        }
        if (arenaState instanceof ActiveArenaState) {
            ActiveArenaState state = (ActiveArenaState) arenaState;
            state.killPlayer(player);
        } else if (arenaState instanceof WaitingArenaState || arenaState instanceof StartingArenaState) {
            sendAllPlayersMessage("&7[&c-&7] &f" + player.getDisplayName());
        }
        players.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());
        allPlayers.remove(player.getUniqueId());
        PlayerRollbackManager.restore(player, false);
        gameManager.plugin().getLobbyHandler().addLobbyPlayer(player);
        //Condition to cancell the countdown
        if (players.size() < (maxPlayers / 2) && arenaState instanceof StartingArenaState) {
            StartingArenaState startingArenaState = (StartingArenaState) arenaState;
            startingArenaState.arenaStartingTask().cancel();
            sendPlayersMessage("&cNot enough players!, Start cancelled");
            setArenaState(new WaitingArenaState(gameManager, this));
        }

    }

    public void sendToLobby(Player player) {
        removePlayer(player);
        gameManager.plugin().getSpawnSetter().teleport(player);
    }

    public boolean isPlaying(Player player) {
        return players.contains(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public boolean isInGame(Player player) {
        return allPlayers.contains(player.getUniqueId());
    }

    public void sendPlayersMessage(String message) {
        message = Colorize.format(message);
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) player.sendMessage(message);
        }
    }

    //Includes spectators
    public void sendAllPlayersMessage(String message) {
        message = Colorize.format(message);
        for (UUID playerUUID : this.allPlayers) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) player.sendMessage(message);
        }
    }

    public void sendArenaTitle(String Title, double fadein, double fadeout) {
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {

            }
        }
    }

    public void sendPlayingSound(Sound sound, float volume, float pitch) {
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    public void sendPlayersSound(Sound sound, float volume, float pitch) {
        for (UUID playerUUID : this.allPlayers) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    public ArenaState arenaState() {
        return arenaState;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState.onDisable(gameManager.plugin());
        this.arenaState = arenaState;
        arenaState.onEnable(gameManager.plugin());
    }

    public boolean cancelArena() {
        if (!(this.arenaState instanceof ActiveArenaState)) {
            return false;
        }
        sendAllPlayersMessage("&c&lGAME CANCELLED BY AN ADMINISTRATOR");
        this.setArenaState(new FinishingArenaState(gameManager, this, null));
        return true;
    }

    public boolean forceStartArena() {
        if (this.arenaState instanceof WaitingArenaState || this.arenaState instanceof StartingArenaState) {
            if (this.arenaState() instanceof StartingArenaState) {
                StartingArenaState state = (StartingArenaState) this.arenaState();
                state.arenaStartingTask().cancel();
            }
            sendPlayersMessage("&c&lGAME FORCEFULLY STARTED BY AN ADMINISTRATOR");
            this.setArenaState(new ActiveArenaState(gameManager, this));
            return true;
        } else {
            return false;
        }
    }

    public boolean startArena() {
        if (this.arenaState instanceof WaitingArenaState) {
            this.setArenaState(new StartingArenaState(gameManager, this));
            sendPlayersMessage("&c&lCOUNTDOWN FORCEFULLY STARTED BY AN ADMINISTRATOR");
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentPlayers(){
        if(this.arenaState instanceof ActiveArenaState){
            ActiveArenaState activeArenaState = (ActiveArenaState) this.arenaState;
            return activeArenaState.getAlivePlayers().size();
        }else if(this.arenaState instanceof FinishingArenaState){
            return 1;
        }else{
            return this.players.size();
        }
    }

}
