package me.rexe0.bettersurvival.smithing;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.awt.*;

public enum SmithingOre {
    COPPER(ChatColor.of(new Color(214, 109, 72))+"Copper", 5, Material.RAW_COPPER, Material.COPPER_INGOT),
    IRON(ChatColor.of(new Color(198, 198, 198))+"Iron", 10, Material.RAW_IRON, Material.IRON_INGOT),
    GOLD(ChatColor.of(new Color(233, 177, 21))+"Gold", 6, Material.RAW_GOLD, Material.GOLD_INGOT),
    DIAMOND(ChatColor.of(new Color(74, 237, 217))+"Diamond", 12, Material.DIAMOND),
    NETHERITE(ChatColor.of(new Color(93, 86, 93))+"Netherite", 15, Material.NETHERITE_SCRAP, Material.ANCIENT_DEBRIS),
    AMETHYST(ChatColor.of(new Color(157, 111, 242))+"Amethyst", 8, Material.AMETHYST_SHARD),
    QUARTZ(ChatColor.of(new Color(229, 223, 214))+"Quartz", 7, Material.QUARTZ),
    EMERALD(ChatColor.of(new Color(14, 183, 9))+"Emerald", 11, Material.EMERALD),
    PRISMARINE(ChatColor.of(new Color(91, 165, 140))+"Prismarine", 10, Material.PRISMARINE_SHARD),
    SHULKER(ChatColor.of(new Color(151, 105, 151))+"Shulker", 15, Material.SHULKER_SHELL),
    ;
    private final String name;
    private final Material[] materials;
    private final int time; // Time each ore takes to smelt in seconds

    SmithingOre(String name, int time, Material... materials) {
        this.name = name;
        this.time = time;
        this.materials = materials;
    }


    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public Material[] getMaterials() {
        return materials;
    }

    public static SmithingOre getFromMaterial(Material material) {
        for (SmithingOre ore : values())
            for (Material mat : ore.getMaterials())
                if (mat == material) return ore;
        return null;
    }
}
