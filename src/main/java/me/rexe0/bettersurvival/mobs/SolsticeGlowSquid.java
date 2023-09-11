package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.item.ColoredInkSac;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.GlowSquid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class SolsticeGlowSquid implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof GlowSquid squid)) return;
        if (EntityDataUtil.getStringValue(squid, "solsticeSquidColor").equals("")) return;
        if (RandomUtil.getRandom().nextInt(3) != 0) return;
        ChatColor color = ChatColor.valueOf(EntityDataUtil.getStringValue(squid, "solsticeSquidColor"));
        squid.getWorld().dropItemNaturally(squid.getLocation(), new ColoredInkSac(color).getItem());

    }
}
