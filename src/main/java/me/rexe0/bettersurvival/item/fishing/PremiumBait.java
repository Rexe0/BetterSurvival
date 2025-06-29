package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class PremiumBait extends Item {
    public PremiumBait() {
        super(Material.MELON_SEEDS, ChatColor.GREEN+"Premium Bait", "PREMIUM_BAIT");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Bait");
        return lore;
    }

    @Override
    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        return true;
    }

    @Override
    public Recipe getRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), ItemType.PREMIUM_BAIT.getItem().getItem());

        recipe.addIngredient(Material.GOLDEN_CARROT);
        recipe.addIngredient(Material.BAKED_POTATO);
        return recipe;
    }
}
