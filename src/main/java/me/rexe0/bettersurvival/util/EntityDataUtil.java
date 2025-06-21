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
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntityDataUtil {

    public static void setStringValue(Entity entity, String key, String value) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);


        entity.getPersistentDataContainer().set(entityKey, PersistentDataType.STRING, value);
    }
    public static String getStringValue(Entity entity, String key) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (entity == null) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();

        if (!container.has(entityKey, PersistentDataType.STRING)) return "";
        return container.get(entityKey, PersistentDataType.STRING);
    }
    public static void removeStringValue(Entity entity, String key) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        entity.getPersistentDataContainer().remove(entityKey);
    }


    public static void setDoubleValue(Entity entity, String key, double value) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);


        entity.getPersistentDataContainer().set(entityKey, PersistentDataType.DOUBLE, value);
    }
    public static double getDoubleValue(Entity entity, String key) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (entity == null) return 0;
        PersistentDataContainer container = entity.getPersistentDataContainer();

        if (!container.has(entityKey, PersistentDataType.DOUBLE)) return 0;
        return container.get(entityKey, PersistentDataType.DOUBLE);
    }

    public static void setIntegerValue(Entity entity, String key, int value) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        entity.getPersistentDataContainer().set(entityKey, PersistentDataType.INTEGER, value);
    }
    public static int getIntegerValue(Entity entity, String key) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (entity == null) return 0;
        PersistentDataContainer container = entity.getPersistentDataContainer();

        if (!container.has(entityKey, PersistentDataType.INTEGER)) return 0;
        return container.get(entityKey, PersistentDataType.INTEGER);
    }
    public static void setLongValue(Entity entity, String key, long value) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        entity.getPersistentDataContainer().set(entityKey, PersistentDataType.LONG, value);
    }
    public static long getLongValue(Entity entity, String key) {
        NamespacedKey entityKey = new NamespacedKey(BetterSurvival.getInstance(), key);

        if (entity == null) return 0;
        PersistentDataContainer container = entity.getPersistentDataContainer();

        if (!container.has(entityKey, PersistentDataType.LONG)) return 0;
        return container.get(entityKey, PersistentDataType.LONG);
    }
}
