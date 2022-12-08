package me.s4rtox.mskywars.commands;

import me.s4rtox.mskywars.MSkywars;
import me.s4rtox.mskywars.util.ConfigUtil;
import me.s4rtox.mskywars.util.SpawnUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
    private final SpawnUtil spawnUtil;
    private final ConfigUtil config;

    public SetSpawn(MSkywars plugin) {
        this.spawnUtil = plugin.getSpawnUtil();
        this.config = plugin.getConfigUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.MS_CONSOLE_COMMAND_EXECUTOR_ERROR());
            return true;
        }

        Player player = (Player)sender;
        spawnUtil.set(player.getLocation());

        player.sendMessage(config.MS_SPAWN_SETSPAWN());
        return true;
    }
}
