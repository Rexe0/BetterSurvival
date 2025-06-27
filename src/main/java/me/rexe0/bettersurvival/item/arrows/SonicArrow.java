package me.rexe0.bettersurvival.item.arrows;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SonicArrow extends Item {
    public SonicArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Sonic Arrow", "SONIC_ARROW");
    }

    @Override
    public double onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        return damage*1.5;
    }

    @Override
    public void onArrowShoot(Player player, Arrow arrow) {
        Vector velocity = arrow.getVelocity();
        double length = velocity.length();
        length = Math.min(4, length*2);
        arrow.setVelocity(velocity.normalize().multiply(length));

        if (length < 4) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isOnGround()) {
                    cancel();
                    return;
                }
                arrow.getWorld().spawnParticle(Particle.SONIC_BOOM, arrow.getLocation(), 1, 0, 0, 0, 0, null, true);
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
    }
}