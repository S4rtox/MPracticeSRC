package me.s4rtox.mpractice.handlers.gamehandlers.arena;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.*;
import me.s4rtox.mpractice.util.Colorize;
import me.s4rtox.mpractice.util.TitleBuilder;
import net.kyori.adventure.audience.Audience;
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
    @Getter
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

    public void setArenaState(ArenaState arenaState) {
        this.arenaState.onDisable(gameManager.plugin());
        this.arenaState = arenaState;
        arenaState.onEnable(gameManager.plugin());
    }

    //////////////////////////////////////////////////////////////////////////
    // ---------------------- Adding/Removing players --------------------- //
    //////////////////////////////////////////////////////////////////////////
    public void addPlayer(Player player) {
        //This two checks done by the arena because its easier
        if (gameManager.arenaManager().findPlayerArena(player).isPresent()) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, you're already in a game!"));
            return;
        }
        if (players.size() >= maxPlayers) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, this arena is full!"));
            return;
        }
        arenaState.onPlayerJoin(player);
    }
    public void addSpectator(Player player) {
        if (gameManager.arenaManager().findPlayerArena(player).isPresent()) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, you're already in a game!"));
            return;
        }
        arenaState.onSpectatorJoin(player);
    }

    public void removePlayer(Player player) {
        if (!isInGame(player)) {
            return;
        }
       arenaState.onPlayerLeave(player);
    }

    // This also sends them to the lobby
    public void sendToLobby(Player player) {
        removePlayer(player);
        gameManager.plugin().getSpawnSetter().teleport(player);
        gameManager.plugin().getLobbyHandler().addLobbyPlayer(player);
    }

    ///////////////////////////////////////////////////////////////
    // ---------------------- Info Getters --------------------- //
    ///////////////////////////////////////////////////////////////

    public boolean isPlaying(Player player) {
        return players.contains(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public boolean isInGame(Player player) {
        return allPlayers.contains(player.getUniqueId());
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
    /////////////////////////////////////////////////////////////////////
    // ---------------------- Arena Setup Events --------------------- //
    /////////////////////////////////////////////////////////////////////
    public void fillChests(){
        gameManager.chestManager().fillChestLocations(islandChests,"islandChest",true);
        gameManager.chestManager().fillChestLocations(middleChests,"middleChest",true);
    }

    // Method should become useless on arena reset system, awaiting implementation
    public void clearChests(){
        gameManager.chestManager().clearChestsInventories(islandChests);
        gameManager.chestManager().clearChestsInventories(middleChests);
    }

    //////////////////////////////////////////////////////////////////////
    // ---------------------- Arena Player Events --------------------- //
    //////////////////////////////////////////////////////////////////////

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

    public void sendArenaTitle(String title, String subTitle, long fadein, long stayin ,long fadeout) {
        for (UUID playerUUID : this.players) {
            Audience audience = gameManager.plugin().adventure().player(playerUUID);
            TitleBuilder.showTitle(audience,title,subTitle,fadein,stayin,fadeout);
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

    public void updateScoreboards(String title, String... lines){
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                if(title != null){
                    gameManager.plugin().getScoreboardManager().getPlayerScoreboard(player).updateTitle(title);
                }
                if(lines != null){
                    gameManager.plugin().getScoreboardManager().getPlayerScoreboard(player).updateLines(lines);
                }

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

    ////////////////////////////////////////////////////////////////////////
    // ---------------------- Arena status changers --------------------- //
    ////////////////////////////////////////////////////////////////////////

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

}
