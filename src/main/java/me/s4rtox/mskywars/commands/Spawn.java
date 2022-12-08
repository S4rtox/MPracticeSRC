package me.s4rtox.mskywars.commands;

import me.s4rtox.mskywars.MSkywars;
import me.s4rtox.mskywars.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {
    private final MSkywars plugin;
    private final ConfigUtil config;

    public Spawn(MSkywars plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigUtil();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.MS_CONSOLE_COMMAND_EXECUTOR_ERROR());
            return true;
        }

        Player player = (Player)sender;
        plugin.getSpawnUtil().teleport(player);
        return true;
    }

}
