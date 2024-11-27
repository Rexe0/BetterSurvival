package me.rexe0.bettersurvival.deepdark;

import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.SculkCatalyst;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DeepDarkChanges implements Listener {

    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.SCULK_SHRIEKER) return;
        if (e.getPlayer().getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) return;
        e.setDropItems(false);
        if (RandomUtil.getRandom().nextInt(3) != 0) return;
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.ECHO_SHARD));
    }

    public static void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block;
            for (int i = 0; i < 50; i++) {
                Location loc = player.getLocation().add(
                        RandomUtil.getRandom().nextInt(-64, 64),
                        0,
                        RandomUtil.getRandom().nextInt(-64, 64));
                loc.setY(RandomUtil.getRandom().nextInt(-60, -25));
                block = loc.getBlock();

                if (block.getType() == Material.SCULK_CATALYST) {
                    SculkCatalyst catalyst = (SculkCatalyst) block.getState();
                    for (int x = -1; x <= 1; x++)
                        for (int z = -1; z <= 1; z++)
                            catalyst.bloom(loc.add(x, 0, z).getBlock(), 3);
                }
                if (block.getType() == Material.SCULK_SHRIEKER) {
                    SculkShrieker shrieker = (SculkShrieker) block.getBlockData();
                    shrieker.setCanSummon(true);
                }


            }
        }
    }
}
