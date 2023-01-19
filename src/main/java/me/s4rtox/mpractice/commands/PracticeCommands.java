package me.s4rtox.mpractice.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.config.ConfigManager;
import me.s4rtox.mpractice.handlers.gamehandlers.ArenaManager;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.states.GameState;
import me.s4rtox.mpractice.util.Colorize;
import me.s4rtox.mpractice.util.CustomItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@SuppressWarnings("unused")
@CommandAlias("mpe|mpractice")
@Description("Base practice command for MPractice")
public class PracticeCommands extends BaseCommand {
    private final MPractice plugin;
    private final ConfigManager config;

    private final GameManager gameManager;
    private final ArenaManager arenaManager;

    public PracticeCommands(MPractice plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.gameManager = plugin.getGameManager();
        this.arenaManager = gameManager.arenaManager();

    }



    /////////////////////////////////////////////////////////////////
    // ---------------------- Player Commands --------------------- //
    /////////////////////////////////////////////////////////////////

    @CatchUnknown
    @Default
    public void onDefault(CommandSender sender) {
        if (sender.hasPermission("mpractice.admin")) {
            sender.sendMessage(Colorize.format("&e&l________________________________________________"));
            sender.sendMessage(Colorize.format("&aUsing MineguardsSkywars version: " + plugin.version));
            sender.sendMessage(Colorize.format("             &aMade by &eS4rtox"));
            sender.sendMessage(Colorize.format("&e&l________________________________________________"));
            sender.sendMessage("");
            sender.sendMessage(Colorize.format("&cUSAGE: /MPE <Join|Leave|Spectate|Spawn|Build|Admin>"));
        } else {
            sender.sendMessage(Colorize.format("&cUSAGE: /MPE <Join|Leave|Spawn|Spectate>"));
        }

    }

    @Subcommand("spectate")
    @CommandAlias("spectate")
    @Description("Attempts to join a game as spectator")
    public class SpectateCommand extends BaseCommand {
        @Default
        public void defaultSpectateCommand(Player player) {
            player.sendMessage(Colorize.format("&cUSAGE: /mpe spectate <arena|player>"));
        }

        @Subcommand("arena")
        @Description("Attempts to join an ongoing game as spectator")
        public void onSpectateArenaCommand(Player player, @co.aikar.commands.annotation.Optional String[] args) {
            if (arenaManager.getArenas().isEmpty()) {
                player.sendMessage(Colorize.format("&cThere are no arenas available"));
                return;
            }
            if (arenaManager.getArenas().size() == 1) {
                Arena arena = arenaManager.getArenas().get(0);
                arena.addSpectator(player);
                return;
            }
            if (args == null || args.length == 0) {
                player.sendMessage(Colorize.format("&cUSAGE: /mpe spectate arena <Arena>"));
                return;
            }
            Optional<Arena> targetArena = arenaManager.findArena(args[0]);
            if (targetArena.isPresent() && targetArena.get().arenaState().getGameStateEnum() == GameState.ACTIVE) {
                player.sendMessage(Colorize.format("&aSpectating: " + targetArena.get().displayName()));
                targetArena.get().addSpectator(player);
            } else {
                player.sendMessage(Colorize.format("&cThis arena is not a valid arena!"));
            }
        }

        @Subcommand("player")
        @Description("Attempts to join an ongoing game as spectator")
        @Syntax("&cUSAGE: /mpe spectate player <target>")
        public void onSpectatePlayer(Player player, OnlinePlayer target) {
            if (player.equals(target.getPlayer())) {
                player.sendMessage(Colorize.format("&cYou cant spectate yourself!"));
            }
            Optional<Arena> targetArena = arenaManager.findPlayerArena(target.getPlayer());
            if (targetArena.isPresent() && targetArena.get().arenaState().getGameStateEnum() == GameState.ACTIVE) {
                player.sendMessage(Colorize.format("&aSpectating: " + target.getPlayer().getName()));
                targetArena.get().addSpectator(player);
            } else {
                player.sendMessage(Colorize.format("&cThe player " + target.getPlayer().getName() + " &cis not in a running game!"));
            }

        }
    }


    @Subcommand("join|play")
    @CommandAlias("join|play")
    @Description("Attempts to join a game")
    public void onJoinCommand(Player player, String[] args) {
        if(arenaManager.isInArena(player)){
            player.sendMessage(Colorize.format("&cYou cant join a game if you are already playing!"));
            return;
        }
        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(Colorize.format("&cThere are no arenas available"));
            return;
        }
        if (arenaManager.getArenas().size() == 1) {
            Arena arena = arenaManager.getArenas().get(0);
            arena.addPlayer(player);
            return;
        }
        if(args == null || args.length == 0){
            getArenaSelector().open(player);
            return;
        }
        Optional<Arena> optionalArena = arenaManager.findArena(args[0]);
        if (!optionalArena.isPresent()) {
            player.sendMessage(Colorize.format("&cThat arena doesn't exist"));
            return;
        }

        optionalArena.get().addPlayer(player);
    }

    @Subcommand("autojoin|autoplay")
    @CommandAlias("autojoin|autoplay")
    @Description("Attempts to automatically join the best game")
    public void onAutoJoinCommand(Player player){
        if(arenaManager.isInArena(player)){
            player.sendMessage(Colorize.format("&cYou cant join a game if you are already playing!"));
            return;
        }
        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(Colorize.format("&cThere are no arenas available"));
            return;
        }
        if (arenaManager.getArenas().size() == 1) {
            Arena arena = arenaManager.getArenas().get(0);
            arena.addPlayer(player);
            return;
        }
        arenaManager.sortArenas();
        arenaManager.getArenas().get(arenaManager.getArenas().size() - 1).addPlayer(player);
    }

    public PaginatedGui getArenaSelector() {
        PaginatedGui arenaSelector = Gui.paginated().title(Component.text(Colorize.format("&aArena selector"))).rows(6).create();
        arenaSelector.disableAllInteractions();
        arenaSelector.setItem(6,3, ItemBuilder.from(CustomItemBuilder.getItem(new ItemStack(Material.PAPER), "&cPrevious", false, "")).asGuiItem(event -> arenaSelector.previous()));
        arenaSelector.setItem(6,7,  ItemBuilder.from(CustomItemBuilder.getItem(new ItemStack(Material.PAPER), "&aNext", false, "")).asGuiItem(event -> arenaSelector.next()));
        arenaSelector.getFiller().fillBottom(ItemBuilder.from(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte) 1)).asGuiItem());
        arenaManager.sortArenas();
        for (Arena arena : arenaManager.getArenas()){
            arenaSelector.addItem(ItemBuilder.from(arena.getMaterial()).asGuiItem(event -> {
                Player player = (Player) event.getWhoClicked();
                if (event.getClick() == ClickType.RIGHT){
                    arena.addSpectator(player);
                }
                else {
                    arena.addPlayer(player);
                }
            }));
        }
        return arenaSelector;
    }
    @Subcommand("leave")
    @CommandAlias("leave")
    @Description("Attempts to leave a running game")
    public void onLeaveCommand(Player player) {
        Optional<Arena> currentArena = arenaManager.findPlayerArena(player);
        if (!currentArena.isPresent()) {
            currentArena = arenaManager.findSpectatingArena(player);
            if(currentArena.isPresent()){
                currentArena.get().sendToLobby(player);
                return;
            }
            player.sendMessage(Colorize.format("&cYou're not in a game!"));
            return;
        }
        currentArena.get().sendToLobby(player);
    }

    @Subcommand("spawn")
    @CommandAlias("spawn")
    @Description("Teleports to spawn")
    public void onSpawnCommand(Player player) {
        Optional<Arena> currentArena = arenaManager.findPlayerArena(player);
        if (currentArena.isPresent()) {
            currentArena.get().sendToLobby(player);
            player.sendMessage(Colorize.format("&aSuccesfully teleported to spawn"));
            return;
        }
        plugin.getSpawnSetter().teleport(player);
        player.sendMessage(Colorize.format("&aSuccesfully teleported to spawn"));
    }


    /////////////////////////////////////////////////////////////////
    // ---------------------- Admin Commands --------------------- //
    /////////////////////////////////////////////////////////////////

    @Subcommand("admin")
    @CommandPermission("mpractice.admin")
    public class AdminCommands extends BaseCommand {
        @Default
        public void onDefault(Player player) {
            player.sendMessage(Colorize.format("&cUSAGE: /mpe admin <Start|Forcestart|Cancel|Arena|Reload|Setspawn>"));
        }

        @Subcommand("reload")
        @Description("Reloads the plugin")
        public void onReloadCommand(CommandSender sender) {
            try {
                plugin.getDefaultConfig().reload();
                plugin.getSpawnConfig().reload();
                plugin.getMessagesConfig().reload();
                plugin.getChestConfig().reload();
                plugin.getConfigManager().loadConfig();
                plugin.getArenaConfig().reload();
                plugin.getGameManager().chestManager().loadChests();
                sender.sendMessage(plugin.getConfigManager().MS_RELOAD_SUCCESS());
                sender.sendMessage(Colorize.format("Remember to restart the server if you made changes to any arena"));
            } catch (IOException e) {
                sender.sendMessage(Colorize.format("&cThere has been an error reloading the plugin, check the console for details"));
                throw new RuntimeException(e);
            }
        }

        @Subcommand("setlobby")
        @Description("Sets the lobby spawn")
        public void onSetSpawnCommand(Player player) {
            plugin.getSpawnSetter().set(player.getLocation());
            player.sendMessage(config.MS_SPAWN_SETSPAWN());
        }

        @Subcommand("start")
        @CommandAlias("start")
        @Description("Forces the start of the countdown for the game")
        public void onStartCommand(Player player, String[] args) {
            if (args.length == 0) {
                Optional<Arena> optionalArena = arenaManager.findPlayerArena(player);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&cYou're not currently in a game!"));
                    return;
                }
                Arena arena = optionalArena.get();
                if (arena.startArena()) {
                    player.sendMessage(Colorize.format("&aSuccessfully started the arena: " + arena.name()));
                } else {
                    player.sendMessage(Colorize.format("&cFailed to start the arena: " + arena.name() + ". this arena has/is already starting!"));
                }
            } else {
                String arenaName = args[0];
                Optional<Arena> optionalArena = arenaManager.findArena(arenaName);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&cThat arena doesnt exist"));
                    return;
                }
                Arena arena = optionalArena.get();
                if (arena.startArena()) {
                    player.sendMessage(Colorize.format("&aSuccessfully started the arena: " + arena.name()));
                } else {
                    player.sendMessage(Colorize.format("&cFailed to start the arena: " + arena.name() + ". this arena has/is already starting!"));
                }
            }
        }

        @Subcommand("forcestart")
        @Description("Forces the start of a game, SKIPPING the countdown")
        @CommandAlias("forcestart")
        public void onForceStartCommand(Player player, String[] args) {
            if (args.length == 0) {
                Optional<Arena> optionalArena = arenaManager.findPlayerArena(player);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&cYou're not currently in a game!"));
                    return;
                }
                Arena arena = optionalArena.get();
                if (arena.forceStartArena()) {
                    player.sendMessage(Colorize.format("&aSuccessfully started the arena: " + arena.name()));
                } else {
                    player.sendMessage(Colorize.format("&cFailed to start the arena: " + arena.name() + ". this arena has already started!"));
                }
            } else {
                String arenaName = args[0];
                Optional<Arena> optionalArena = arenaManager.findArena(arenaName);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&cThat arena doesnt exist"));
                    return;
                }
                Arena arena = optionalArena.get();
                if (arena.forceStartArena()) {
                    player.sendMessage(Colorize.format("&aSuccessfully started the arena: " + arena.name()));
                } else {
                    player.sendMessage(Colorize.format("&cFailed to start the arena: " + arena.name() + ". this arena has already started!"));
                }
            }
        }

        @Subcommand("cancel")
        @CommandAlias("cancel")
        @Description("Cancels the countdown of the game")
        public void onForceCancelComamnd(Player player, String[] args) {
            if (args.length == 0) {
                Optional<Arena> optionalArena = arenaManager.findPlayerArena(player);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&cYou're not currently in a game!"));
                    return;
                }
                Arena arena = optionalArena.get();
                if (arena.cancelArena()) {
                    player.sendMessage(Colorize.format("&aSuccesfully terminated arena game: " + arena.name()));
                } else {
                    player.sendMessage(Colorize.format("&cFailed to terminate arena game, game is currently not running."));
                }
            } else {
                String arenaName = args[0];
                Optional<Arena> optionalArena = arenaManager.findArena(arenaName);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&cThat arena doesnt exist"));
                    return;
                }
                Arena arena = optionalArena.get();
                if (arena.cancelArena()) {
                    player.sendMessage(Colorize.format("&aSuccesfully terminated arena game: " + arena.name()));
                } else {
                    player.sendMessage(Colorize.format("&cFailed to terminate arena game, game is currently not running."));
                }
            }
        }


        /////////////////////////////////////////////////////////////////
        // ---------------------- Arena Commands --------------------- //
        /////////////////////////////////////////////////////////////////

        @Subcommand("arena")
        public class ArenaCommands extends BaseCommand {

            @Default
            public void onDefault(Player player) {
                player.sendMessage(Colorize.format("&cUSAGE: /mpe admin arena <Create|Delete|List|Edit>"));
            }

            @Subcommand("list")
            @Description("Sets the lobby spawn")
            public void onListArenas(CommandSender commandSender) {
                if (gameManager.arenaManager().getArenas().isEmpty()) {
                    commandSender.sendMessage(Colorize.format("&cThere are no disponible arenas."));
                } else {
                    commandSender.sendMessage(Colorize.format("&aThe disponible arenas are:"));
                    for (Arena arena : gameManager.arenaManager().getArenas()) {
                        commandSender.sendMessage(Colorize.format("&a - " + arena.name() + " - &aState: " + Colorize.format(arena.arenaState().getGameStateEnum().getDisplayedName()) + " &a- " + "&a" + arena.getCurrentPlayers() + "&7/&a" + arena.maxPlayers()));
                    }
                }

            }

            @Subcommand("create")
            @Description("Attempts to create a new arena")
            public void onArenaCreate(Player player) {
                gameManager.setupWizardManager().startWizard(player, null);
            }


            @Subcommand("edit")
            @Description("Attempts to edit an arena")
            public void onArenaEdit(Player player, String[] args) {
                if (args.length == 0) {
                    player.sendMessage(Colorize.format("&cUSAGE: /mpe admin arena edit <arena>"));
                    return;
                }
                String arenaInput = args[0];
                Optional<Arena> optionalArena = gameManager.arenaManager().findArena(arenaInput);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&4No arenas with that name were found"));
                } else {
                    player.teleport(optionalArena.get().centerLocation());
                    gameManager.setupWizardManager().startWizard(player, optionalArena.get());
                }
            }

            @Subcommand("delete")
            @Description("Attempts to delete an arena")
            public void onArenaDelete(Player player, String[] args) {
                if (args.length == 0) {
                    player.sendMessage(Colorize.format("&cUSAGE: /mpe admin arena delete <arena>"));
                    return;
                }
                String arenaInput = args[0];
                Optional<Arena> optionalArena = gameManager.arenaManager().findArena(arenaInput);
                if (!optionalArena.isPresent()) {
                    player.sendMessage(Colorize.format("&4No arenas with that name were found"));
                } else {
                    String name = optionalArena.get().name();
                    gameManager.arenaManager().deleteArena(optionalArena.get());
                    gameManager.configManager().deleteArena(optionalArena.get());
                    player.sendMessage(Colorize.format("&aSuccesfully deleted the arena " + name));
                }

            }
        }
    }
}
