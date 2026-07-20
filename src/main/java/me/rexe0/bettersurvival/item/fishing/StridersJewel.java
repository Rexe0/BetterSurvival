package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StridersJewel extends Item {
    public StridersJewel() {
        super(Material.PLAYER_HEAD, ChatColor.GOLD+"Strider's Jewel", "STRIDERS_JEWEL");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A precious ruby that can");
        lore.add(ChatColor.GRAY+"be embedded into your helmet");
        lore.add(ChatColor.GRAY+"using an anvil.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"It allows the player to");
        lore.add(ChatColor.GRAY+"gain up to "+ChatColor.GREEN+"10"+ChatColor.GRAY+" seconds");
        lore.add(ChatColor.GRAY+"of fire resistance.");
        return lore;
    }

    @Override
    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        return true;
    }

    @Override
    public ItemStack getItem() {
        return SkullUtil.getCustomSkull(super.getItem(), "http://textures.minecraft.net/texture/94ebf5606ac83a74c1cb9f7e5604be3ce297892a4ca1005ce510ca25eba2888"
                , UUID.fromString("24dd86ea-8884-4c66-9368-3ff5defca31f"));
    }
}
