package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class ResonantIngot extends Item {
    public ResonantIngot() {
        super(Material.NETHER_BRICK, ChatColor.BLUE+"Resonant Ingot", "RESONANT_INGOT");
    }
    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);
        return item;
    }


    public Recipe getRecipe() {
        ItemStack item = ItemType.RESONANT_INGOT.getItem().getItem();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.addIngredient(new RecipeChoice.ExactChoice(ItemType.PLATINUM_INGOT.getItem().getItem()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(ItemType.PLATINUM_INGOT.getItem().getItem()));
        recipe.addIngredient(Material.ECHO_SHARD);
        recipe.addIngredient(Material.ECHO_SHARD);
        return recipe;
    }
}
