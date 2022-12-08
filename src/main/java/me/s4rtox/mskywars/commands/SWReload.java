package me.s4rtox.mskywars.commands;

import me.s4rtox.mskywars.MSkywars;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SWReload implements CommandExecutor {
    private final MSkywars plugin;

    public SWReload(MSkywars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission(plugin.getConfigUtil().P_ADMIN_RELOAD())){
            sender.sendMessage(plugin.getConfigUtil().MS_NO_PERMISSION());
            return true;
        }
        try {
            plugin.getDefaultConfig().reload();
            plugin.getSpawnConfig().reload();
            plugin.getMessagesConfig().reload();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eAttempting to reload plugin!, its to restart if updating menus were added/removed"));
            plugin.getConfigUtil().loadConfig();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere has been an error reloading the plugin, check the console for details"));
            throw new RuntimeException(e);
        }
        sender.sendMessage(plugin.getConfigUtil().MS_RELOAD_SUCCESS());
        return true;


    }
}
