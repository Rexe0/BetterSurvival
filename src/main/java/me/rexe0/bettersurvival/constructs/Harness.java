package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class Harness extends Modification {
    private static int currentId = 0; // Used for id'ing the harnesses

    public static Harness NONE = new Harness(ChatColor.RED + "None", Material.BARRIER);

    public static Harness WHITE = new Harness(ChatColor.GREEN+"White Harness", Material.WHITE_HARNESS);
    public static Harness LIGHT_GRAY = new Harness(ChatColor.GREEN+"Light Gray Harness", Material.LIGHT_GRAY_HARNESS);
    public static Harness GRAY = new Harness(ChatColor.GREEN+"Gray Harness", Material.GRAY_HARNESS);
    public static Harness BLACK = new Harness(ChatColor.GREEN+"Black Harness", Material.BLACK_HARNESS);
    public static Harness BROWN = new Harness(ChatColor.GREEN+"Brown Harness", Material.BROWN_HARNESS);
    public static Harness RED = new Harness(ChatColor.GREEN+"Red Harness", Material.RED_HARNESS);
    public static Harness ORANGE = new Harness(ChatColor.GREEN+"Orange Harness", Material.ORANGE_HARNESS);
    public static Harness YELLOW = new Harness(ChatColor.GREEN+"Yellow Harness", Material.YELLOW_HARNESS);
    public static Harness LIME = new Harness(ChatColor.GREEN+"Lime Harness", Material.LIME_HARNESS);
    public static Harness GREEN = new Harness(ChatColor.GREEN+"Green Harness", Material.GREEN_HARNESS);
    public static Harness CYAN = new Harness(ChatColor.GREEN+"Cyan Harness", Material.CYAN_HARNESS);
    public static Harness LIGHT_BLUE = new Harness(ChatColor.GREEN+"Light Blue Harness", Material.LIGHT_BLUE_HARNESS);
    public static Harness BLUE = new Harness(ChatColor.GREEN+"Blue Harness", Material.BLUE_HARNESS);
    public static Harness PURPLE = new Harness(ChatColor.GREEN+"Purple Harness", Material.PURPLE_HARNESS);
    public static Harness MAGENTA = new Harness(ChatColor.GREEN+"Magenta Harness", Material.MAGENTA_HARNESS);
    public static Harness PINK = new Harness(ChatColor.GREEN+"Pink Harness", Material.PINK_HARNESS);

    public static Harness ARMORED = new Harness(ChatColor.BLUE+"Armored Harness", Material.LIGHT_GRAY_HARNESS, 5, -0.1);
    public static Harness REINFORCED = new Harness(ChatColor.DARK_PURPLE+"Reinforced Harness", Material.GRAY_HARNESS, 10, -0.2);
    public static Harness FORTIFIED = new Harness(ChatColor.GOLD+"Fortified Harness", Material.BLACK_HARNESS, 15, -0.25);


    private static final List<Harness> allHarnesses;

    static {
        allHarnesses = List.of(
                NONE,
                WHITE,
                LIGHT_GRAY,
                GRAY,
                BLACK,
                BROWN,
                RED,
                ORANGE,
                YELLOW,
                LIME,
                GREEN,
                CYAN,
                LIGHT_BLUE,
                BLUE,
                PURPLE,
                MAGENTA,
                PINK,
                ARMORED,
                REINFORCED,
                FORTIFIED
        );
        for (Harness harness : allHarnesses) {
            harness.addCosts();
        }

    }
    public static List<Harness> getAllHarnesses() {
        return allHarnesses;
    }


    public Harness(String name, Material icon) {
        this(name, icon, 0, 0);
    }

    public Harness(String name, Material icon, double armor, double acceleration) {
        super(name, icon, 0, armor, 0, acceleration);
        this.id = currentId++;
    }

    private void addCosts() {
        if (this == NONE) return;
        addResearchCost(new RecipeChoice.MaterialChoice(Material.LEATHER), 3);

        addCraftCost(new RecipeChoice.MaterialChoice(Material.LEATHER), 3);
        addCraftCost(new RecipeChoice.MaterialChoice(Material.GLASS), 2);

        if (this == ARMORED) {
            addResearchCost(Material.IRON_INGOT, 20);
            addCraftCost(Material.IRON_INGOT, 10);
        } else if (this == REINFORCED) {
            addResearchCost(new RecipeChoice.ExactChoice(ItemType.PLATINUM_INGOT.getItem().getItem()), 4);
            addCraftCost(new RecipeChoice.ExactChoice(ItemType.PLATINUM_INGOT.getItem().getItem()), 2);
        } else if (this == FORTIFIED) {
            addResearchCost(Material.NETHERITE_INGOT, 1);
            addCraftCost(Material.NETHERITE_INGOT, 1);
        } else if (this == WHITE) addCraftCost(Material.WHITE_WOOL, 1);
        else if (this == LIGHT_GRAY) addCraftCost(Material.LIGHT_GRAY_WOOL, 1);
        else if (this == GRAY) addCraftCost(Material.GRAY_WOOL, 1);
        else if (this == BLACK) addCraftCost(Material.BLACK_WOOL, 1);
        else if (this == BROWN) addCraftCost(Material.BROWN_WOOL, 1);
        else if (this == RED) addCraftCost(Material.RED_WOOL, 1);
        else if (this == ORANGE) addCraftCost(Material.ORANGE_WOOL, 1);
        else if (this == YELLOW) addCraftCost(Material.YELLOW_WOOL, 1);
        else if (this == LIME) addCraftCost(Material.LIME_WOOL, 1);
        else if (this == GREEN) addCraftCost(Material.GREEN_WOOL, 1);
        else if (this == CYAN) addCraftCost(Material.CYAN_WOOL, 1);
        else if (this == LIGHT_BLUE) addCraftCost(Material.LIGHT_BLUE_WOOL, 1);
        else if (this == BLUE) addCraftCost(Material.BLUE_WOOL, 1);
        else if (this == PURPLE) addCraftCost(Material.PURPLE_WOOL, 1);
        else if (this == MAGENTA) addCraftCost(Material.MAGENTA_WOOL, 1);
        else if (this == PINK) addCraftCost(Material.PINK_WOOL, 1);
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        if (this == NONE) return description;

        if (this == ARMORED) {
            description.add(ChatColor.GRAY+"A harness equipped with");
            description.add(ChatColor.GRAY+"additional iron armor.");
        } else if (this == REINFORCED) {
            description.add(ChatColor.GRAY+"A harness reinforced with");
            description.add(ChatColor.GRAY+"platinum plating.");
        } else if (this == FORTIFIED) {
            description.add(ChatColor.GRAY+"A harness fortified with");
            description.add(ChatColor.GRAY+"strong netherite plating.");
        } else {
            description.add(ChatColor.GRAY+"A harness made of leather,");
            description.add(ChatColor.GRAY+"with a color of your choice.");
        }
        return description;
    }

    public ItemStack getHarness() {
        if (getIcon() == Material.BARRIER) return new ItemStack(Material.AIR);
        return new ItemStack(getIcon());
    }
}
