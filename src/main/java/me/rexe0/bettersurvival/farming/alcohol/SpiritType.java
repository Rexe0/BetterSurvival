package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum SpiritType {
    SWEET_BERRY(ChatColor.DARK_PURPLE, "Sweet Berry Brandy", Material.SWEET_BERRIES, Color.fromRGB(153, 14, 30)),
    GLOW_BERRY(ChatColor.DARK_PURPLE, "Glow Berry Brandy", Material.GLOW_BERRIES, Color.fromRGB(222, 85, 0)),
    MELON(ChatColor.DARK_PURPLE, "Melon Brandy", Material.MELON_SLICE, Color.fromRGB(201, 0, 37)),
    CHORUS_FRUIT(ChatColor.DARK_PURPLE, "Chorus Brandy", Material.CHORUS_FRUIT, Color.fromRGB(115, 0, 204)),
    APPLE(ChatColor.GOLD, "Apple Brandy", Material.APPLE, Color.fromRGB(222, 65, 22)),
    GOLDEN_APPLE(ChatColor.GOLD, "Golden Apple Brandy", Material.GOLDEN_APPLE, Color.fromRGB(219, 150, 2)),
    BEER(ChatColor.YELLOW, "Whiskey", Material.WHEAT, Color.fromRGB(189, 60, 0)),
    VODKA(ChatColor.WHITE, "Vodka", Material.WHEAT, Color.fromRGB(209, 209, 209)),
    SUGAR_WASH(ChatColor.YELLOW, "Rum", Material.SUGAR, Color.fromRGB(143, 32, 10)),
    DISTILLATE(ChatColor.WHITE, "Spirit Distillate", Material.AIR, Color.fromRGB(209, 209, 209));




    private String name;
    private ChatColor nameColor;
    private Material fruit;
    private Color color;

    SpiritType(ChatColor nameColor, String name, Material fruit, Color color) {
        this.nameColor = nameColor;
        this.name = name;
        this.fruit = fruit;
        this.color = color;
    }

    public String getName() {
        return name;
    }
    public Material getFruit() {
        return fruit;
    }

    public Color getColor() {
        return color;
    }

    public ChatColor getNameColor() {
        return nameColor;
    }

    public static SpiritType getSpiritType(Material fruit) {
        for (SpiritType type : values())
            if (type.getFruit() == fruit)
                return type;

        return null;
    }
}
