package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

public class WaitingArenaState extends ArenaState {

    public WaitingArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        arena.sendAllPlayersMessage("&7[&a+&7] &f" + player.getDisplayName());
        arena.updateScoreboards(Colorize.format("&e&lMSkywars"),
                Colorize.format("&fPlayers: &a" + arena.getCurrentPlayers() + "&7/" + arena.maxPlayers()),
                "",
                Colorize.format("&fStarting in: &e&lWAITING"),
                "",
                Colorize.format("&fArena: &a" + arena.displayName()),
                Colorize.format("&fip.example.com"));
        if (arena.players().size() > (arena.maxPlayers() / 2)) {
            arena.setArenaState(new StartingArenaState(gameManager, arena));
        }
    }


    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public void onPlayerLeave(Player player) {
        super.onPlayerLeave(player);
        arena.sendAllPlayersMessage("&7[&c-&7] &f" + player.getDisplayName());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            arena.removePlayer(event.getPlayer());
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
    private void WaitingChatFormat(AsyncPlayerChatEvent event) {
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
    public void freezePlayersInplace(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        if (!player.getWorld().equals(arena.world())) return;
        if (event.getFrom().getX() == event.getTo().getX() &&
                event.getFrom().getY() == event.getTo().getY() &&
                event.getFrom().getZ() == event.getTo().getZ()) return;
        event.setTo(event.getFrom());
    }

}
