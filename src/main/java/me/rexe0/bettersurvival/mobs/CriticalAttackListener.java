package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class CriticalAttackListener implements Listener {
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player player)) return;
        if (e.getDamager() instanceof Player) return;

        if (RandomUtil.getRandom().nextInt(4) != 0) return;
        // Critical hit
        EquipmentSlot slot = EquipmentSlot.values()[RandomUtil.getRandom().nextInt(2, 6)];
        ItemStack armor = player.getInventory().getItem(slot);

        if (armor == null || armor.getType().isAir()) {
            e.setDamage(e.getDamage() * 1.5);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 0.5f);
            return;
        }
        Damageable meta = (Damageable) armor.getItemMeta();
        meta.setDamage(meta.getDamage() + armor.getType().getMaxDurability()/50);
        armor.setItemMeta(meta);

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.14f, 1.3f);

    }
}
