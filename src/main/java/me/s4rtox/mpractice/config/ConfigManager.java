package me.s4rtox.mpractice.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.util.Colorize;

import java.util.Collections;
import java.util.List;

public class ConfigManager {
    private final YamlDocument config;
    private final YamlDocument messages;

    /* Config Settings */
    //World Section
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_RAIN;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_PVP;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_HUNGER;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_MOBSPAWNING;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_MOBGRIEFING;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_FIRE;
    @Getter
    private boolean C_LOBBYWORLD_KEEPINVENTORY;
    @Getter
    private boolean C_LOBBYWORLD_DISABLE_FALLDAMAGE;
    @Getter
    private boolean C_LOBBYWORLD_BUILDMODE_ENABLED;
    @Getter
    private boolean C_LOBBYWORLD_BUILDMODE_INTERACTIONS;
    @Getter
    private List<String> C_LOBBYWORLD_ENABLEDWORLDS;

    /* End of Config */

    /* Messages */
    private String MS_PREFIX;
    @Getter
    private String MS_RELOAD_SUCCESS;
    @Getter
    private String MS_CONSOLE_COMMAND_EXECUTOR_ERROR;
    @Getter
    private String MS_NO_PERMISSION;
    @Getter
    private String MS_COOLDOWN;
    @Getter
    private String MS_PLAYER_NOTONLINE;
    @Getter
    private String MS_SPAWN_SETSPAWN;
    @Getter
    private String MS_SPAWN_NOT_SET_YET;
    @Getter
    private String MS_BUILDMODE_OFF;
    @Getter
    private String MS_BUILDMODE_ON;

    /* End of Messages */

    /* Permissions */
    @Getter
    private String P_ADMIN_RELOAD;
    @Getter
    private String P_ADMIN_SPAWN_SET;
    @Getter
    private String P_ADMIN_BUILDMODE;
    /* End of Permissions */

    public ConfigManager(MPractice plugin) {
        this.config = plugin.getDefaultConfig();
        this.messages = plugin.getMessagesConfig();
        loadConfig();

    }

    public void loadConfig() {
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
        C_LOBBYWORLD_DISABLE_RAIN = config.getBoolean("LobbyRules.DisableRain", false);
        C_LOBBYWORLD_DISABLE_DAYLIGHTCYCLE = config.getBoolean("LobbyRules.DisableDaylightCycle", false);
        C_LOBBYWORLD_DISABLE_PVP = config.getBoolean("LobbyRules.DisablePvP", false);
        C_LOBBYWORLD_DISABLE_HUNGER = config.getBoolean("LobbyRules.DisableHunger", false);
        C_LOBBYWORLD_DISABLE_MOBSPAWNING = config.getBoolean("LobbyRules.DisableMobSpawning", false);
        C_LOBBYWORLD_DISABLE_MOBGRIEFING = config.getBoolean("LobbyRules.DisableMobGriefing", false);
        C_LOBBYWORLD_DISABLE_FIRE = config.getBoolean("LobbyRules.DisableFireSpread", false);
        C_LOBBYWORLD_KEEPINVENTORY = config.getBoolean("LobbyRules.KeepInventory", false);
        C_LOBBYWORLD_DISABLE_FALLDAMAGE = config.getBoolean("LobbyRules.DisableFallDamage", false);
        C_LOBBYWORLD_BUILDMODE_ENABLED = config.getBoolean("LobbyRules.BuildMode.Enabled", false);
        C_LOBBYWORLD_BUILDMODE_INTERACTIONS = config.getBoolean("LobbyRules.BuildMode.Interactions-Disabled", false);
    }

    public String getMessage(String path) {
        return Colorize.format(MS_PREFIX + messages.getString(path, ""));
    }
}