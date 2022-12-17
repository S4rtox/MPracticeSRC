package me.s4rtox.mpractice.handlers.gamehandlers;

import lombok.Data;
import me.s4rtox.mpractice.MPractice;

@Data
public class GameManager {
    private final MPractice plugin;
    private final ArenaManager arenaManager;
    private final ArenaConfigurationManager configManager;
    private final SetupWizardManager setupWizardManager;

    public GameManager(MPractice plugin){
        this.plugin = plugin;

        this.configManager = new ArenaConfigurationManager(plugin);
        this.arenaManager = new ArenaManager(configManager.loadArenas());
        this.setupWizardManager = new SetupWizardManager(this);

        plugin.getServer().getPluginManager().registerEvents(setupWizardManager,plugin);
    }

}
