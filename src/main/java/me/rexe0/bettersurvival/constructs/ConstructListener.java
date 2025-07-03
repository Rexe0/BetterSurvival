package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

public class ConstructListener implements Listener {
    @EventHandler
    public void onGhastExplosionDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
            e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;
        double health = ghast.getHealth();
        if (health <= 4) return;
        if (Math.random() < (e.getDamageSource().getDamageType() == DamageType.FIREWORKS ? 0.8 : 0.5)) return;
        ghast.setFireTicks(Math.random() < 0.5 ? 101 : 201);
        // Chance for ghast to be set on fire when taking explosive damage from missiles, etc.
    }
    @EventHandler
    public void onGhastDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) return;
        if (ghast.getScoreboardTags().contains("isDead")) return;

        // Cancel damage to prevent ghast velocity from staggering
        e.setCancelled(true);
        double health = ghast.getHealth();
        double amount = e.getFinalDamage();
        health -= amount;

        Sound sound = Sound.ITEM_MACE_SMASH_AIR;
        if (amount >= 10) sound = Sound.ITEM_MACE_SMASH_GROUND_HEAVY;
        else if (amount >= 5) sound = Sound.ITEM_MACE_SMASH_GROUND;
        ghast.getWorld().playSound(ghast.getLocation(), sound, 20, 0.8f);


        if (health <= 0) {
            ghast.addScoreboardTag("isDead");
            ghast.damage(100);
        } else {
            ghast.setHealth(health);
            ghast.playHurtAnimation(0);
        }
    }

    @EventHandler
    public void onGhastDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) return;
        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());

        int power = construct.getEngine().useFuel(construct.getFuel());
        if (power > 0 && construct.getLoad() != Load.EXPLOSIVE_DAMPENER) {
            // Explode on death if it has fuel
            power *= construct.getFuel().getAmount();
            boolean hasFire = construct.getEngine() == Engine.MOLTEN;

            float explosionPower = Math.min(20, power/750f);
            double d = explosionPower/10;


            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                ghast.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, ghast.getLocation(), (int) Math.ceil(explosionPower/4), d, d, d, 0);
                ghast.getWorld().spawnParticle(Particle.LARGE_SMOKE, ghast.getLocation(), (int) (explosionPower*10), d*0.75f, d*0.75f, d*0.75f, 0);
                ghast.getWorld().spawnParticle(Particle.LARGE_SMOKE, ghast.getLocation(), (int) (explosionPower*20), 0, 0, 0, 0.4);
                ghast.getWorld().spawnParticle(Particle.LAVA, ghast.getLocation(), (int) (explosionPower*5), d, d, d, 0);

                ghast.getWorld().playSound(ghast.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, explosionPower/2, 0.6f);
                ghast.getWorld().createExplosion(ghast.getLocation(), Math.min(15, explosionPower), hasFire, ghast.getWorld().getGameRuleValue(GameRule.MOB_GRIEFING));
            }, 5);
        } else
            ghast.getWorld().dropItemNaturally(ghast.getLocation(), construct.getFuel());


        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), ghast::remove, 5);
        GhastConstruct.removeConstruct(ghast.getUniqueId());
    }
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof HappyGhast ghast)) return;
        if (e.isCancelled()) return;

        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());
        if (construct == null) return;

        ItemStack item = e.getPlayer().getEquipment().getItem(e.getHand());
        Material type = item.getType();
        if (type.isAir()) return;
        if (type == Material.SHEARS) {
            construct.setHarness(Harness.NONE);
            construct.update();
            return;
        }

        if (Tag.ITEMS_HARNESSES.isTagged(type))
            for (Harness harness : Harness.getAllHarnesses())
                if (harness.getHarness().getType() == type) {
                    construct.setHarness(harness);
                    construct.update();
                    return;
                }

    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player player)) return;
        if (!(player.getVehicle() instanceof HappyGhast ghast)) return;
        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());

        if (!construct.hasAimAssistance()) return;

        if (e.getEntity() instanceof Arrow arrow) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!arrow.isValid() || arrow.isOnGround()) {
                        cancel();
                        return;
                    }
                    BoundingBox bb;
                    for (HappyGhast g : arrow.getWorld().getEntitiesByClass(HappyGhast.class)) {
                        if (g.equals(ghast)) continue;
                        if (g.getLocation().distanceSquared(arrow.getLocation()) > 100) continue;
                        bb = g.getBoundingBox().expand(4);
                        if (!bb.contains(arrow.getLocation().toVector())) continue;
                        arrow.setVelocity(bb.getCenter().subtract(arrow.getLocation().toVector()));
                        cancel();
                        return;
                    }
                }
            }.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
        } else if (e.getEntity() instanceof Firework firework) {
            firework.setVelocity(firework.getVelocity().multiply(3));
        }
    }


    public static void run() {
//        for (World world : Bukkit.getWorlds()) {
//            ServerLevel level = ((CraftWorld)world).getHandle();
//            for (HappyGhast ghast : world.getEntitiesByClass(HappyGhast.class)) {
//                // Weird cast because for some reason CraftHappyGhast.getHandle() returns NoSuchMethodException
//                net.minecraft.world.entity.animal.Animal nmsGhast = ((CraftAnimals) ghast).getHandle();
//                if (!(nmsGhast instanceof GhastConstructEntity)) {
//                    Location loc = ghast.getLocation();
//
//                    GhastConstructEntity construct = new GhastConstructEntity(level);
//                    construct.setPos(loc.getX(), loc.getY(), loc.getZ());
//                    construct.setBodyArmorItem(CraftItemStack.asNMSCopy(ghast.getEquipment().getItem(EquipmentSlot.BODY)));
//                    level.addFreshEntity(construct);
//
//                    ghast.remove();
//                }
//            }
//        }
    }
}
