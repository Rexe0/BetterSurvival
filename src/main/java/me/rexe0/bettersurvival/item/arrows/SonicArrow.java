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

public class SonicArrow extends Item {
    public SonicArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Sonic Arrow", "SONIC_ARROW");
    }

    @Override
    public double onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        return damage*0.75; // Reduce damage. Idk how much to actually reduce because doubling the speed doesn't actually double the damage I think.
    }

    @Override
    public void onArrowShoot(Player player, Arrow arrow) {
        arrow.setVelocity(arrow.getVelocity().multiply(2));
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