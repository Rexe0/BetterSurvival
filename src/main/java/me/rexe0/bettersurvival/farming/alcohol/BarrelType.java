package me.rexe0.bettersurvival.farming.alcohol;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.awt.*;

public enum BarrelType {
    OAK(ChatColor.of(new Color(181, 146, 86))+"Oak", Material.OAK_PLANKS),
    SPRUCE(ChatColor.of(new Color(128, 94, 55))+"Spruce", Material.SPRUCE_PLANKS),
    BIRCH(ChatColor.of(new Color(205, 190, 130))+"Birch", Material.BIRCH_PLANKS),
    JUNGLE(ChatColor.of(new Color(182, 137, 94))+"Jungle", Material.JUNGLE_PLANKS),
    ACACIA(ChatColor.of(new Color(189, 96, 62))+"Acacia", Material.ACACIA_PLANKS),
    DARK_OAK(ChatColor.of(new Color(84, 65, 40))+"Dark Oak", Material.DARK_OAK_PLANKS),
    MANGROVE(ChatColor.of(new Color(113, 51, 45))+"Mangrove", Material.MANGROVE_PLANKS),
    CHERRY(ChatColor.of(new Color(204, 140, 145))+"Cherry", Material.CHERRY_PLANKS),
    PALE_OAK(ChatColor.of(new Color(238, 228, 227))+"Pale Oak", Material.PALE_OAK_PLANKS),
    BAMBOO(ChatColor.of(new Color(200, 179, 86))+"Bamboo", Material.BAMBOO_PLANKS),
    CRIMSON(ChatColor.of(new Color(134, 56, 89))+"Crimson", Material.CRIMSON_PLANKS),
    WARPED(ChatColor.of(new Color(53, 145, 139))+"Warped", Material.WARPED_PLANKS);

    private String name;
    private Material planks;

    BarrelType(String name, Material planks) {
        this.name = name;
        this.planks = planks;
    }

    public String getName() {
        return name;
    }
    public Material getPlanks() {
        return planks;
    }
}
