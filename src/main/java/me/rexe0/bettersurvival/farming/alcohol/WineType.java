package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum WineType {
    SWEET_BERRY(ChatColor.DARK_PURPLE+"Sweet Berry Wine", Material.SWEET_BERRIES, 4),
    GLOW_BERRY(ChatColor.DARK_PURPLE+"Glow Berry Wine", Material.GLOW_BERRIES, 4, Color.fromRGB(255, 106, 13)),
    MELON(ChatColor.DARK_PURPLE+"Melon Wine", Material.GLOW_BERRIES, 4, Color.fromRGB(255, 0, 47)),
    CHORUS_FRUIT(ChatColor.DARK_PURPLE+"Chorus Wine", Material.CHORUS_FRUIT, 8, Color.fromRGB(144, 0, 255)),
    APPLE(ChatColor.GOLD+"Apple Cider", Material.APPLE, 4, Color.fromRGB(255, 81, 33)),
    GOLDEN_APPLE(ChatColor.GOLD+"Golden Apple Cider", Material.GOLDEN_APPLE, 2, Color.fromRGB(255, 174, 0)),
    BEER(ChatColor.YELLOW+"Beer", Material.WHEAT, 16, Color.fromRGB(255, 175, 36)),
    SUGAR_WASH(ChatColor.WHITE+"Sugar Wash", Material.SUGAR, 2, Color.fromRGB(255, 230, 186));



    private String name;
    private Material fruit;
    private int fruitCost; // How many fruits are needed to make 1% equivalent
    private Color color;

    WineType(String name, Material fruit, int fruitCost) {
        this(name, fruit, fruitCost, Color.fromRGB(173, 14, 40));
    }

    WineType(String name, Material fruit, int fruitCost, Color color) {
        this.name = name;
        this.fruit = fruit;
        this.fruitCost = fruitCost;
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

    public int getFruitCost() {
        return fruitCost;
    }

    public static WineType getWineType(Material fruit) {
        for (WineType type : values())
            if (type.getFruit() == fruit)
                return type;

        return null;
    }
}
