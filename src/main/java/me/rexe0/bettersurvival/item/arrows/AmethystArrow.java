package me.rexe0.bettersurvival.item.arrows;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
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
    public double onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1.5f);
        for (int i = 0; i < 3; i++)
            new AmethystShard(player, arrow.getLocation(), entity, damage).getRunnable().runTaskTimer(BetterSurvival.getInstance(), 0, 1);
        return damage+2;
    }

    private static class AmethystShard {
        private static final Particle.DustOptions COLOR = new Particle.DustOptions(Color.fromRGB(200, 0, 255), 0.7f);

        private final Player player;
        private final Location location;
        private final Vector direction;
        private final LivingEntity entity; // The entity to ignore
        private final double damage;
        private boolean isFinished;
        private int i = 0;

        public AmethystShard(Player player, Location location, LivingEntity entity, double damage) {
            this.player = player;
            this.entity = entity;
            this.location = location;
            this.damage = damage;
            this.direction = new Vector(RandomUtil.getRandom().nextDouble(-1, 1)
                    , RandomUtil.getRandom().nextDouble(0, 1)
                    , RandomUtil.getRandom().nextDouble(-1, 1)).multiply(0.4); // 8 blocks per second
        }

        private void run() {
            if (i == 6) {
                isFinished = true;
                return;
            }

            // Slight tracking
            LivingEntity closest = getClosestTarget();
            if (closest != null) {
                Vector dir = closest.getBoundingBox().getCenter().toLocation(location.getWorld()).subtract(location).toVector().normalize();
                dir.multiply(0.1);
                direction.add(dir);
            }

            direction.multiply(0.25);
            for (int i = 0; i < 4; i++) {
                location.add(direction);
                location.getWorld().spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, COLOR);
            }
            direction.multiply(4);
            for (Entity en : location.getWorld().getNearbyEntities(location, 3, 3, 3)) {
                if (!(en instanceof LivingEntity living)) continue;
                if (living.equals(entity)) continue;
                if (!living.getBoundingBox().expand(0.4).contains(location.toVector())) continue;
                damage(living);
            }

            i++;
            direction.setY(direction.getY()-0.03);
        }
        private void damage(LivingEntity target) {
            target.damage(damage, player);

            isFinished = true;
        }

        private LivingEntity getClosestTarget() {
            LivingEntity closest = null;
            double closestDistance = Double.MAX_VALUE;
            for (Entity en : location.getWorld().getNearbyEntities(location, 5, 5, 5)) {
                if (!(en instanceof LivingEntity living)) continue;
                if (living.equals(entity)) continue;
                double distance = living.getLocation().distance(location);
                if (distance < closestDistance) {
                    closest = living;
                    closestDistance = distance;
                }
            }
            return closest;
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