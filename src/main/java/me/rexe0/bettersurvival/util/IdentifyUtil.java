package me.rexe0.bettersurvival.util;

import org.bukkit.inventory.ItemStack;

public class IdentifyUtil {
    public static boolean isItem(ItemStack item, String name) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasDisplayName()) return false;
        return item.getItemMeta().getDisplayName().equals(name);
    }
}
