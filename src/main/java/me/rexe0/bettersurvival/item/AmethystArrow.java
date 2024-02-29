package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AmethystArrow extends Item {
    public AmethystArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Amethyst Arrow", "AMETHYST_ARROW");
    }

    @Override
    public void onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1.5f);
        for (int i = 0; i < 3; i++)
            new AmethystShard(arrow, arrow.getLocation(), entity, damage).getRunnable().runTaskTimer(BetterSurvival.getInstance(), 0, 1);
    }

    private static class AmethystShard {
        private static final Particle.DustOptions COLOR = new Particle.DustOptions(Color.fromRGB(200, 0, 255), 0.7f);

        private final Arrow arrow;
        private final Location location;
        private final Vector direction;
        private final LivingEntity entity; // The entity to ignore
        private final double damage;
        private boolean isFinished;
        private int i = 0;

        public AmethystShard(Arrow arrow, Location location, LivingEntity entity, double damage) {
            this.arrow = arrow;
            this.entity = entity;
            this.location = location;
            this.damage = damage;
            this.direction = new Vector(RandomUtil.getRandom().nextDouble(-1, 1)
                    , RandomUtil.getRandom().nextDouble(0, 1)
                    , RandomUtil.getRandom().nextDouble(-1, 1)).multiply(0.2); // 4 blocks per second
        }

        private void run() {
            if (i == 10) {
                isFinished = true;
                return;
            }

            location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, COLOR);
            for (Entity en : location.getWorld().getNearbyEntities(location, 3, 3, 3)) {
                if (!(en instanceof LivingEntity living)) continue;
                if (living.equals(entity)) continue;
                if (!living.getBoundingBox().contains(location.toVector())) continue;
                damage(living);
            }

            i++;
            direction.setY(direction.getY()-0.01);
            location.add(direction);
        }
        private void damage(LivingEntity target) {
            target.damage(damage, arrow);

            isFinished = true;
        }

        public BukkitRunnable getRunnable() {
            return new BukkitRunnable() {
                @Override
                public void run() {
                    if (isFinished()) {
                        cancel();
                        return;
                    }
                    AmethystShard.this.run();
                }
            };
        }

        public boolean isFinished() {
            return isFinished;
        }
    }
}