package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.s4rtox.mmhunt.handlers.ScoreboardManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.util.CItemBuilder;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

public class WaitingArenaState extends ArenaState {
    private final ScoreboardManager scoreboardManager;

    public WaitingArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
        this.scoreboardManager = gameManager.getPlugin().getScoreboardManager();
        setDefaultPlayersStates();
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        arena.sendAllMessage("&7[&a+&7] &f" + player.getDisplayName());
       giveAdminItems(player);
       player.teleport(arena.getWaitingLobby());
    }

    private void giveAdminItems(Player test){
        if(test.hasPermission("mmhunt.admin")){
            Inventory inventory = test.getInventory();
            inventory.setItem(0, CItemBuilder.of(Material.BLAZE_ROD).name("&eSelect Runner").setLore("&7Right Click a player to select runner").dummyEnchant().addBooleanNbtData("RunnerSelector",true).build());
            inventory.setItem(4, CItemBuilder.of(Material.EMERALD_BLOCK).name("&aStart Game").setLore("&7Right Click to start game").dummyEnchant().addBooleanNbtData("GameStarter",true).build());
        }
    }


    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public GameState getGameStateEnum() {
        return GameState.WAITING;
    }

    @Override
    public void onPlayerLeave(Player player) {
        super.onPlayerLeave(player);
        arena.sendAllMessage("&7[&c-&7] &f" + player.getDisplayName());
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getPlayer().hasPermission("mmhunt.admin")){
            if(event.getAction().isRightClick()) {
                if (!event.hasItem()) return;
                if (!event.getItem().hasItemMeta()) return;
                NBTItem itemFlag = new NBTItem(event.getItem());
                if (!itemFlag.hasCustomNbtData()) return;
                if (itemFlag.hasTag("GameStarter")) {
                    if(arena.getRunner() == null){
                        event.getPlayer().sendMessage(Colorize.format("&cYou need to select a runner first!"));
                        return;
                    }
                    arena.setArenaState(new StartingArenaState(gameManager, arena));
                }
            } else if (event.getAction().isLeftClick()) {
                if (!event.hasItem()) return;
                if (!event.getItem().hasItemMeta()) return;
                NBTItem itemFlag = new NBTItem(event.getItem());
                if (!itemFlag.hasCustomNbtData()) return;
                if (itemFlag.hasTag("RunnerSelector")) {
                    selectRunner(event.getPlayer());
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        if(event.getPlayer().hasPermission("mmhunt.admin")){
            if(!(event.getRightClicked() instanceof Player)) return;
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (!item.hasItemMeta()) return;
            NBTItem itemFlag = new NBTItem(item);
            if(!itemFlag.hasCustomNbtData()) return;
            if(itemFlag.hasTag("RunnerSelector")){
                selectRunner(event.getPlayer());
            }
        }
    }

    private void selectRunner(Player runner){
        arena.setRunner(runner.getUniqueId());
        arena.sendAllMessage("Runner is:" + arena.getRunner().getName());
    }
    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if (arena.isPlaying(event.getPlayer())) {
            arena.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    private void onKick (PlayerKickEvent event) {
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
    private void WaitingChatFormat(AsyncPlayerChatEvent event) {
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

    @Override
    public void setDefaultPlayersStates() {
        arena.updateAllScoreboards("&e&lMSkywars",
                "",
                "&fPlayers: &a" + arena.getCurrentPlayers(),
                "",
                "&fStarting in: &7-",
                "",
                "&fStatus: &e&lWAITING",
                "&fArena: &a" + arena.getDisplayName(),
                "&fip.example.com"
        );
    }


    @Override
    public void setDefaultPlayerState(Player player){
        arena.updatePlayerScoreboard(player,"&e&lMSkywars",
                "",
                "&fPlayers: &a" + arena.getCurrentPlayers(),
                "",
                "&fStarting in: &7-",
                "",
                "&fStatus: &e&lWAITING",
                "&fArena: &a" + arena.getDisplayName(),
                "&fip.example.com");
        arena.doAllAction(this::giveAdminItems);
    }
}
