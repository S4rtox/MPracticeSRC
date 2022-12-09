package me.s4rtox.mpractice.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.lobbyhandlers.BuildModeHandler;
import me.s4rtox.mpractice.util.Colorize;
import me.s4rtox.mpractice.config.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

@SuppressWarnings("unused")
@CommandAlias("mpe|mpractice")
@Description("Base practice command for MPractice")
public class PracticeCommands extends BaseCommand {
    private final MPractice plugin;
    private final ConfigManager config;
    private final BuildModeHandler buildModeHandler;
    public PracticeCommands(MPractice plugin){
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.buildModeHandler = plugin.getBuildModeHandler();
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
            sender.sendMessage(Colorize.format("&cUSAGE: /MPE <JOIN|LEAVE|ADMIN>"));
        }else{
            sender.sendMessage(Colorize.format("&cUSAGE: /MPE <JOIN|LEAVE>"));
        }

    }
    @Subcommand("spectate")
    @Description("Attempts to join a game as spectator")
    public void onSpectateCommand(Player player){

    }

    @Subcommand("join")
    @Description("Attempts to join a game")
    public void onJoinCommand(Player player){

    }

    @Subcommand("leave")
    @Description("Attempts to leave a running game")
    public void onLeaveCommand(Player player){
        plugin.getSpawnSetter().teleport(player);
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
            player.sendMessage(Colorize.format("&cUSAGE: /MPE ADMIN <START|FORCESTART|CANCEL|ARENA|RELOAD|SETSPAWN>"));
        }

        @Subcommand("reload")
        @Description("Reloads a plugin")
        public void onReloadCommand(CommandSender sender){
            try {
                plugin.getDefaultConfig().reload();
                plugin.getSpawnConfig().reload();
                plugin.getMessagesConfig().reload();
                plugin.getConfigManager().loadConfig();
                sender.sendMessage(plugin.getConfigManager().MS_RELOAD_SUCCESS());
            } catch (IOException e) {
                sender.sendMessage(Colorize.format( "&cThere has been an error reloading the plugin, check the console for details"));
                throw new RuntimeException(e);
            }
        }
        @Subcommand("setspawn")
        @Description("Sets the lobby spawn")
        public void onSetSpawnCommand(Player player){
            plugin.getSpawnSetter().set(player.getLocation());
            player.sendMessage(config.MS_SPAWN_SETSPAWN());
        }

        @Subcommand("start")
        @Description("Forces the start of the countdown for the game")
        public void onStartCommand(Player player){

        }

        @Subcommand("forcestart")
        @Description("Forces the start of a game, SKIPPING the countdown")
        public void onForceStartCommand(Player player){

        }

        @Subcommand("cancel")
        @Description("Cancels the countdown of the game")
        public void onForceCancelComamnd(Player player){

        }


        /////////////////////////////////////////////////////////////////
        // ---------------------- Arena Commands --------------------- //
        /////////////////////////////////////////////////////////////////

        @Subcommand("arena")
        public class ArenaCommands extends BaseCommand{

            @Subcommand("list")
            @Description("Sets the lobby spawn")
            public void onListArenas(Player player){
                player.sendMessage(Colorize.format("&cTest for arena commands"));
            }

        }
    }
}
