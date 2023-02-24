package me.s4rtox.mmhunt.handlers.lobbyhandlers;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.util.BungeeWrapper;
import org.bukkit.scheduler.BukkitRunnable;

public class DisplaysDataRefresher extends BukkitRunnable {
    private final MMHunt plugin;
    public DisplaysDataRefresher(MMHunt plugin){
        this.plugin = plugin;
        this.runTaskTimer(plugin,0, 15*20);
    }

    @Override
    public void run() {
        BungeeWrapper.sendPlayerCountAll();
        //BungeeWrapper.sendPlayerCount("Survival");
        plugin.getLobbyHandler().updateAllTablist("&7&l&m============================================================" + "\n" + "\n"
                        + "&6&lMINE&b&lGUARDS" + "\n"
                        + "&7(Te encuentras en MANHUNT)" + "\n" + "\n" +
                        "&fActualmente hay &b" + BungeeWrapper.getPlayerCount("ALL") + "&f jugadores Conectados" + "\n",
                """

                        &6&lDis&b&lcord&7:&f discord.gg/n6XuXxGRP
                        &6&lTie&b&lnda&7: &ftienda.mineguards.com
                        &6&lTwi&b&ltter&7: &f@MineguardsNET

                        &7&l&m============================================================""");
    }
}
