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
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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
    public static ItemMeta setDoubleValue(ItemStack item, String key, double value) {
        NamespacedKey itemKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.DOUBLE, value);
        return meta;
    }
    public static double getDoubleValue(ItemStack item, String key) {
        NamespacedKey itemKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (item == null || !item.hasItemMeta()) return 0;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (!container.has(itemKey, PersistentDataType.DOUBLE)) return 0;
        return container.get(itemKey, PersistentDataType.DOUBLE);
    }
    public static ItemMeta setIntegerValue(ItemStack item, String key, int value) {
        NamespacedKey itemKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.INTEGER, value);
        return meta;
    }
    public static int getIntegerValue(ItemStack item, String key) {
        NamespacedKey itemKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (item == null || !item.hasItemMeta()) return 0;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (!container.has(itemKey, PersistentDataType.INTEGER)) return 0;
        return container.get(itemKey, PersistentDataType.INTEGER);
    }

    public static boolean isItem(ItemStack item, String ID) {
        return getStringValue(item, "itemID").equals(ID);
    }

    public static boolean isItemName(ItemStack item, String name) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasDisplayName()) return false;
        return item.getItemMeta().getDisplayName().equals(name);
    }
    public static ItemType getItemType(ItemStack item) {
        try {
            return ItemType.valueOf(getStringValue(item, "itemID"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    public static boolean hasItem(String itemID, int amount, Player player) {
        int materialsAmount = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            if (!itemStack.hasItemMeta()) continue;
            if (isItem(itemStack, itemID))
                materialsAmount += itemStack.getAmount();
        }
        return materialsAmount >= amount;
    }
    public static ItemStack removeItems(String itemID, int amount, Player player) {
        ItemStack itemStack = null;
        int counter = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (!item.hasItemMeta()) continue;
            if (isItem(item, itemID)) {
                if (item.getAmount() <= counter) {
                    itemStack = item;
                    counter -= item.getAmount();
                    item.setAmount(0);
                } else if (item.getAmount() > counter) {
                    itemStack = item;
                    item.setAmount(item.getAmount() - counter);
                    counter = 0;
                    break;
                }
            }
        }
        return itemStack;
    }
}
