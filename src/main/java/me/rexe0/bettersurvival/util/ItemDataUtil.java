/*
 * Copyright (c) 2022 Thomas Bringemeier - All Rights Reserved
 * This file is a part of Divinity and no users except
 * the author have the right to use, merge, publish,
 * copy, sell, modify and/or distribute this file or copies of it
 * unless otherwise explicitly stated by the author.
 *
 * The above copyright notice and this permission notice shall be included in
 * and applies to all copies or substantial portions of the Software/Project.
 */

package me.rexe0.bettersurvival.util;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemDataUtil {

    public static ItemMeta setStringValue(ItemStack item, String key, String value) {
        NamespacedKey itemKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, value);
        return meta;
    }
    public static String getStringValue(ItemStack item, String key) {
        NamespacedKey itemKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (item == null || !item.hasItemMeta()) return "";
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (!container.has(itemKey, PersistentDataType.STRING)) return "";
        return container.get(itemKey, PersistentDataType.STRING);
    }


    public static boolean isItem(ItemStack item, String ID) {
        return getStringValue(item, "itemID").equals(ID);
    }

}
