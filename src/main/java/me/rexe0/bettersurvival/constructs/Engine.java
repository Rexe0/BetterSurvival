package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class Engine extends Modification {
    private static int currentId = 0; // Used for id'ing the harnesses

    public static Engine NONE = new Engine(ChatColor.RED + "None", Material.BARRIER);

    public static Engine COAL = new Engine(ChatColor.GREEN + "Coal Engine", Material.COAL, 0.8, 0.5);
    public static Engine CHARCOAL = new Engine(ChatColor.GREEN + "Charcoal Engine", Material.CHARCOAL, 0.8, 0.5);
    public static Engine BIOFUEL = new Engine(ChatColor.BLUE + "Biofuel Engine", Material.GREEN_DYE, 1.2, 1);
    public static Engine ENHANCED_COAL = new Engine(ChatColor.BLUE + "Enhanced Coal Engine", Material.COAL_BLOCK, 1.4, 1);
    public static Engine ELECTRICAL = new Engine(ChatColor.DARK_PURPLE + "Electrical Engine", Material.REDSTONE_BLOCK, 2, 2);
    public static Engine MOLTEN = new Engine(ChatColor.DARK_PURPLE + "Molten Engine", Material.LAVA_BUCKET, 3, 2);


    private static final List<Engine> allEngines;

    static {
        allEngines = List.of(
                NONE,
                COAL,
                CHARCOAL,
                BIOFUEL,
                ENHANCED_COAL,
                ELECTRICAL,
                MOLTEN
        );
        for (Engine engine : allEngines) {
            engine.addCosts();
        }
    }
    public static List<Engine> getAllEngines() {
        return allEngines;
    }


    public Engine(String name, Material icon) {
        this(name, icon, 0, 0);
    }

    public Engine(String name, Material icon, double speed, double acceleration) {
        super(name, icon, 0, 0, speed, acceleration);
        this.id = currentId++;
    }

    private void addCosts() {
        if (this == NONE) return;
        addResearchCost(new RecipeChoice.MaterialChoice(Material.COPPER_BLOCK), 10);
        addCraftCost(new RecipeChoice.MaterialChoice(Material.COPPER_BLOCK), 5);

        if (this == COAL || this == CHARCOAL) {
            addResearchCost(Material.FURNACE, 1);
            addCraftCost(Material.FURNACE, 1);
        } else if (this == BIOFUEL) {
            addResearchCost(Material.SMOKER, 1);
            addCraftCost(Material.SMOKER, 1);
            addCraftCost(Material.IRON_INGOT, 5);
        } else if (this == ENHANCED_COAL) {
            addResearchCost(Material.BLAST_FURNACE, 1);
            addCraftCost(Material.BLAST_FURNACE, 1);
            addCraftCost(Material.IRON_BLOCK, 2);
        } else if (this == ELECTRICAL) {
            addResearchCost(Material.REDSTONE_BLOCK, 5);
            addCraftCost(Material.REDSTONE_BLOCK, 5);
            addCraftCost(Material.DIAMOND, 4);
        } else if (this == MOLTEN) {
            addResearchCost(Material.NETHERITE_INGOT, 1);
            addCraftCost(Material.NETHERITE_SCRAP, 3);
            addCraftCost(Material.DIAMOND, 4);
        }
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        if (this == COAL) {
            description.add(ChatColor.GRAY + "A basic engine that burns");
            description.add(ChatColor.GRAY + "coal to boost forwards");
            description.add(ChatColor.GRAY + "speed and acceleration.");
        } else if (this == CHARCOAL) {
            description.add(ChatColor.GRAY + "A basic engine that burns");
            description.add(ChatColor.GRAY + "charcoal at a faster rate");
            description.add(ChatColor.GRAY + "to boost forwards speed");
            description.add(ChatColor.GRAY + "and acceleration.");
        } else if (this == BIOFUEL) {
            description.add(ChatColor.GRAY + "A more advanced engine that");
            description.add(ChatColor.GRAY + "uses fertilizer and bonemeal");
            description.add(ChatColor.GRAY + "as fuel. Higher tier fertilizer");
            description.add(ChatColor.GRAY + "burns for longer.");
        } else if (this == ENHANCED_COAL) {
            description.add(ChatColor.GRAY + "An advanced engine that burns");
            description.add(ChatColor.GRAY + "compact coal blocks to");
            description.add(ChatColor.GRAY + "increase effective fuel");
            description.add(ChatColor.GRAY + "capacity.");
        } else if (this == ELECTRICAL) {
            description.add(ChatColor.GRAY + "An innovative engine that");
            description.add(ChatColor.GRAY + "uses special battery packs");
            description.add(ChatColor.GRAY + "to provide a powerful");
            description.add(ChatColor.GRAY + "speed boost.");
        } else if (this == MOLTEN) {
            description.add(ChatColor.GRAY + "An intricate engine that");
            description.add(ChatColor.GRAY + "uses lava buckets or blaze");
            description.add(ChatColor.GRAY + "rods to provide a short");
            description.add(ChatColor.GRAY + "but massive speed boost.");
        }
        return description;
    }

    // Returns amount of ticks of fuel per item
    public int useFuel(ItemStack item) {
        Material type = item.getType();
        if (this == COAL) {
            if (type == Material.COAL) return 95;
            return 0;
        }
        if (this == CHARCOAL) {
            if (type  == Material.CHARCOAL) return 75;
            return 0;
        }
        if (this == BIOFUEL) {
            if (type  == Material.BONE_MEAL) {
                int tier = ItemDataUtil.getIntegerValue(item, "fertilizerTier");
                return 56*tier;
            }
            return 0;
        }
        if (this == ENHANCED_COAL) {
            if (type  == Material.COAL_BLOCK) return 855;
            return 0;
        }
        if (this == ELECTRICAL) {
            if (ItemDataUtil.getIntegerValue(item, "isBatteryCharged") == 1) return 900;
            return 0;
        }
        if (this == MOLTEN) {
            if (type == Material.LAVA_BUCKET) return 1200;
            if (type == Material.BLAZE_ROD) return 300;
            return 0;
        }
        return 0;
    }
}
