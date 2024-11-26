package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.TreasureDrop;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class TreasureSand extends Item {
    public TreasureSand() {
        super(Material.SUSPICIOUS_SAND, ChatColor.GREEN+"Treasure Sand", "TREASURE_SAND");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"There may be goodies inside...");
        return lore;
    }

    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        Block block = e.getBlock();
        BrushableBlock state = (BrushableBlock) block.getState();
        state.setItem(TreasureDrop.getTreasureItem(e.getPlayer()));
        state.update();
    }
}
