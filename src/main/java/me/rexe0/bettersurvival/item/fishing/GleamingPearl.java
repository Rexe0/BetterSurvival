package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GleamingPearl extends Item {
    public GleamingPearl() {
        super(Material.PLAYER_HEAD, ChatColor.GOLD+"Gleaming Pearl", "GLEAMING_PEARL");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A precious pearl that can");
        lore.add(ChatColor.GRAY+"ascend your fishing rod");
        lore.add(ChatColor.GRAY+"to its full potential.");
        return lore;
    }
    @Override
    public boolean canPlaceBlock() {
        return false;
    }
    @Override
    public ItemStack getItem() {
        return SkullUtil.getCustomSkull(super.getItem(), "http://textures.minecraft.net/texture/5170f0940d55022b721a64ea446278f359fe4bb395725dc8e93c02b1229e1a7f"
                , UUID.fromString("b520e1b1-5662-42e3-af5e-93280ffe0574"));
    }
}
