package me.s4rtox.mpractice.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.config.ConfigManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.ArenaManager;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.lobbyhandlers.BuildModeHandler;
import me.s4rtox.mpractice.util.Colorize;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
@CommandAlias("mpe|mpractice")
@Description("Base practice command for MPractice")
public class PracticeCommands extends BaseCommand {
    private final MPractice plugin;
    private final ConfigManager config;
    private final BuildModeHandler buildModeHandler;

    private final GameManager gameManager;
    private final ArenaManager arenaManager;

    public PracticeCommands(MPractice plugin){
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.buildModeHandler = plugin.getBuildModeHandler();
        this.gameManager = plugin.getGameManager();
        this.arenaManager = gameManager.arenaManager();
    }

    /////////////////////////////////////////////////////////////////
    // ---------------------- Player Commands --------------------- //
    /////////////////////////////////////////////////////////////////

    @Default
    public void onDefault(CommandSender sender){
        if(sender.hasPermission("mpractice.admin")){
            sender.sendMessage(Colorize.format("&e---------------------------------------------"));
            sender.sendMessage(Colorize.format("&aUsing MineguardsPractice version: " + plugin.version));
            sender.sendMessage(Colorize.format("             &aMade by &eS4rtox"));
            sender.sendMessage(Colorize.format("&e---------------------------------------------"));
            sender.sendMessage("");
            sender.sendMessage(Colorize.format("&cUSAGE: /MPE <Join|Leave|Spectate|Spawn|Build|Admin>"));
        }else{
            sender.sendMessage(Colorize.format("&cUSAGE: /MPE <Join|Leave|Spawn|Spectate>"));
        }

    }
    @Subcommand("spectate")
    @Description("Attempts to join a game as spectator")
    public void onSpectateCommand(Player player){
        player.sendMessage("WIP");
    }

    @Subcommand("join")
    @Description("Attempts to join a game")
    public void onJoinCommand(Player player, String[] args){
        if(arenaManager.getArenas().size() == 0){
            player.sendMessage(Colorize.format("&cThere are no arenas available"));
            return;
        }

        Optional<Arena> currentArena = arenaManager.findPlayerArena(player);
        if(currentArena.isPresent()){
            player.sendMessage(Colorize.format("&cYou're already in a game!"));
            return;
        }

        if(arenaManager.getArenas().size() == 1){
            Arena arena = arenaManager.getArenas().get(0);
            arena.addPlayer(player);
            return;
        }

        Optional<Arena> optionalArena = arenaManager.findArena(args[0]);
        if(!optionalArena.isPresent()){
            player.sendMessage(Colorize.format("&cThat arena doesn't exist"));
            return;
        }

        optionalArena.get().addPlayer(player);
    }

    @Subcommand("leave")
    @Description("Attempts to leave a running game")
    public void onLeaveCommand(Player player){
        Optional<Arena> currentArena = arenaManager.findPlayerArena(player);
        if(!currentArena.isPresent()){
            player.sendMessage(Colorize.format("&cYou're not in a game!"));
            return;
        }
        currentArena.get().removePlayer(player);
    }

    @Subcommand("spawn")
    @CommandAlias("spawn")
    @Description("Teleports to spawn")
    public void onSpawnCommand(Player player){
        plugin.getSpawnSetter().teleport(player);
        player.sendMessage("&aSuccessfully teleported to spawn");
    }

    /////////////////////////////////////////////////////////////////
    // ---------------------- Build Commands --------------------- //
    /////////////////////////////////////////////////////////////////

    @Subcommand("build")
    @CommandAlias("build")
    @CommandPermission("mpractice.builder.buildmodetoggle")
    @Description("Toggles buildmode")
    public void onBuildModeToggle(Player player){
        buildModeHandler.checkBuildMode(player);
    }

    /////////////////////////////////////////////////////////////////
    // ---------------------- Admin Commands --------------------- //
    /////////////////////////////////////////////////////////////////

    @Subcommand("admin")
    @CommandPermission("mpractice.admin")
    public class AdminCommands extends BaseCommand{
        @Default
        public void onDefault(Player player){
            player.sendMessage(Colorize.format("&cUSAGE: /mpe admin <Start|Forcestart|Cancel|Arena|Reload|Setspawn>"));
        }

        @Subcommand("reload")
        @Description("Reloads the plugin")
        public void onReloadCommand(CommandSender sender){
            try {
                plugin.getDefaultConfig().reload();
                plugin.getSpawnConfig().reload();
                plugin.getMessagesConfig().reload();
                plugin.getConfigManager().loadConfig();
                plugin.getArenaConfig().reload();
                sender.sendMessage(plugin.getConfigManager().MS_RELOAD_SUCCESS());
            } catch (IOException e) {
                sender.sendMessage(Colorize.format( "&cThere has been an error reloading the plugin, check the console for details"));
                throw new RuntimeException(e);
            }
        }
        @Subcommand("setlobby")
        @Description("Sets the lobby spawn")
        public void onSetSpawnCommand(Player player){
            plugin.getSpawnSetter().set(player.getLocation());
            player.sendMessage(config.MS_SPAWN_SETSPAWN());
        }

        @Subcommand("start")
        @Description("Forces the start of the countdown for the game")
        public void onStartCommand(Player player){
            player.sendMessage("WIP");
        }

        @Subcommand("forcestart")
        @Description("Forces the start of a game, SKIPPING the countdown")
        public void onForceStartCommand(Player player){
            player.sendMessage("WIP");
        }

        @Subcommand("cancel")
        @Description("Cancels the countdown of the game")
        public void onForceCancelComamnd(Player player){
            player.sendMessage("WIP");
        }


        /////////////////////////////////////////////////////////////////
        // ---------------------- Arena Commands --------------------- //
        /////////////////////////////////////////////////////////////////

        @Subcommand("arena")
        public class ArenaCommands extends BaseCommand{

            @Default
            public void onDefault(Player player){
            player.sendMessage(Colorize.format("&cUSAGE: /mpe admin arena <Create|Delete|List|Edit>"));
            }
            @Subcommand("list")
            @Description("Sets the lobby spawn")
            public void onListArenas(Player player){
                if(gameManager.arenaManager().getArenas().isEmpty()){
                    player.sendMessage(Colorize.format("&cThere are no disponible arenas."));
                }else{
                    player.sendMessage(Colorize.format("&aThe disponible arenas are:"));
                    for(Arena arena : gameManager.arenaManager().getArenas()){
                        player.sendMessage(Colorize.format("&a - " + arena.name()));
                    }
                }
                
            }

            @Subcommand("create")
            @Description("Attempts to create a new arena")
            public void onArenaCreate(Player player){
                gameManager.setupWizardManager().startWizard(player,null);
            }


            //TODO: Edit subcommand
            @Subcommand("edit")
            @Description("Attempts to edit an arena")
            public void onArenaEdit(Player player, String[] args){
                String arenaInput = args[0];
                Optional<Arena> optionalArena = gameManager.arenaManager().findArena(arenaInput);
                if(!optionalArena.isPresent()){
                    player.sendMessage(Colorize.format("&4No arenas with that name were found"));
                }else{
                    //TODO: Restore and tp in case of an edit.
                    player.teleport(optionalArena.get().centerLocation());
                    gameManager.setupWizardManager().startWizard(player,optionalArena.get());
                }
            }

            @Subcommand("delete")
            @Description("Attempts to delete an arena")
            public void onArenaDelete(Player player, String[] args){
                player.sendMessage("Editing" + Arrays.toString(args));
                String arenaInput = args[0];
                Optional<Arena> optionalArena = gameManager.arenaManager().findArena(arenaInput);
                if(!optionalArena.isPresent()){
                    player.sendMessage(Colorize.format("&4No arenas with that name were found"));
                }else{
                    gameManager.arenaManager().deleteArena(optionalArena.get());
                    gameManager.configManager().deleteArena(optionalArena.get());
                }
            }
        }
    }
}
