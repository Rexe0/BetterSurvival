package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class CopperFishingRod extends Item {
    public CopperFishingRod() {
        super(Material.FISHING_ROD, ChatColor.GREEN+"Copper Fishing Rod", "COPPER_FISHING_ROD");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Can use Bait when fishing.");
        return lore;
    }

    public ShapedRecipe getRecipe() {
        ItemStack item = ItemType.COPPER_FISHING_ROD.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("  #", " #$", "# $");
        recipe.setIngredient('#', Material.COPPER_BLOCK);
        recipe.setIngredient('$', Material.STRING);
        return recipe;
    }
}
