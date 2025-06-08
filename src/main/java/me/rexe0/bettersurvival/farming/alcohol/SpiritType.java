package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum SpiritType implements AlcoholType {
    SWEET_BERRY(ChatColor.DARK_PURPLE, "Sweet Berry Brandy", Material.SWEET_BERRIES, Color.fromRGB(153, 14, 30)),
    GLOW_BERRY(ChatColor.DARK_PURPLE, "Glow Berry Brandy", Material.GLOW_BERRIES, Color.fromRGB(222, 85, 0)),
    MELON(ChatColor.DARK_PURPLE, "Melon Brandy", Material.MELON_SLICE, Color.fromRGB(201, 0, 37)),
    CHORUS_FRUIT(ChatColor.DARK_PURPLE, "Chorus Brandy", Material.CHORUS_FRUIT, Color.fromRGB(115, 0, 204), 1.1),
    APPLE(ChatColor.GOLD, "Apple Brandy", Material.APPLE, Color.fromRGB(222, 65, 22)),
    GOLDEN_APPLE(ChatColor.GOLD, "Golden Apple Brandy", Material.GOLDEN_APPLE, Color.fromRGB(219, 150, 2), 1.2),
    BEER(ChatColor.YELLOW, "Whiskey", Material.WHEAT, Color.fromRGB(189, 60, 0), 0.9),
    VODKA(ChatColor.WHITE, "Vodka", Material.WHEAT, Color.fromRGB(209, 209, 209), 0.9),
    SUGAR_WASH(ChatColor.YELLOW, "Rum", Material.SUGAR, Color.fromRGB(143, 32, 10), 0.85),
    DISTILLATE(ChatColor.WHITE, "Spirit Distillate", Material.AIR, Color.fromRGB(209, 209, 209), 0.6);




    private String name;
    private ChatColor nameColor;
    private Material fruit;
    private Color color;
    private double priceMultiplier;

    SpiritType(ChatColor nameColor, String name, Material fruit, Color color) {
        this(nameColor, name, fruit, color, 1.0);
    }

    SpiritType(ChatColor nameColor, String name, Material fruit, Color color, double priceMultiplier) {
        this.nameColor = nameColor;
        this.name = name;
        this.fruit = fruit;
        this.color = color;
        this.priceMultiplier = priceMultiplier;
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

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public static SpiritType getSpiritType(Material fruit) {
        for (SpiritType type : values())
            if (type.getFruit() == fruit)
                return type;

        return null;
    }

}
