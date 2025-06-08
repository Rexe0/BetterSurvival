package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum WineType implements AlcoholType {
    SWEET_BERRY(ChatColor.DARK_PURPLE, "Sweet Berry Wine", "Sweet Berries", Material.SWEET_BERRIES, 4, Color.fromRGB(173, 14, 40)),
    GLOW_BERRY(ChatColor.DARK_PURPLE, "Glow Berry Wine", "Glow Berries", Material.GLOW_BERRIES, 4, Color.fromRGB(255, 106, 13)),
    MELON(ChatColor.DARK_PURPLE, "Melon Wine", "Melons", Material.MELON_SLICE, 4, Color.fromRGB(255, 0, 47)),
    CHORUS_FRUIT(ChatColor.DARK_PURPLE, "Chorus Wine", "Chorus Fruits", Material.CHORUS_FRUIT, 8, Color.fromRGB(144, 0, 255), 1.1),
    APPLE(ChatColor.GOLD, "Apple Cider", "Apples", Material.APPLE, 4, Color.fromRGB(255, 81, 33), 1.1),
    GOLDEN_APPLE(ChatColor.GOLD, "Golden Apple Cider", "Golden Apples", Material.GOLDEN_APPLE, 2, Color.fromRGB(255, 174, 0), 1.2),
    BEER(ChatColor.YELLOW, "Beer", "Bitter", Material.WHEAT, 16, Color.fromRGB(255, 175, 36), 0.9),
    SUGAR_WASH(ChatColor.WHITE, "Sugar Wash", "Very Sweet", Material.SUGAR, 2, Color.fromRGB(255, 230, 186), 0.85);



    private String name;
    private String flavorName;
    private ChatColor nameColor;
    private Material fruit;
    private int fruitCost; // How many fruits are needed to make 1% equivalent
    private Color color;
    private double priceMultiplier;

    WineType(ChatColor nameColor, String name, String flavorName, Material fruit, int fruitCost, Color color) {
        this(nameColor, name, flavorName, fruit, fruitCost, color, 1.0);
    }
    WineType(ChatColor nameColor, String name, String flavorName, Material fruit, int fruitCost, Color color, double priceMultiplier) {
        this.nameColor = nameColor;
        this.name = name;
        this.flavorName = flavorName;
        this.fruit = fruit;
        this.fruitCost = fruitCost;
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

    public int getFruitCost() {
        return fruitCost;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public ChatColor getNameColor() {
        return nameColor;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }


    public static WineType getWineType(Material fruit) {
        for (WineType type : values())
            if (type.getFruit() == fruit)
                return type;

        return null;
    }
}
