package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TungstenClump extends Item {
    public TungstenClump() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Tungsten Clump", "TUNGSTEN_CLUMP");
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
}
