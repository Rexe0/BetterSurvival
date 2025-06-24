package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.TreasureDrop;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TreasureSand extends Item {
    private final ItemType fishingRod;
    public TreasureSand(ItemType fishingRod) {
        super(Material.SUSPICIOUS_SAND, ChatColor.GREEN+"Treasure Sand", "TREASURE_SAND");
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

    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;

        ItemType type;
        try {
            type = ItemType.valueOf(ItemDataUtil.getStringValue(e.getItemInHand(), "fishingRodType"));
        } catch (IllegalArgumentException ex) {
            type = null;
        }
        Bukkit.broadcastMessage(type+"");

        Block block = e.getBlock();
        BrushableBlock state = (BrushableBlock) block.getState();
        state.setItem(TreasureDrop.getTreasureItem(e.getPlayer(), type));
        state.update();
    }
}
