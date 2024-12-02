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

import java.awt.*;

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
    public static String IntegerToRomanNumeral(int input) {
        if (input < 0 || input > 3999)
            return "Invalid Roman Number Value";
        if (input == 0) return "0";
        String s = "";
        while (input >= 1000) {
            s += "M";
            input -= 1000;
        }
        while (input >= 900) {
            s += "CM";
            input -= 900;
        }
        while (input >= 500) {
            s += "D";
            input -= 500;
        }
        while (input >= 400) {
            s += "CD";
            input -= 400;
        }
        while (input >= 100) {
            s += "C";
            input -= 100;
        }
        while (input >= 90) {
            s += "XC";
            input -= 90;
        }
        while (input >= 50) {
            s += "L";
            input -= 50;
        }
        while (input >= 40) {
            s += "XL";
            input -= 40;
        }
        while (input >= 10) {
            s += "X";
            input -= 10;
        }
        while (input >= 9) {
            s += "IX";
            input -= 9;
        }
        while (input >= 5) {
            s += "V";
            input -= 5;
        }
        while (input >= 4) {
            s += "IV";
            input -= 4;
        }
        while (input >= 1) {
            s += "I";
            input -= 1;
        }

        return s;
    }

    // Add color ranging from red to green based on value and max
    public static String getFormattedColorString(String string, int value, int max) {
        int red = 255;
        int green = 255;
        double ratio = (double) value / max;
        if (ratio >= 0.5)
            red = (int) (255*(2-(ratio*2)));
        if (ratio <= 0.5)
            green = (int) (255*(ratio*2));
        net.md_5.bungee.api.ChatColor chatColor = net.md_5.bungee.api.ChatColor.of(new Color(red, green, 0));

        return chatColor+string;
    }

}
