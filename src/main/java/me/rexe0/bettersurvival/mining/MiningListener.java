package me.rexe0.bettersurvival.mining;

import me.rexe0.bettersurvival.util.EntityDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiningListener implements Listener {
    private final List<Material> ores = new ArrayList<>();

    public MiningListener() {
        ores.add(Material.COPPER_ORE);
        ores.add(Material.COAL_ORE);
        ores.add(Material.IRON_ORE);
        ores.add(Material.GOLD_ORE);
        ores.add(Material.EMERALD_ORE);
        ores.add(Material.DIAMOND_ORE);
        ores.add(Material.LAPIS_ORE);
        ores.add(Material.REDSTONE_ORE);
        ores.add(Material.NETHER_GOLD_ORE);
        ores.add(Material.NETHER_QUARTZ_ORE);
        ores.add(Material.ANCIENT_DEBRIS);
        ores.add(Material.DEEPSLATE_COPPER_ORE);
        ores.add(Material.DEEPSLATE_COAL_ORE);
        ores.add(Material.DEEPSLATE_IRON_ORE);
        ores.add(Material.DEEPSLATE_GOLD_ORE);
        ores.add(Material.DEEPSLATE_EMERALD_ORE);
        ores.add(Material.DEEPSLATE_DIAMOND_ORE);
        ores.add(Material.DEEPSLATE_LAPIS_ORE);
        ores.add(Material.DEEPSLATE_REDSTONE_ORE);
    }
    @EventHandler
    public void onBlockMine(BlockBreakEvent e) {
        if (!ores.contains(e.getBlock().getType())) return;

        Player player = e.getPlayer();
        int level = EntityDataUtil.getIntegerValue(player, "upgradeLevel.MINING");

        for (ItemStack item : e.getBlock().getDrops(player.getEquipment().getItemInMainHand())) {
            if (Math.random() < level*0.05)
                item.setAmount(item.getAmount() + 1);
        }
    }
}
