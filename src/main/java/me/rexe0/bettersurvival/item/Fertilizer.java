package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Fertilizer extends Item {
    private int tier;
    public Fertilizer(int tier) {
        super(Material.BONE_MEAL, switch (tier) {
            default -> ChatColor.GREEN;
            case 2 -> ChatColor.BLUE;
            case 3 -> ChatColor.DARK_PURPLE;
            case 4 -> ChatColor.GOLD;
        }+"Fertilizer", "FERTILIZER");
        this.tier = tier;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "fertilizerTier", tier));
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        return item;
    }
}
