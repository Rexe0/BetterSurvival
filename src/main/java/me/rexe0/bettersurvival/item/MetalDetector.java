package me.rexe0.bettersurvival.item;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MetalDetector extends Item {
    private static final List<Material> METALS = List.of(
            Material.DIAMOND, Material.EMERALD, Material.LAPIS_LAZULI, Material.REDSTONE,
            Material.RAW_GOLD, Material.GOLD_INGOT,
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT,
            Material.COPPER_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK,
            Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.LAPIS_BLOCK,
            Material.REDSTONE_BLOCK, Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK
    );

    public MetalDetector() {
        super(Material.IRON_HOE, ChatColor.GREEN+"Metal Detector", "METAL_DETECTOR");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A device that detects");
        lore.add(ChatColor.GRAY+"metal and ores underneath");
        lore.add(ChatColor.GRAY+"you.");
        return lore;
    }

    @Override
    public void holdCheck(Player player) {
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
                if (!hasMetal(loc.getBlock())) continue;
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

    private boolean hasMetal(Block block) {
        if (block.getType().toString().contains("ORE")) return true;
        if (block.getState() instanceof BrushableBlock brushable) {
            Material mat = brushable.getItem().getType();
            if (mat.toString().contains("ORE")) return true;
            return METALS.contains(mat);
        }
        return false;
    }
}
