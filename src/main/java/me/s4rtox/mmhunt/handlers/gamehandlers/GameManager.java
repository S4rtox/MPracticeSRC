package me.s4rtox.mmhunt.handlers.gamehandlers;

import lombok.Data;
import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.HunterTrackerHandler;
import me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers.ChestManager;

@Data
public class GameManager {
    private final MMHunt plugin;
    private final ArenaManager arenaManager;
    private final ArenaConfigurationManager configManager;
    private final SetupWizardManager setupWizardManager;
    private final PlayerRollbackManager rollbackManager;
    private final WorldTablistManager worldTablistManager;
    private final ChestManager chestManager;
    private final HunterTrackerHandler trackerHandler;

    public GameManager(MMHunt plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
        this.configManager = new ArenaConfigurationManager(this);
        this.chestManager = new ChestManager(plugin);
        this.trackerHandler = new HunterTrackerHandler();
        this.worldTablistManager = new WorldTablistManager(plugin);
        this.arenaManager = new ArenaManager(configManager.loadArenas());
        this.setupWizardManager = new SetupWizardManager(this);
    }

}
