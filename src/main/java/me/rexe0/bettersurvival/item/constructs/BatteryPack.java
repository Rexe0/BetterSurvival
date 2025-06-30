package me.rexe0.bettersurvival.item.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatteryPack extends Item {
    private boolean isCharged;
    public BatteryPack(boolean isCharged) {
        super(Material.AMETHYST_SHARD, ChatColor.GREEN+"Battery Pack", "BATTERY_PACK");
        this.isCharged = isCharged;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A battery pack that can");
        lore.add(ChatColor.GRAY+"be charged in a lightning");
        lore.add(ChatColor.GRAY+"powered furnace.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Status: "+(isCharged ? ChatColor.GREEN+"Charged" : ChatColor.RED+"Empty"));
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();

        if (!isCharged) return item;

        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "isBatteryCharged", 1));
        return item;
    }
    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();
        ItemStack item = ItemType.BATTERY_PACK.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("###", "#$#", "###");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setIngredient('$', Material.AMETHYST_SHARD);
        recipes.put(new NamespacedKey(BetterSurvival.getInstance(), getID()), recipe);

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_CHARGED"), new BatteryPack(true).getItem(), new RecipeChoice.ExactChoice(new BatteryPack(false).getItem()), 1, 400);
        recipes.put(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_CHARGED"), furnaceRecipe);
        return recipes;
    }
}
