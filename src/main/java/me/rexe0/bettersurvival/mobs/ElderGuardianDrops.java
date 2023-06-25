package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ElderGuardianDrops implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof ElderGuardian)) return;

        if (RandomUtil.getRandom().nextInt(3) != 0) return;

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.MENDING, 1, false);
        book.setItemMeta(meta);

        e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), book);
    }
}
