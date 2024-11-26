package me.rexe0.bettersurvival.config;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigLoader implements CommandExecutor {
    private FileConfiguration config;

    private boolean mendingChanges;
    private boolean blizzardHarmful;

    public ConfigLoader(FileConfiguration config) {
        this.config = config;
        init();
    }

    private void init() {
        mendingChanges = config.getBoolean("enable-mending-changes");
        blizzardHarmful = config.getBoolean("enable-blizzard-harmful");
    }

    public boolean isMendingChanges() {
        return mendingChanges;
    }

    public boolean isBlizzardHarmful() {
        return blizzardHarmful;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        File file = new File(BetterSurvival.getInstance().getDataFolder(), File.separator+"config.yml");
        if (!file.exists()) {
            BetterSurvival.getInstance().saveDefaultConfig();
            return true;
        }
        BetterSurvival.getInstance().reloadConfig();
        config = YamlConfiguration.loadConfiguration(file);

        init();
        sender.sendMessage(ChatColor.GREEN+"Successfully reload the config.yml of BetterSurvival.");
        return true;
    }
}
