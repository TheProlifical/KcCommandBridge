package com.puddingkc;

import com.github.puregero.multilib.MultiLib;
import com.puddingkc.commands.ReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KcCommandBridge extends JavaPlugin implements Listener {

    private Set<Pattern> commandPatterns;
    private boolean deBug = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        Objects.requireNonNull(getCommand("commandbridge")).setExecutor(new ReloadCommand(this));
        getServer().getPluginManager().registerEvents(this,this);
    }

    public void loadConfig() {
        reloadConfig();

        deBug = getConfig().getBoolean("debug",false);

        commandPatterns = new HashSet<>();
        List<String> commands = getConfig().getStringList("commands");

        for (String command : commands) {
            String regex = command.replace("*", "(.*)");
            commandPatterns.add(Pattern.compile("^/" + regex + "$"));
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String playerCommand = event.getMessage();
        for (Pattern pattern : commandPatterns) {
            Matcher matcher = pattern.matcher(playerCommand);
            if (matcher.matches()) {
                String extracted = matcher.group(1);

                deBugLog("Player " + event.getPlayer().getName() + " entered the command " + playerCommand + ", which matches successfully. The forwarding program has started execution.");
                deBugLog("The extracted custom string is: " + extracted);

                relayCommand(event, playerCommand, extracted);
                return;
            }
        }
    }

    private void relayCommand(PlayerCommandPreprocessEvent event, String command, String playerName) {
        if (MultiLib.isExternalPlayer(Bukkit.getPlayer(playerName))) {
            MultiLib.chatOnOtherServers(event.getPlayer(), command);

            deBugLog("Successfully found the target player " + playerName + " on another server, command forwarding was successful.");

            event.setCancelled(true);
        }
    }

    private void deBugLog(String string) {
        if (deBug) {
            getLogger().info("[DEBUG] " + string);
        }
    }
}