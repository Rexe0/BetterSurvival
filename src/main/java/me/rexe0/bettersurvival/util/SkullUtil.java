package me.rexe0.bettersurvival.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class SkullUtil {
    private static final Base64 base64 = new Base64();
    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull(ItemStack head, String url) {

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), UUID.randomUUID().toString());
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Method setProfileMethod = null;
        try {
            setProfileMethod = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfileMethod.setAccessible(true);
            setProfileMethod.invoke(headMeta, profile);
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException |
                 InvocationTargetException e1) {
            e1.printStackTrace();
        }

        head.setItemMeta(headMeta);
        return head;
    }
}
