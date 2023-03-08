package me.s4rtox.mmhunt;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.api.SlimePlugin;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import lombok.NonNull;
import me.s4rtox.mmhunt.commands.PracticeCommands;
import me.s4rtox.mmhunt.config.ConfigManager;
import me.s4rtox.mmhunt.handlers.ScoreboardManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.lobbyhandlers.JoinItemsHandler;
import me.s4rtox.mmhunt.handlers.lobbyhandlers.LobbyHandler;
import me.s4rtox.mmhunt.handlers.lobbyhandlers.SpawnSetter;
import me.s4rtox.mmhunt.util.BungeeWrapper;
import me.s4rtox.mmhunt.util.Colorize;
import me.s4rtox.mmhunt.util.PapiFormatter;
import me.s4rtox.mmhunt.util.WorkloadRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MMHunt extends JavaPlugin {

    public static final String version = "1.1.0";
    private ConfigManager configManager;
    private SpawnSetter spawnSetter;
    private YamlDocument config;
    private YamlDocument spawnConfig;
    private YamlDocument messagesConfig;
    private YamlDocument arenaConfig;
    private YamlDocument chestConfig;
    private PaperCommandManager commandManager;
    private ScoreboardManager scoreboardManager;
    private GameManager gameManager;
    private LobbyHandler lobbyHandler;
    @Getter
    private final WorkloadRunnable workloadRunnable = new WorkloadRunnable();

    //TODO: Scoreboards(animated?),test and fix chest system, add chest config reload, add bossbar.
    @Override
    public void onEnable() {

        //Bungeecord channel setup

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //PlaceholderApi Setup

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PapiFormatter.setPapiStatus(true);
            getLogger().info(Colorize.format("&aPlaceholderAPI detected!, enabling PAPI placeholders."));
        } else {
            PapiFormatter.setPapiStatus(false);
            getLogger().warning(Colorize.format("&ePlaceholderAPI not detected!, disabling PAPI placeholders."));
        }

        commandManager = new PaperCommandManager(this);

        configSetup();
        utilSetup();
        handlerSetup();
        commandSetup();


        Bukkit.getLogger().info(Colorize.format("&a <----------------------------->"));
        Bukkit.getLogger().info(Colorize.format("&a     [MPE] correctly loaded!"));
        Bukkit.getLogger().info(Colorize.format("&a         made by: S4rtox"));
        Bukkit.getLogger().info(Colorize.format("&a <----------------------------->"));

        Bukkit.getScheduler().runTaskTimer(this, this.workloadRunnable, 1, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void utilSetup() {
        scoreboardManager = new ScoreboardManager(this);
        configManager = new ConfigManager(this);
        spawnSetter = new SpawnSetter(this);
    }

    public void configSetup() {
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            spawnConfig = YamlDocument.create(new File(getDataFolder(), "spawn.yml"), getResource("spawn.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            messagesConfig = YamlDocument.create(new File(getDataFolder(), "messages.yml"), getResource("messages.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            arenaConfig = YamlDocument.create(new File(getDataFolder(), "arenas.yml"));
            chestConfig = YamlDocument.create(new File(getDataFolder(), "chests.yml"), getResource("chests.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handlerSetup() {
        new BungeeWrapper(this);
        lobbyHandler = new LobbyHandler(this);
        new JoinItemsHandler(this);
        gameManager = new GameManager(this);
    }

    public void commandSetup() {
        commandManager.registerCommand(new PracticeCommands(this));
    }

    public YamlDocument getDefaultConfig() {
        return config;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SpawnSetter getSpawnSetter() {
        return spawnSetter;
    }

    public YamlDocument getSpawnConfig() {
        return spawnConfig;
    }

    public YamlDocument getMessagesConfig() {
        return messagesConfig;
    }


    public GameManager getGameManager() {
        return gameManager;
    }

    public YamlDocument getArenaConfig() {
        return arenaConfig;
    }
    public LobbyHandler getLobbyHandler() {
        return lobbyHandler;
    }

    public YamlDocument getChestConfig() {
        return chestConfig;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
