package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import lombok.Getter;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.tasks.ArenaStartingTask;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

public class StartingArenaState extends ArenaState {

    @Getter
    private ArenaStartingTask arenaStartingTask;

    public StartingArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onEnable(MPractice plugin) {
        super.onEnable(plugin);
        arenaStartingTask = new ArenaStartingTask(arena, () -> {
            arena.spawnLocations().forEach(location -> {
                Block floor = location.clone().subtract(0,1,0).getBlock();
                if(floor != null && floor.getType() == Material.GLASS){
                    floor.setType(Material.AIR);
                }
            });
            arena.setArenaState(new ActiveArenaState(gameManager, arena));
        }, 10);
        arenaStartingTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        arena.sendAllPlayersMessage("&7[&a+&7] &f" + player.getDisplayName());
    }

    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public void onPlayerLeave(Player player) {
        super.onPlayerLeave(player);
        arena.sendAllPlayersMessage("&7[&c-&7] &f" + player.getDisplayName());
        //Condition to cancell the countdown
        if (arena.players().size() < (arena.maxPlayers() / 2)) {
            this.arenaStartingTask.cancel();
            arena.sendPlayersMessage("&cNot enough players!, Start cancelled");
            arena.setArenaState(new WaitingArenaState(gameManager, arena));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void onKick (PlayerKickEvent event) {
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
            arena.allPlayers().forEach(playerUUID -> {
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
}
