package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class PlatinumIngot extends Item {
    public PlatinumIngot() {
        super(Material.IRON_INGOT, ChatColor.GREEN+"Platinum Ingot", "PLATINUM_INGOT");
    }

    @Override
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new BlastingRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem(), new RecipeChoice.ExactChoice(ItemType.PLATINUM_ORE.getItem().getItem()), 20, 600));
        recipes.add(new FurnaceRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem(), new RecipeChoice.ExactChoice(ItemType.PLATINUM_ORE.getItem().getItem()), 20, 1200));
        return recipes;
    }
}
