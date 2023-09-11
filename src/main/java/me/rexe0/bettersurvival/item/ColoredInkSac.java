package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ColoredInkSac extends Item {
    public ColoredInkSac(ChatColor color) {
        super(Material.INK_SAC, color+"Colored Ink Sac", "COLORED_INK_SAC");
    }
}