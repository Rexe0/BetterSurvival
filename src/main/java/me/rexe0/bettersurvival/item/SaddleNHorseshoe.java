package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class SaddleNHorseshoe extends Item {
    public SaddleNHorseshoe() {
        super(Material.SADDLE, ChatColor.GREEN+"Saddle 'n' Horseshoe", "SADDLE_N_HORSESHOE");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.addUnsafeEnchantment(Enchantment.POWER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);
        return item;
    }

    public SmithingTransformRecipe getRecipe() {
        ItemStack item = ItemType.SADDLE_N_HORSESHOE.getItem().getItem();
        return new SmithingTransformRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()),
                item,
                new RecipeChoice.MaterialChoice(Material.IRON_BLOCK),
                new RecipeChoice.MaterialChoice(Material.SADDLE),
                new RecipeChoice.MaterialChoice(Material.IRON_BLOCK));
    }
}
