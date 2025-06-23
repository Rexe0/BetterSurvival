package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Stopwatch extends Item {
    public Stopwatch() {
        super(Material.CLOCK, ChatColor.GREEN+"Stopwatch", "STOPWATCH");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A device that displays");
        lore.add(ChatColor.GRAY+"your current speed in km/h.");
        return lore;
    }

    @Override
    public void holdCheck(Player player) {
        Location location = player.getLocation();

        new BukkitRunnable() {
            @Override
            public void run() {
                double distance = player.getLocation().distance(location);
                double speed = Math.round((distance*3.6)/0.25 * 100)/100d; // Gets the speed of the player in km/h in 2 decimal places
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.AQUA+"Speed: "+ChatColor.BLUE+speed+" km/h"));
            }
        }.runTaskLater(BetterSurvival.getInstance(), 5);
    }

}
