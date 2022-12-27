package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.Data;
import me.s4rtox.mpractice.MPractice;

@Data
public class GameManager {
    private final MPractice plugin;
    private final ArenaManager arenaManager;
    private final ArenaConfigurationManager configManager;
    private final SetupWizardManager setupWizardManager;
    private final PlayerRollbackManager rollbackManager;

    public GameManager(MPractice plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
        this.configManager = new ArenaConfigurationManager(this);
        this.arenaManager = new ArenaManager(configManager.loadArenas());
        this.setupWizardManager = new SetupWizardManager(this);


        plugin.getServer().getPluginManager().registerEvents(setupWizardManager, plugin);
    }

}
