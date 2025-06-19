package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShinyLure extends Item {
    public ShinyLure() {
        super(Material.YELLOW_DYE, ChatColor.GREEN+"Shiny Lure", "SHINY_LURE");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"Acts as a non-consumable bait");
        lore.add(ChatColor.GRAY+"that reduces the time it takes");
        lore.add(ChatColor.GRAY+"to catch a fish by "+ChatColor.GREEN+"40%"+ChatColor.GRAY+".");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public ShapedRecipe getRecipe() {
        ItemStack item = ItemType.SHINY_LURE.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape(" # ", "#$#", "%#%");
        recipe.setIngredient('#', Material.GOLD_INGOT);
        recipe.setIngredient('$', Material.DIAMOND);
        recipe.setIngredient('%', Material.YELLOW_DYE);
        return recipe;
    }
}
