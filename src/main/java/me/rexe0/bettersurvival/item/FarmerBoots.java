package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class FarmerBoots extends Item {
    public FarmerBoots() {
        super(Material.LEATHER_BOOTS, ChatColor.GREEN+"Farmer Boots", "FARMER_BOOTS");
    }

    @Override
    public Recipe getRecipe() {
        ItemStack item = ItemType.FARMER_BOOTS.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.shape("# #", "# #", "& &");
        recipe.setIngredient('#', Material.LEATHER);
        recipe.setIngredient('&', Material.COPPER_INGOT);

        return recipe;
    }
}
