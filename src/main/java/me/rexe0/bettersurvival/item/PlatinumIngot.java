package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class PlatinumIngot extends Item {
    public PlatinumIngot() {
        super(Material.IRON_INGOT, ChatColor.GREEN+"Platinum Ingot", "PLATINUM_INGOT");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        return item;
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();
        recipes.put(new NamespacedKey(BetterSurvival.getInstance(), getID()), new FurnaceRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem(), new RecipeChoice.ExactChoice(ItemType.PLATINUM_ORE.getItem().getItem()), 20, 1200));
        recipes.put(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_blasting"), new BlastingRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_blasting"), getItem(), new RecipeChoice.ExactChoice(ItemType.PLATINUM_ORE.getItem().getItem()), 20, 600));
        return recipes;
    }
}
