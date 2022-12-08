package me.s4rtox.mskywars.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.s4rtox.mskywars.MSkywars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.List;

public class ConfigUtil {
    private MSkywars plugin;
    private final YamlDocument config;
    private final YamlDocument messages;

    /* Config Settings */
    //World Section
    private boolean C_LOBBYWORLD_DISABLE_RAIN;
    private boolean C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE;
    private boolean C_LOBBYWORLD_DISABLE_PVP;
    private boolean C_LOBBYWORLD_DISABLE_HUNGER;
    private boolean C_LOBBYWORLD_DISABLE_MOBSPAWNING;
    private boolean C_LOBBYWORLD_DISABLE_MOBGRIEFING;
    private boolean C_LOBBYWORLD_DISABLE_FIRE;
    private boolean C_LOBBYWORLD_KEEPINVENTORY;
    private boolean C_LOBBYWORLD_DISABLE_FALLDAMAGE;
    private boolean C_LOBBYWORLD_BUILDMODE_ENABLED;
    private boolean C_LOBBYWORLD_BUILDMODE_INTERACTIONS;

    private List<String> C_LOBBYWORLD_ENABLEDWORLDS;

    /* End of Config */

    /* Messages */
    private String MS_PREFIX;
    private String MS_RELOAD_SUCCESS;
    private String MS_CONSOLE_COMMAND_EXECUTOR_ERROR;
    private String MS_NO_PERMISSION;
    private String MS_COOLDOWN;
    private String MS_PLAYER_NOTONLINE;
    private String MS_SPAWN_SETSPAWN;
    private String MS_SPAWN_NOT_SET_YET;
    private String MS_BUILDMODE_OFF;
    private String MS_BUILDMODE_ON;

    /* End of Messages */

    /* Permissions */
    private String P_ADMIN_RELOAD;
    private String P_ADMIN_SPAWN_SET;
    private String P_ADMIN_BUILDMODE;
    /* End of Permissions */

    public ConfigUtil(MSkywars plugin){
        this.plugin = plugin;
        this.config = plugin.getDefaultConfig();
        this.messages = plugin.getMessagesConfig();
        loadConfig();

    }

    public void loadConfig(){
        /* Sets up the messages */
        MS_PREFIX = messages.getString("Plugin-Prefix", "");

        MS_RELOAD_SUCCESS = getMessage("Plugin-Reload-Success");
        MS_CONSOLE_COMMAND_EXECUTOR_ERROR = getMessage("Console-Command-Executor-Error");
        MS_NO_PERMISSION = getMessage("No-Permission-Error");
        MS_COOLDOWN = getMessage("Cooldown-Message");
        MS_PLAYER_NOTONLINE = getMessage("Player-Not-Online-Error");
        MS_SPAWN_SETSPAWN = getMessage("Spawn-Set-Success");
        MS_SPAWN_NOT_SET_YET = getMessage("Spawn-Not-Set-Yet");
        MS_BUILDMODE_OFF = getMessage("Build-Mode-Toggled-Off");
        MS_BUILDMODE_ON = getMessage("Build-Mode-Toggled-On");
        /* End of messages setup */

        /* Sets up permissions */
        P_ADMIN_BUILDMODE = "shc.admin.buildmode";
        P_ADMIN_RELOAD = "shc.admin.reload";
        P_ADMIN_SPAWN_SET = "shc.admin.spawn.set";
        /* End of Permissions */
        
        /* Sets up the config */
        C_LOBBYWORLD_ENABLEDWORLDS = config.getStringList("LobbyWorlds", Collections.singletonList(" "));
        for(String s : C_LOBBYWORLD_ENABLEDWORLDS){
            Bukkit.getLogger().info(s);
        }
        C_LOBBYWORLD_DISABLE_RAIN = config.getBoolean("LobbyRules.DisableRain", false);
        C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE = config.getBoolean("LobbyRules.DisableDaylightCycle", false);
        C_LOBBYWORLD_DISABLE_PVP = config.getBoolean("LobbyRules.DisablePvP", false);
        C_LOBBYWORLD_DISABLE_HUNGER = config.getBoolean("LobbyRules.DisableHunger",false);
        C_LOBBYWORLD_DISABLE_MOBSPAWNING = config.getBoolean("LobbyRules.DisableMobSpawning", false);
        C_LOBBYWORLD_DISABLE_MOBGRIEFING = config.getBoolean("LobbyRules.DisableMobGriefing", false);
        C_LOBBYWORLD_DISABLE_FIRE = config.getBoolean("LobbyRules.DisableFireSpread", false);
        C_LOBBYWORLD_KEEPINVENTORY = config.getBoolean("LobbyRules.KeepInventory", false);
        C_LOBBYWORLD_DISABLE_FALLDAMAGE = config.getBoolean("LobbyRules.DisableFallDamage", false);
        C_LOBBYWORLD_BUILDMODE_ENABLED = config.getBoolean("LobbyRules.BuildMode.Enabled", false);
        C_LOBBYWORLD_BUILDMODE_INTERACTIONS = config.getBoolean("LobbyRules.BuildMode.Interactions-Disabled", false);
    }

    public String getMessage(String path){
        return ChatColor.translateAlternateColorCodes('&', MS_PREFIX + messages.getString(path,""));
    }


    public boolean C_LOBBYWORLD_DISABLE_RAIN() {
        return C_LOBBYWORLD_DISABLE_RAIN;
    }

    public boolean C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE() {
        return C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE;
    }

    public boolean C_LOBBYWORLD_DISABLE_PVP() {
        return C_LOBBYWORLD_DISABLE_PVP;
    }

    public boolean C_LOBBYWORLD_DISABLE_HUNGER() {
        return C_LOBBYWORLD_DISABLE_HUNGER;
    }

    public boolean C_LOBBYWORLD_DISABLE_MOBSPAWNING() {
        return C_LOBBYWORLD_DISABLE_MOBSPAWNING;
    }

    public boolean C_LOBBYWORLD_DISABLE_MOBGRIEFING() {
        return C_LOBBYWORLD_DISABLE_MOBGRIEFING;
    }

    public boolean C_LOBBYWORLD_DISABLE_FIRE() {
        return C_LOBBYWORLD_DISABLE_FIRE;
    }

    public boolean C_LOBBYWORLD_KEEPINVENTORY() {
        return C_LOBBYWORLD_KEEPINVENTORY;
    }

    public boolean C_LOBBYWORLD_BUILDMODE_ENABLED() {
        return C_LOBBYWORLD_BUILDMODE_ENABLED;
    }

    public boolean C_LOBBYWORLD_BUILDMODE_INTERACTIONS() {
        return C_LOBBYWORLD_BUILDMODE_INTERACTIONS;
    }

    public List<String> C_LOBBYWORLD_ENABLEDWORLDS() {
        return C_LOBBYWORLD_ENABLEDWORLDS;
    }

    public String MS_RELOAD_SUCCESS() {
        return MS_RELOAD_SUCCESS;
    }

    public String MS_CONSOLE_COMMAND_EXECUTOR_ERROR() {
        return MS_CONSOLE_COMMAND_EXECUTOR_ERROR;
    }

    public String MS_NO_PERMISSION() {
        return MS_NO_PERMISSION;
    }

    public String MS_COOLDOWN() {
        return MS_COOLDOWN;
    }

    public String MS_PLAYER_NOTONLINE() {
        return MS_PLAYER_NOTONLINE;
    }

    public String MS_SPAWN_SETSPAWN() {
        return MS_SPAWN_SETSPAWN;
    }

    public String MS_SPAWN_NOT_SET_YET() {
        return MS_SPAWN_NOT_SET_YET;
    }

    public String MS_BUILDMODE_OFF() {
        return MS_BUILDMODE_OFF;
    }

    public String MS_BUILDMODE_ON() {
        return MS_BUILDMODE_ON;
    }

    public String P_ADMIN_RELOAD() {
        return P_ADMIN_RELOAD;
    }

    public String P_ADMIN_SPAWN_SET() {
        return P_ADMIN_SPAWN_SET;
    }

    public String P_ADMIN_BUILDMODE() {
        return P_ADMIN_BUILDMODE;
    }

    public boolean C_LOBBYWORLD_DISABLE_FALLDAMAGE() {
        return C_LOBBYWORLD_DISABLE_FALLDAMAGE;
    }
}