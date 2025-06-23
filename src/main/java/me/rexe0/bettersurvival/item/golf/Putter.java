package me.rexe0.bettersurvival.item.golf;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Putter extends Item implements GolfClub {
    public Putter() {
        super(Material.IRON_HOE, ChatColor.GREEN+"Putter", "PUTTER");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A type of golf club used");
        lore.add(ChatColor.GRAY+"to finish the hole with");
        lore.add(ChatColor.GRAY+"precision.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"It generates tiny loft");
        lore.add(ChatColor.GRAY+"and a small amount of");
        lore.add(ChatColor.GRAY+"horizontal power.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey(BetterSurvival.getInstance(), "noAttributes"), 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);
        return item;
    }
    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("###", "#$#", "###");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setIngredient('$', getMaterial());
        recipe.setGroup("GOLF");
        return recipe;
    }

    @Override
    public boolean onRightClick(Player player) {
        return true;
    }

    @Override
    public boolean onLeftClick(Player player) {
        return true;
    }

    @Override
    public double getPower() {
        return 0.5;
    }

    @Override
    public double getLoft() {
        return 0.05;
    }
}
