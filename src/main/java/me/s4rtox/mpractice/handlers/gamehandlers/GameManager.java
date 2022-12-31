package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.Data;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.chesthandlers.ChestManager;

@Data
public class GameManager {
    private final MPractice plugin;
    private final ArenaManager arenaManager;
    private final ArenaConfigurationManager configManager;
    private final SetupWizardManager setupWizardManager;
    private final PlayerRollbackManager rollbackManager;
    private final ChestManager chestManager;

    public GameManager(MPractice plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
        this.configManager = new ArenaConfigurationManager(this);
        this.chestManager = new ChestManager(plugin);
        this.arenaManager = new ArenaManager(configManager.loadArenas());
        this.setupWizardManager = new SetupWizardManager(this);


        plugin.getServer().getPluginManager().registerEvents(setupWizardManager, plugin);
    }

}
