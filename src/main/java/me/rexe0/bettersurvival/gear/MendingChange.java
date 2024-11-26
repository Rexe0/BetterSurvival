package me.rexe0.bettersurvival.gear;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

public class MendingChange implements Listener {
    @EventHandler
    public void onMend(PlayerItemMendEvent e) {
        if (!BetterSurvival.getConfigLoader().isMendingChanges()) return;
        e.setCancelled(true);

        ItemStack item = e.getItem();
        if (!(item.getItemMeta() instanceof Repairable meta)) return;
        if (RandomUtil.getRandom().nextInt(3) != 0) return;
        meta.setRepairCost(Math.max(0, meta.getRepairCost()-1));
        item.setItemMeta(meta);
    }
}
