package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.TreasureDrop;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TreasurePot extends Item {
    private final ItemType fishingRod;
    public TreasurePot(ItemType fishingRod) {
        super(Material.DECORATED_POT, ChatColor.GREEN+"Treasure Pot", "TREASURE_POT");
        this.fishingRod = fishingRod;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"There may be goodies inside...");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setStringValue(item, "fishingRodType", fishingRod != null ? fishingRod.getItem().getID() : ""));
        return item;
    }

    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        ItemType type;
        try {
            type = ItemType.valueOf(ItemDataUtil.getStringValue(item, "fishingRodType"));
        } catch (IllegalArgumentException ex) {
            type = null;
        }

        org.bukkit.block.data.type.DecoratedPot data = (org.bukkit.block.data.type.DecoratedPot) block.getBlockData();
        data.setCracked(true);
        block.setBlockData(data);

        DecoratedPot state = (DecoratedPot) block.getState();
        state.getInventory().setItem(TreasureDrop.getTreasureItem(player, type, true));
        return false;
    }

    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.DECORATED_POT) return;
        Block block = e.getBlock();
        org.bukkit.block.data.type.DecoratedPot data = (org.bukkit.block.data.type.DecoratedPot) block.getBlockData();
        if (!data.isCracked()) return;


        // Force shatter the pot by 'breaking' it with a tool
        e.setCancelled(true);
        block.breakNaturally(new ItemStack(Material.WOODEN_SWORD), true);
    }
}
