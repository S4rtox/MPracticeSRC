package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.PlayableArenaState;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OnPlayerDeathEvent implements Runnable {
    private final PlayableArenaState state;
    private final Arena arena;

    public final Player player;
    private int timer = 20;

    public OnPlayerDeathEvent(@NotNull PlayableArenaState state, @NotNull Player player){
        this.state = state;
        this.arena = state.getArena();
        this.player = player;
        arena.getGameManager().getPlugin().getServer().getScheduler().runTaskTimer(arena.getGameManager().getPlugin(), this,0,20);
    }
    @Override
    public void run() {
        if(timer == 20){
            player.setPlayerListName(Colorize.format(ActiveArenaState.DEAD + player.getName()));
            player.spigot().respawn();
            player.teleport(arena.getSpectatorSpawnLocation());
            state.getRespawningPlayers().add(player.getUniqueId());
            player.setGameMode(GameMode.SPECTATOR);
        }
        player.sendTitle(Colorize.format("&c&lYou died!"), Colorize.format("&eRespawning in " + timer));

        if (timer <= 10){
            player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS,1.0f, 1.0f);
        }
        if(timer <= 0){
            state.setDefaultPlayerState(player);
            player.sendTitle(Colorize.format("&aRespawned!"), "");
            state.getRespawningPlayers().remove(player.getUniqueId());
        }
        timer --;
    }
}
