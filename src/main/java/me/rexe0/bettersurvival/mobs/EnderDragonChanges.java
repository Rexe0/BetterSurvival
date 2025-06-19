package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnderDragonChanges implements Listener {
    @EventHandler
    public void onExplosionDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;
        if (!(e.getEntity() instanceof EnderDragon)) return;
        // Reduce block explosion damage by 80% to Ender Dragon - Prevents Bed Cheese
        e.setDamage(e.getDamage()*0.2f);
    }
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.ENDER_DRAGON) return;
        ItemStack item = ItemType.DRAGON_SCALE.getItem().getItem();
        item.setAmount(RandomUtil.getRandom().nextInt(2, 5));
        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
    }

    private static final NamespacedKey WEATHER_BEACON_KEY = new NamespacedKey(BetterSurvival.getInstance(), ItemType.WEATHER_BEACON.name());
    @EventHandler
    public void onPickupItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!ItemDataUtil.isItem(e.getItem().getItemStack(), ItemType.DRAGON_SCALE.name())) return;
        if (player.hasDiscoveredRecipe(WEATHER_BEACON_KEY)) return;

        // Upon picking up a dragon scale, the player will learn the Weather Beacon crafting recipe
        player.discoverRecipe(WEATHER_BEACON_KEY);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof EnderDragon dragon)) return;
        if (e.isCancelled()) return;
        if (dragon.getHealth()-e.getFinalDamage() > 0) return;
        if (inSecondPhase(dragon)) return;

        e.setCancelled(true);

        if (!inTransitionPhase(dragon))
            startSecondPhase(dragon);
    }

    @EventHandler
    public void onChangePhase(EnderDragonChangePhaseEvent e) {
        EnderDragon dragon = e.getEntity();
        if (!inSecondPhase(dragon)) return;

        if (e.getNewPhase() == EnderDragon.Phase.CIRCLING) {
            int attack = RandomUtil.getRandom().nextInt(3);
            if (attack == 0) {
                lightningAttack(dragon);
                return;
            }
            if (attack == 1) {
                endermenAttack(dragon);
                return;
            }

            // 50% chance for lightning attack, 50% for fireball attack
            List<Player> players = new ArrayList<>(getPlayersInRange(dragon));
            Collections.shuffle(players);
            new BukkitRunnable() {
                private int i = 0;
                @Override
                public void run() {
                    if (i == (players.size()+1)/2) {
                        cancel();
                        return;
                    }
                    players.forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.7f, 1));

                    shootFireball(dragon, players.get(i));
                    i++;
                }
            }.runTaskTimer(BetterSurvival.getInstance(), 0, 20);
        }
    }

    private boolean inSecondPhase(EnderDragon dragon) {
        return EntityDataUtil.getIntegerValue(dragon, "dragonPhase") == 2;
    }
    private boolean inTransitionPhase(EnderDragon dragon) {
        return EntityDataUtil.getIntegerValue(dragon, "dragonPhase") == 1;
    }

    private void startSecondPhase(EnderDragon dragon) {
        EntityDataUtil.setIntegerValue(dragon, "dragonPhase", 1);
        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                if (i == 200) {
                    getPlayersInRange(dragon).forEach(p ->
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1));

                    EntityDataUtil.setIntegerValue(dragon, "dragonPhase", 2);
                    cancel();
                    return;
                }

                Location location = dragon.getWorld().getHighestBlockAt(RandomUtil.getRandom().nextInt(-80, 80),
                        RandomUtil.getRandom().nextInt(-80, 80)).getLocation();
                location.getWorld().strikeLightning(location);

                double maxHealth = dragon.getAttribute(Attribute.MAX_HEALTH).getValue();
                dragon.setHealth(Math.min(maxHealth, dragon.getHealth()+maxHealth/200));

                i++;
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 1, 1);
    }

    private void shootFireball(EnderDragon dragon, LivingEntity target) {
        LargeFireball fireball = dragon.getWorld().spawn(dragon.getEyeLocation().subtract(0, 7, 0), LargeFireball.class);
        fireball.setShooter(dragon);

        fireball.setDirection(target.getEyeLocation().subtract(dragon.getEyeLocation()).toVector().normalize());
        fireball.setYield(3);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (fireball.isDead() || fireball.getTicksLived() > 600) {
                    fireball.remove();
                    cancel();
                }
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 20);

    }

    private void lightningAttack(EnderDragon dragon) {
        List<Player> players = getPlayersInRange(dragon);

        if (players.size() == 0) return;
        players.forEach(p -> p.sendMessage(ChatColor.RED+""+ ChatColor.ITALIC+"The Ender Dragon is discharging a storm. Take cover!"));

        for (int i = 0; i < 20; i++)
            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () ->
                    dragon.getWorld().strikeLightningEffect(dragon.getLocation().add(0, 3, 0)), i*5);
        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> players.forEach(p -> {
            Location location = p.getWorld().getHighestBlockAt(p.getLocation()).getLocation();
            p.getWorld().strikeLightningEffect(location);
            if (location.distanceSquared(p.getLocation()) < 4) {
                double damage = switch (dragon.getWorld().getDifficulty()) {
                    case PEACEFUL -> 0.0;
                    case EASY -> 5.0;
                    case NORMAL -> 10.0;
                    case HARD -> 20.0;
                };
                if (dragon.getDragonBattle().hasBeenPreviouslyKilled()) damage *= 1.5f;
                p.damage(damage);
            }
        }), 110);
    }
    private void endermenAttack(EnderDragon dragon) {
        int duration = switch (dragon.getWorld().getDifficulty()) {
            default -> 0;
            case NORMAL -> 200;
            case HARD -> 400;
        };

        if (duration == 0) return;

        int amount = switch (dragon.getWorld().getDifficulty()) {
            default -> 0;
            case NORMAL -> 2;
            case HARD -> 4;
        };
        if (dragon.getDragonBattle().hasBeenPreviouslyKilled()) {
            duration *= 1.5f;
            amount *= 1.5f;
        }

        for (Player player : getPlayersInRange(dragon)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0, true));
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_ROAR, 2, 0);

            int i = amount;
            for (Enderman enderman : player.getWorld().getEntitiesByClass(Enderman.class)) {
                if (enderman.getLocation().distanceSquared(player.getLocation()) > 40000) continue;
                if (enderman.hasPotionEffect(PotionEffectType.GLOWING)) continue;
                enderman.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, true));
                enderman.setTarget(player);

                i--;
                if (i <= 0) break;
            }
        }
    }


    private List<Player> getPlayersInRange(EnderDragon dragon) {
        return dragon.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(dragon.getLocation()) < 40000)
                .toList();
    }
}
