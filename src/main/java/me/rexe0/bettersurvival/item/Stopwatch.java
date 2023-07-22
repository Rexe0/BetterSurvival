package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Stopwatch extends Item {
    public Stopwatch() {
        super(Material.CLOCK, ChatColor.GREEN+"Stopwatch", "STOPWATCH");
    }

    public static void stopWatchCheck(Player player) {
        String ID = ItemType.STOPWATCH.getItem().getID();
        if (!(ItemDataUtil.isItem(player.getEquipment().getItemInMainHand(), ID)
                || ItemDataUtil.isItem(player.getEquipment().getItemInOffHand(), ID))) return;
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
