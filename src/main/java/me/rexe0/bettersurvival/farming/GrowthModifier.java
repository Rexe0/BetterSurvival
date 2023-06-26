package me.rexe0.bettersurvival.farming;

import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class GrowthModifier implements Listener {
    @EventHandler
    public void onGrow(BlockGrowEvent e) {
        Block block = e.getBlock();

        // If it's raining, crops grow at normal rate
        if (block.getWorld().getWeatherDuration() > 0) return;

        boolean nearbySniffer = false;
        for (Sniffer sniffer : block.getWorld().getEntitiesByClass(Sniffer.class)) {
            if (sniffer.getLocation().distanceSquared(block.getLocation()) < 400) {
                nearbySniffer = true;
                break;
            }
        }

        if (RandomUtil.getRandom().nextInt(10) < (nearbySniffer ? 8 : 5)) return;
        e.setCancelled(true); // Reduces crop growth by 50%. If a sniffer is nearby, only reduce by 30%
    }
}
