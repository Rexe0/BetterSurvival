package me.rexe0.bettersurvival.util;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
        if(url.isEmpty())return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URI(url).toURL());
        } catch (URISyntaxException | MalformedURLException ex) {
            ex.printStackTrace();
        }
        profile.setTextures(textures);
        headMeta.setOwnerProfile(profile);
        head.setItemMeta(headMeta);
        return head;
    }
}
