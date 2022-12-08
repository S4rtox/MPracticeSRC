package me.s4rtox.mskywars;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.s4rtox.mskywars.commands.SWReload;
import me.s4rtox.mskywars.commands.SetSpawn;
import me.s4rtox.mskywars.commands.Spawn;
import me.s4rtox.mskywars.handlers.BuildMode;
import me.s4rtox.mskywars.handlers.JoinItemsHandler;
import me.s4rtox.mskywars.handlers.LobbyHandler;
import me.s4rtox.mskywars.util.ConfigUtil;
import me.s4rtox.mskywars.util.PapiFormatter;
import me.s4rtox.mskywars.util.SpawnUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.basic.BasicButtonUI;
import java.io.File;
import java.io.IOException;

public final class MSkywars extends JavaPlugin {
    private BukkitAudiences adventure;
    private ConfigUtil configUtil;
    private SpawnUtil spawnUtil;
    private YamlDocument config;
    private YamlDocument spawnConfig;
    private YamlDocument messagesConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        //AdventureApi SetUp
        this.adventure = BukkitAudiences.create(this);
        //Bungeecord channel setup
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        //PlaceholderApi Setup
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PapiFormatter.setPapiStatus(true);
            getLogger().info(ChatColor.translateAlternateColorCodes('&', "&aPlaceholderAPI detected!, enabling PAPI placeholders."));
        } else {
            PapiFormatter.setPapiStatus(false);
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&ePlaceholderAPI not detected!, disabling PAPI placeholders."));
        }
        configSetup();
        utilSetup();
        commandSetup();
        handlerSetup();

        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&a <----------------------------->"));
        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&a     [SHC] correctly loaded!"));
        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&a         made by: S4rtox"));
        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&a <----------------------------->"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void utilSetup(){
        configUtil = new ConfigUtil(this);
        spawnUtil = new SpawnUtil(this);
    }

    public void configSetup(){
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            spawnConfig = YamlDocument.create(new File(getDataFolder(), "spawn.yml"), getResource("spawn.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
            messagesConfig = YamlDocument.create(new File(getDataFolder(), "messages.yml"), getResource("messages.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handlerSetup(){
        new LobbyHandler(this);
        new BuildMode(this);
        new JoinItemsHandler(this);
    }
    public void commandSetup(){
        getCommand("build").setExecutor(new BuildMode(this));
        getCommand("setspawn").setExecutor(new SetSpawn(this));
        getCommand("spawn").setExecutor(new Spawn(this));
        getCommand("mskywarsreload").setExecutor(new SWReload(this));

    }

    public YamlDocument getDefaultConfig(){
        return config;
    }
    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
    public SpawnUtil getSpawnUtil(){
        return spawnUtil;
    }

    public YamlDocument getSpawnConfig() {
        return spawnConfig;
    }

    public YamlDocument getMessagesConfig() {
        return messagesConfig;
    }
}
