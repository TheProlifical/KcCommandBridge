package com.puddingkc;

import com.github.puregero.multilib.MultiLib;
import com.puddingkc.commands.ReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.regex.Pattern;

public class KcCommandBridge extends JavaPlugin implements Listener {

    private Pattern commandPattern;
    private boolean deBug = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        Objects.requireNonNull(getCommand("commandbridge")).setExecutor(new ReloadCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void loadConfig() {
        reloadConfig();
        deBug = getConfig().getBoolean("debug", false);
        commandPattern = Pattern.compile("^/(\\S+)(\\s.*)?");
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String playerCommand = event.getMessage();
        if (commandPattern.matcher(playerCommand).matches()) {
            deBugLog("Player " + event.getPlayer().getName() + " entered the command " + playerCommand + ", which matches successfully. The forwarding program has started execution.");
            relayCommand(event, playerCommand);
        }
    }

    private void relayCommand(PlayerCommandPreprocessEvent event, String command) {
        String playerName = event.getPlayer().getName();
        if (MultiLib.isExternalPlayer(Bukkit.getPlayer(playerName))) {
            MultiLib.chatOnOtherServers(event.getPlayer(), command);
            deBugLog("Successfully found the target player " + playerName + " on another server, command forwarding was successful.");
            event.setCancelled(true);
        }
    }

    private void deBugLog(String message) {
        if (deBug) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}
