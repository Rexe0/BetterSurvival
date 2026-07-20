package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class TungstenCrystal extends Item {
    public TungstenCrystal() {
        super(Material.QUARTZ, ChatColor.GREEN+"Tungsten Crystal", "TUNGSTEN_CRYSTAL");
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
        ItemStack item = me.rexe0.bettersurvival.item.ItemType.TUNGSTEN_CRYSTAL.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("###", "#$#", "###");
        recipe.setIngredient('#', new RecipeChoice.ExactChoice(ItemType.TUNGSTEN_CLUMP.getItem().getItem()));
        recipe.setIngredient('$', Material.MAGMA_CREAM);
        return recipe;
    }
}
