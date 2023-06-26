package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.IdentifyUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WanderingTrader implements Listener {
    public static void itemCheck(Player player) {
        stopWatchCheck(player);
        metalDetectorCheck(player);
    }

    private static void stopWatchCheck(Player player) {
        if (!(IdentifyUtil.isItem(player.getEquipment().getItemInMainHand(), ChatColor.GREEN+"Stopwatch")
        || IdentifyUtil.isItem(player.getEquipment().getItemInOffHand(), ChatColor.GREEN+"Stopwatch"))) return;
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

    private static void metalDetectorCheck(Player player) {
        if (!(IdentifyUtil.isItem(player.getEquipment().getItemInMainHand(), ChatColor.GREEN+"Metal Detector")
                || IdentifyUtil.isItem(player.getEquipment().getItemInOffHand(), ChatColor.GREEN+"Metal Detector"))) return;
        Location location = player.getLocation();
        location.setY(location.getY()-1);

        List<Location> locs = new LinkedList<>();
        locs.add(location.getBlock().getLocation().add(0.5, 0, 0.5));

        List<Location> prevLocs = new ArrayList<>();

        double distance = 4;

        int k = 4;
        while (k > 0) {
            List<Location> toRemove = new ArrayList<>(locs);
            prevLocs.addAll(locs);
            // Check for metal ore
            boolean found = false;
            for (Location loc : locs) {
                if (!loc.getBlock().getType().toString().contains("ORE")) continue;
                found = true;
                distance = loc.add(0, 1, 0).distance(player.getLocation());
                break;
            }
            if (found) break;
            locs.clear();
            // If no ore found, check adjacent blocks on next iteration
            for (Location loc : toRemove)
                for (int i = 0; i < 5; i++) {
                    Location newLoc = loc.clone();
                    switch (i) {
                        case 0 -> newLoc.add(1, 0, 0);
                        case 1 -> newLoc.add(0, 0, 1);
                        case 2 -> newLoc.add(-1, 0, 0);
                        case 3 -> newLoc.add(0, 0, -1);
                        case 4 -> newLoc.add(0, -1, 0);
                    }
                    if (!prevLocs.contains(newLoc)) locs.add(newLoc);
                }
            k--;
        }
        if (k > 0)
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.1f*k+1.6f);


        String actionBar = ChatColor.GREEN+"";
        for (int i = 0; i < 20; i++) {
            if (i == Math.max(0, Math.round(5*(4-distance)))) actionBar += ChatColor.RED+"";
            actionBar+="|";
        }

        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBar));

    }




    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.WanderingTrader trader)) return;
        MerchantRecipe trade = new MerchantRecipe(RandomUtil.getRandom().nextBoolean() ? getStopwatch() : getMetalDetector(), 1);
        trade.addIngredient(new ItemStack(Material.EMERALD, 30));
        trade.addIngredient(new ItemStack(trade.getResult().getType(), 1));
        trader.setRecipe(trader.getRecipeCount()-2, trade);
    }

    private ItemStack getStopwatch() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Stopwatch");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack getMetalDetector() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Metal Detector");
        item.setItemMeta(meta);
        return item;
    }
}
