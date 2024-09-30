package com.puddingkc.commands;

import com.puddingkc.KcCommandBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final KcCommandBridge plugin;
    public ReloadCommand(KcCommandBridge plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {

        if (sender.hasPermission("kccommandbridge.reload")) {
            plugin.loadConfig();
            sender.sendMessage("§f:D §7Configuration file reloaded successfully.");
            return true;
        }

        return false;
    }

}
