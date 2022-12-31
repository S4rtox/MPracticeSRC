package me.s4rtox.mpractice;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.api.SlimePlugin;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.NonNull;
import me.s4rtox.mpractice.commands.PracticeCommands;
import me.s4rtox.mpractice.config.ConfigManager;
import me.s4rtox.mpractice.handlers.ScoreboardManager;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.lobbyhandlers.JoinItemsHandler;
import me.s4rtox.mpractice.handlers.lobbyhandlers.LobbyHandler;
import me.s4rtox.mpractice.handlers.lobbyhandlers.SpawnSetter;
import me.s4rtox.mpractice.util.Colorize;
import me.s4rtox.mpractice.util.PapiFormatter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MPractice extends JavaPlugin {

    public final String version = "1.0.0";
    private BukkitAudiences adventure;
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

    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        //AdventureApi SetUp
        this.adventure = BukkitAudiences.create(this);

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

        SlimePlugin slimeWorldManager = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

        commandManager = new PaperCommandManager(this);

        configSetup();
        utilSetup();
        handlerSetup();
        commandSetup();


        Bukkit.getLogger().info(Colorize.format("&a <----------------------------->"));
        Bukkit.getLogger().info(Colorize.format("&a     [MPE] correctly loaded!"));
        Bukkit.getLogger().info(Colorize.format("&a         made by: S4rtox"));
        Bukkit.getLogger().info(Colorize.format("&a <----------------------------->"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void utilSetup() {
        scoreboardManager = new ScoreboardManager(this);
        configManager = new ConfigManager(this);
        spawnSetter = new SpawnSetter(this);
    }

    public void configSetup() {
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            spawnConfig = YamlDocument.create(new File(getDataFolder(), "spawn.yml"), getResource("spawn.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            messagesConfig = YamlDocument.create(new File(getDataFolder(), "messages.yml"), getResource("messages.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            arenaConfig = YamlDocument.create(new File(getDataFolder(), "arenas.yml"));
            chestConfig = YamlDocument.create(new File(getDataFolder(), "chests.yml"), getResource("chests.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handlerSetup() {

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
