package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks.ArenaStartingTask;
import me.s4rtox.mmhunt.util.CItemBuilder;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

public class StartingArenaState extends ArenaState {

    @Getter
    private ArenaStartingTask arenaStartingTask;

    public StartingArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onEnable(MMHunt plugin) {
        super.onEnable(plugin);
        setDefaultPlayersStates();
        arenaStartingTask = new ArenaStartingTask(arena, () -> arena.setArenaState(new ActiveArenaState(gameManager, arena)), 10);
        arenaStartingTask.runTaskTimer(plugin, 0, 20);
        setDefaultPlayersStates();
    }
    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        arena.sendAllMessage("&7[&a+&7] &f" + player.getDisplayName());
        giveAdminItems(player);
        setDefaultPlayerState(player);
        arena.updateAllScoreboardsLine(1, "&fPlayers: &a" + arena.getCurrentPlayers() + "&7/&a");
    }

    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public GameState getGameStateEnum() {
        return GameState.STARTING;
    }

    @Override
    public void onPlayerLeave(Player player) {
        super.onPlayerLeave(player);
        arena.sendAllMessage("&7[&c-&7] &f" + player.getDisplayName());
        //Condition to cancell the countdown
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getPlayer().hasPermission("mmhunt.admin")) {
            if (!event.hasItem()) return;
            if (!event.getItem().hasItemMeta()) return;
            NBTItem itemFlag = new NBTItem(event.getItem());
            if (!itemFlag.hasCustomNbtData()) return;
            if (itemFlag.hasTag("GameStarter")) {
                event.setCancelled(true);
            }
        }
    }

    private void cancelStartup(Player player){
        this.arenaStartingTask.cancel();
        arena.sendPlayersMessage("&cStartup cancelled");
       player.getInventory().setItem(4, CItemBuilder.of(Material.EMERALD_BLOCK).name("&aStart Game").setLore("&7Right Click to start game").dummyEnchant().addBooleanNbtData("GameStarter",true).build());
        arena.setArenaState(new WaitingArenaState(gameManager, arena));
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void onKick(PlayerKickEvent event) {
        event.setLeaveMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void StartingChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.getEveryone().forEach(playerUUID -> {
                Player arenaPlayer = Bukkit.getPlayer(playerUUID);
                if(arenaPlayer != null){
                    event.getRecipients().add(arenaPlayer);
                }
            });
            event.setFormat(Colorize.format("&7[&eWaiting&7]&f " + player.getDisplayName() + "&7:&f ") + event.getMessage());
        }
    }
    @EventHandler
    public void freezePlayersInplace(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        if (event.getFrom().getX() == event.getTo().getX() &&
                event.getFrom().getY() == event.getTo().getY() &&
                event.getFrom().getZ() == event.getTo().getZ()) return;
        event.setTo(event.getFrom());
    }

    @Override
    public void setDefaultPlayersStates() {
        arena.updateAllScoreboards( "&e&lMMHunt",
                "",
                "&fPlayers: &a" + arena.getCurrentPlayers() + "&7/&a",
                "",
                "&fStarting in: &7-",
                "",
                "&fStatus: &6&lSTARTING!",
                "&fArena: &a" + arena.getDisplayName(),
                "&fip.example.com"
        );
    }

    private void giveAdminItems(Player test){
        if(test.hasPermission("mmhunt.admin")){
            Inventory inventory = test.getInventory();
            inventory.setItem(0, CItemBuilder.of(Material.BLAZE_ROD).name("&eSelect Runner").setLore("&7Right Click a player to select runner").dummyEnchant().addBooleanNbtData("RunnerSelector",true).build());
            inventory.setItem(4, CItemBuilder.of(Material.BARRIER).name("&cCancel Start").setLore("&7Right Click to cancel start game").dummyEnchant().addBooleanNbtData("GameStarter",true).build());
        }
    }
    @Override
    public void setDefaultPlayerState(Player player){
        arena.updatePlayerScoreboard(player,"&e&lMMHunt",
                "",
                "&fPlayers: &a" + arena.getCurrentPlayers() + "&7/&a",
                "",
                "&fStarting in: &7-",
                "",
                "&fStatus: &6&lSTARTING!",
                "&fArena: &a" + arena.getDisplayName(),
                "&fip.example.com"
        );
        arena.doAllAction(this::giveAdminItems);
    }
}
