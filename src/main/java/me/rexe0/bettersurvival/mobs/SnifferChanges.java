package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.item.farming.Fertilizer;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class SnifferChanges implements Listener {
    @EventHandler
    public void onDigUp(EntityDropItemEvent e) {
        if (!(e.getEntity() instanceof Sniffer sniffer)) return;

        ItemStack fertilizer = new Fertilizer(RandomUtil.getRandom().nextInt(3) == 0 ? 4 : 3).getItem();
        fertilizer.setAmount(RandomUtil.getRandom().nextInt(3, 5));
        sniffer.getWorld().dropItemNaturally(e.getItemDrop().getLocation(), fertilizer);
    }
}
