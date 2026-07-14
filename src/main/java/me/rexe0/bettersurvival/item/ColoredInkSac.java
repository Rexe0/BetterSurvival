package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ColoredInkSac extends Item {
    public ColoredInkSac(ChatColor color) {
        super(Material.INK_SAC, color+"Colored Ink Sac", "COLORED_INK_SAC");
    }


    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Allows you to change the");
        lore.add(ChatColor.GRAY+"color of an item's name");
        lore.add(ChatColor.GRAY+"when applied in an anvil.");
        return lore;
    }
}