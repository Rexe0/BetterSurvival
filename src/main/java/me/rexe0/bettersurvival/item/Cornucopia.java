package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Cornucopia extends Item {
    private final int FOOD_INCREASE = 4;
    private final int SATURATION_INCREASE = 6;
    public Cornucopia() {
        super(Material.GOLDEN_CARROT, ChatColor.GREEN+"Cornucopia", "CORNUCOPIA");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"Restores "+ChatColor.GREEN+(6+FOOD_INCREASE)+ChatColor.GRAY+" hunger and ");
        lore.add(""+ChatColor.GREEN+(14+SATURATION_INCREASE)+ChatColor.GRAY+" saturation.");
        return lore;
    }

    @Override
    public void onConsume(Player player) {
        player.setFoodLevel(Math.min(20, player.getFoodLevel()+FOOD_INCREASE));
        player.setSaturation(Math.min(player.getFoodLevel(), player.getSaturation()+SATURATION_INCREASE));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, true, false));
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
    @Override
    public Recipe getRecipe() {
        ItemStack item = ItemType.CORNUCOPIA.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape(" % ", "#@#", "&%&");
        recipe.setIngredient('#', Material.GOLDEN_CARROT);
        recipe.setIngredient('@', Material.HAY_BLOCK);
        recipe.setIngredient('&', Material.BEETROOT);
        recipe.setIngredient('%', Material.BAKED_POTATO);

        return recipe;
    }
}
