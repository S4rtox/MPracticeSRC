package me.s4rtox.mmhunt.handlers.gamehandlers.arena;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.*;

import me.s4rtox.mmhunt.util.CItemBuilder;
import me.s4rtox.mmhunt.util.Colorize;
import me.s4rtox.mmhunt.util.FastBlockPlacer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class Arena {
    @Getter
    private final GameManager gameManager;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String displayName;
    @Getter
    private final World world;
    @Getter
    @Setter
    private Location centerLocation;
    @Getter
    @Setter
    private int worldBorderRadius;
    @Getter
    private Location spectatorSpawnLocation;
    @Getter
    private Location spawnLocation;
    @Getter
    private Location waitingLobby;
    @Getter
    @Setter
    private List<Location> chests;
    @Getter
    private final Set<UUID> hunters;
    @Setter
    @Getter
    private UUID runner;
    @Getter
    private final Set<UUID> spectators;
    @Getter
    private ArenaState arenaState;

    public Arena(
            @NonNull GameManager gameManager,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Integer worldBorderRadius,
            @NonNull Location centerLocation,
            @NonNull Location spectatorSpawnLocation,
            @NonNull Location spawnLocation,
            @NonNull Location waitingLobby,
            List<Location> chests
    ) {

        this.gameManager = gameManager;
        this.name = name;
        this.displayName = displayName;
        this.worldBorderRadius = worldBorderRadius;
        this.centerLocation = centerLocation;
        this.world = centerLocation.getWorld();
        this.spectatorSpawnLocation = spectatorSpawnLocation;
        this.spawnLocation = spawnLocation;
        this.waitingLobby = waitingLobby;
        this.chests = chests;
        this.hunters = new HashSet<>();
        this.spectators = new HashSet<>();
        this.runner = null;
        this.arenaState = new InitArenaState(gameManager,this);
        this.arenaState.onEnable(gameManager.getPlugin());
    }

    public Arena(
            @NonNull GameManager gameManager,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Integer worldBorderRadius,
            @NonNull Location centerLocation,
            @NonNull Location spectatorSpawnLocation,
            @NonNull Location spawnLocation,
            @NonNull Location waitingLobby,
            List<Location> chests,
            ArenaState arenaState
    ) {
        this.gameManager = gameManager;
        this.name = name;
        this.displayName = displayName;
        this.worldBorderRadius = worldBorderRadius;
        this.centerLocation = centerLocation;
        this.world = centerLocation.getWorld();
        this.spectatorSpawnLocation = spectatorSpawnLocation;
        this.spawnLocation = spawnLocation;
        this.waitingLobby = waitingLobby;
        this.chests = chests;
        this.hunters = new HashSet<>();
        this.spectators = new HashSet<>();
        this.runner = null;
        //This is so horrible placed an thought but I cant be fucked I've been 5 hours fixing gradle/maven shit so fuck you if you care :)))))
        this.arenaState = new StartupArenaState(gameManager,this);
        this.arenaState.onEnable(gameManager.getPlugin());
    }


    public void setArenaState(ArenaState arenaState) {
        this.arenaState.onDisable(gameManager.getPlugin());
        this.arenaState = arenaState;
        arenaState.onEnable(gameManager.getPlugin());
    }

    public Player getRunner() {
        return Bukkit.getPlayer(runner);
    }

    public Set<UUID> getPlayers() {
        Set<UUID> players = new HashSet<>(hunters);
        if(runner != null){
            players.add(runner);
        }
        return players;
    }

    public Set<UUID> getEveryone() {
        Set<UUID> players = new HashSet<>(hunters);
        players.addAll(spectators);
        players.add(runner);
        return players;
    }

    //////////////////////////////////////////////////////////////////////////
    // ---------------------- Adding/Removing players --------------------- //
    //////////////////////////////////////////////////////////////////////////
    public void addPlayer(Player player) {
        //This two checks done by the arena because its easier
        if (gameManager.getArenaManager().isInArena(player)) {
            player.sendMessage(Colorize.format("&cError sending you to the game!, you're already in a game!"));
            return;
        }
        arenaState.onPlayerJoin(player);
    }

    public void addSpectator(Player player) {
        if (gameManager.getArenaManager().isInArena(player)) {
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
        gameManager.getPlugin().getSpawnSetter().teleport(player);
        gameManager.getPlugin().getLobbyHandler().addLobbyPlayer(player);
    }

    ///////////////////////////////////////////////////////////////
    // ---------------------- Info Getters --------------------- //
    ///////////////////////////////////////////////////////////////

    public boolean isRunner(Player player){
        return runner == player.getUniqueId();
    }
    public boolean isHunter(Player player){
        return hunters.contains(player.getUniqueId());
    }
    public boolean isPlaying(Player player) {
        return getPlayers().contains(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return spectators.contains(player.getUniqueId());
    }


    public boolean isInGame(Player player) {
        return getEveryone().contains(player.getUniqueId());
    }

    public int getCurrentPlayers() {
        if (this.arenaState.getGameStateEnum() == GameState.ACTIVE) {
            ActiveArenaState activeArenaState = (ActiveArenaState) this.arenaState;
            return activeArenaState.getAlivePlayers().size();
        } else if (this.arenaState.getGameStateEnum() == GameState.FINISHING) {
            return 1;
        } else {
            return this.getPlayers().size();
        }
    }

    /////////////////////////////////////////////////////////////////////
    // ---------------------- Arena Setup Events --------------------- //
    /////////////////////////////////////////////////////////////////////
    public void firstFillChests(){
        gameManager.getChestManager().firstFillChests(chests,"islandChest",true);
    }
    public void fillChests() {
        gameManager.getChestManager().fillChestLocations(chests, "islandChest", true);
    }

    public void restoreChests(){
        for(Location chest : chests){
            FastBlockPlacer.rapidSetBlock((net.minecraft.world.level.World) world,FastBlockPlacer.fromMaterial(Material.CHEST) ,chest.getBlockX(),chest.getBlockY(),chest.getBlockZ());

        }
    }

    // Method should become useless on arena reset system, awaiting implementation
    public void clearChests() {
        gameManager.getChestManager().clearChestsInventories(chests);
    }

    //////////////////////////////////////////////////////////////////////
    // ---------------------- Arena Player Events --------------------- //
    //////////////////////////////////////////////////////////////////////

    public void doPlayerAction(Consumer<Player> playerConsumer){
        for (UUID playerUUID : this.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) playerConsumer.accept(player);
        }
    }

    public void doHunterAction(Consumer<Player> playerConsumer){
        for (UUID playerUUID : this.getHunters()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) playerConsumer.accept(player);
        }
    }

    public void doAllAction(Consumer<Player> playerConsumer){
        for (UUID playerUUID : this.getEveryone()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) playerConsumer.accept(player);
        }
    }

    public void doRunnerAction(Consumer<Player> playerConsumer){
            Player runner = Bukkit.getPlayer(this.runner);
            if (runner != null) playerConsumer.accept(runner);
    }

    public void sendPlayersMessage(String message) {
        message = Colorize.format(message);
        for (UUID playerUUID : this.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) player.sendMessage(message);
        }
    }

    //Includes spectators
    public void sendAllMessage(String message) {
        message = Colorize.format(message);
        for (UUID playerUUID : this.getEveryone()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) player.sendMessage(message);
        }
    }

    public void sendPlayersTitle(String title, String subTitle, int fadein, int stayin ,int fadeout) {
        for (UUID playerUUID : this.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player!= null) player.sendTitle(Colorize.format(title),Colorize.format(subTitle),fadein,stayin,fadeout);
        }
    }

    public void sendAllTitle(String title, String subTitle, int fadein, int stayin ,int fadeout) {
        for (UUID playerUUID : this.getEveryone()) {
            Player audience = Bukkit.getPlayer(playerUUID);
            if(audience != null) audience.sendTitle(Colorize.format(title),Colorize.format(subTitle),fadein,stayin,fadeout);
        }
    }


    public void sendPlayersSound(Sound sound, float volume, float pitch) {
        for (UUID playerUUID : this.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    public void sendAllSound(Sound sound, float volume, float pitch) {
        for (UUID playerUUID : this.getEveryone()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }


    public void updatePlayersScoreboards(String title, String... lines){
        for (UUID playerUUID : this.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                gameManager.getPlugin().getScoreboardManager().updateScoreboard(player, title, lines);
            }
        }
    }

    public void updatePlayerScoreboard(Player player,String title, String... lines){
            if (player != null) {
                gameManager.getPlugin().getScoreboardManager().updateScoreboard(player, title, lines);
            }
    }

    public void updateAllScoreboards(String title, String... lines){
        for (UUID playerUUID : this.getEveryone()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                gameManager.getPlugin().getScoreboardManager().updateScoreboard(player, title, lines);
            }
        }
    }

    public void updatePlayerScoreboardsLine(int line, String text){
        for (UUID playerUUID : this.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                gameManager.getPlugin().getScoreboardManager().updateLine(player, line, text);
            }
        }
    }

    public void updateAllScoreboardsLine(int line, String text){
        for (UUID playerUUID : this.getEveryone()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
            gameManager.getPlugin().getScoreboardManager().updateLine(player,line,text);
            }
        }
    }

    public void resetScoreboards(){
        for (UUID playerUUID : this.getEveryone()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                gameManager.getPlugin().getScoreboardManager().resetScoreboard(player);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////
    // ---------------------- Arena status changers --------------------- //
    ////////////////////////////////////////////////////////////////////////

    public boolean cancelArena() {
        if (!(this.arenaState.getGameStateEnum() == GameState.ACTIVE)) {
            return false;
        }
        sendAllMessage("&c&lGAME CANCELLED BY AN ADMINISTRATOR");
        ((ActiveArenaState) this.arenaState).cancelArenaEvents();
        this.setArenaState(new FinishingArenaState(gameManager, this));
        return true;
    }

    public boolean forceStartArena() {
        if (this.arenaState.getGameStateEnum() == GameState.WAITING || this.arenaState.getGameStateEnum() == GameState.STARTING) {
            if (this.arenaState.getGameStateEnum() == GameState.STARTING) {
                StartingArenaState state = (StartingArenaState) this.arenaState;
                state.getArenaStartingTask().cancel();
            }
            sendPlayersMessage("&c&lGAME FORCEFULLY STARTED BY AN ADMINISTRATOR");
            this.setArenaState(new RunnerOutArenaState(gameManager, this));
            return true;
        } else {
            return false;
        }
    }

    public boolean startArena() {
        if (this.arenaState.getGameStateEnum() == GameState.WAITING) {
            this.setArenaState(new StartingArenaState(gameManager, this));
            sendPlayersMessage("&c&lCOUNTDOWN FORCEFULLY STARTED BY AN ADMINISTRATOR");
            return true;
        } else {
            return false;
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // ---------------------- Arena misc getters --------------------- //
    ////////////////////////////////////////////////////////////////////////

    public ItemStack getMaterial(){

    return switch (arenaState.getGameStateEnum()) {
            case SETUP ->
                    CItemBuilder.of(Material.LEGACY_STAINED_CLAY).addItemData((byte) 15).name("&9&lSetting Up").setLore("&a" + getPlayers().size(), "&7Arena: &f" + displayName).build();
            case WAITING ->
                    CItemBuilder.of(Material.LEGACY_STAINED_CLAY).addItemData((byte) 5).name("&a&lWaiting").setLore("&a" + getPlayers().size(), "&7Arena: &f" + displayName).build();
            case STARTING ->
                    CItemBuilder.of(Material.LEGACY_STAINED_CLAY).addItemData((byte) 4).name("&e&lStarting").setLore("&a" + getPlayers().size(), "&7Arena: &f" + displayName).build();
            case ACTIVE ->
                    CItemBuilder.of(Material.LEGACY_STAINED_CLAY).addItemData((byte) 14).name("&c&lOngoing").setLore("&a" + getPlayers().size(), "&7Arena: &f" + displayName).build();
           case FINISHING ->
                   CItemBuilder.of(Material.LEGACY_STAINED_CLAY).addItemData((byte) 8).name("&7&lFinishing").setLore("&a" + getPlayers().size(), "&7Arena: &f" + displayName).build();
           default ->
                   CItemBuilder.of(Material.LEGACY_STAINED_CLAY).addItemData((byte) 7).name("&8&lRestarting").setLore("&a" + getPlayers().size(), "&7Arena: &f" + displayName).build();
    };
    }
}
