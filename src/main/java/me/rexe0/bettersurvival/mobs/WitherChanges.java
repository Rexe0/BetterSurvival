package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.WitherRing;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftWither;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WitherChanges implements Listener {
    private static WitherChanges instance;

    public static WitherChanges getInstance() {
        if (instance == null)
            instance = new WitherChanges();
        return instance;
    }

    private final Random random = new Random();

    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof Wither wither)) return;
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_WITHER) return;
        boolean playerHasBadOmen = false;
        for (Player player : wither.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(wither.getLocation()) > 10000) continue;
            if (player.hasPotionEffect(PotionEffectType.BAD_OMEN)) {
                playerHasBadOmen = true;
                player.removePotionEffect(PotionEffectType.BAD_OMEN);
                break;
            }
        }

        if (!playerHasBadOmen) return;

        convertWither(wither);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Wither wither)) return;
        if (EntityDataUtil.getIntegerValue(wither, "buffedWither") == 0) return;

        playWitherSound(wither, Sound.ENTITY_WITHER_DEATH, 4);
        wither.getWorld().dropItemNaturally(wither.getLocation(), new WitherRing().getItem());
    }

    @EventHandler
    public void onWitherSkeletonDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof WitherSkeleton)) return;
        // Prevent wither skeletons from being damaged by withers
        if (e.getDamager() instanceof WitherSkull) e.setCancelled(true);
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Wither wither)) return;
        if (EntityDataUtil.getIntegerValue(wither, "buffedWither") == 0) return;
        playWitherSound(wither, Sound.ENTITY_WITHER_HURT, 2);
    }

    @EventHandler
    public void onWitherSkullShoot(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof WitherSkull witherSkull)) return;
        if (!(witherSkull.getShooter() instanceof Wither wither)) return;
        if (EntityDataUtil.getIntegerValue(wither, "buffedWither") == 0) return;
        playWitherSound(wither, Sound.ENTITY_WITHER_SHOOT, 0.5f);
    }

    @EventHandler
    public void onWitherSpawnHeal(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.WITHER_SPAWN) return;
        if (!(e.getEntity() instanceof Wither wither)) return;
        if (EntityDataUtil.getIntegerValue(wither, "buffedWither") == 0) return;
        double maxHealth = wither.getAttribute(Attribute.MAX_HEALTH).getValue();

        // Has to restore 2/3 of its max health over the course of 22 heal ticks (220 invulnerability ticks)
        e.setAmount(((2*maxHealth)/3)/22);

        if (wither.getInvulnerabilityTicks() == 0) {
            playWitherSound(wither, Sound.ENTITY_WITHER_SPAWN, 4);
            EntityDataUtil.setIntegerValue(wither, "witherPhase", 1);
            wither.setHealth(wither.getAttribute(Attribute.MAX_HEALTH).getValue());

            float hardness = wither.getLocation().add(0, 4, 0).getBlock().getType().getHardness();
            if (hardness > 50 || hardness == -1) {
                // Prevent end portal cheese
                wither.teleport(wither.getLocation().add(0, 10, 0));
            }
        }
    }

    private void convertWither(Wither wither) {
        double maxHealth = switch (wither.getWorld().getDifficulty()) {
            case HARD -> 600;
            case NORMAL -> 500;
            default -> 400;
        };
        wither.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        wither.getAttribute(Attribute.ARMOR).setBaseValue(12);
        wither.getAttribute(Attribute.SCALE).setBaseValue(1.7);
        wither.setSilent(true); // Make wither silent so we can play our own lower pitch sounds

        wither.getBossBar().setTitle(ChatColor.DARK_RED+"Primeval Wither");
        wither.getBossBar().setColor(BarColor.RED);

        EntityDataUtil.setIntegerValue(wither, "buffedWither", 1);

        playWitherSound(wither, Sound.ENTITY_WITHER_AMBIENT, 4);
    }

    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Wither wither : world.getEntitiesByClass(Wither.class)) {
                if (EntityDataUtil.getIntegerValue(wither, "buffedWither") == 0) continue;
                // Despawn Check
                despawnCheck(wither);

                // Phase Check
                phaseCheck(wither);

                // Particles
                wither.getWorld().spawnParticle(Particle.LARGE_SMOKE, wither.getLocation(), 3, 1, 1, 1, 0.02);

                // Ambient Sound
                if (wither.getInvulnerabilityTicks() == 0 && random.nextInt(100) == 0) playWitherSound(wither, Sound.ENTITY_WITHER_AMBIENT, 2);

                double healthRatio = wither.getHealth() / wither.getAttribute(Attribute.MAX_HEALTH).getBaseValue();

                // Additional Skull Attacks as his health gets lower
                double randRatio = 0.9+(healthRatio/10);
                for (int i = 0; i < 3; i++) {
                    if (randRatio > random.nextDouble()) continue;
                    LivingEntity target = wither.getTarget(switch (i) {
                        case 1 -> Wither.Head.LEFT;
                        case 2 -> Wither.Head.RIGHT;
                        default -> Wither.Head.CENTER;
                    });
                    shootWitherSkull(wither, i, target);
                }
            }
        }
    }

    private void despawnCheck(Wither wither) {
        if (wither == null || !wither.isValid()) return;
        if (wither.getTicksLived() > 20*60*20) {
            for (Player player : wither.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(wither.getLocation()) > 10000) continue;
                player.sendMessage(ChatColor.RED+"The Primeval Wither grew tired of your weakness and inaction.");
            }

            playWitherSound(wither, Sound.ENTITY_WITHER_AMBIENT, 2);
            wither.getWorld().spawnParticle(Particle.LARGE_SMOKE, wither.getLocation(), 300, 1, 1, 1, 0.5);
            wither.remove();
        }
    }

    private void phaseCheck(Wither wither) {
        if (!wither.isValid()) return;
        int phase = EntityDataUtil.getIntegerValue(wither, "witherPhase");

        double healthRatio = wither.getHealth() / wither.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        double nextPhase = switch (phase) {
            case 1 -> 0.8;
            case 2 -> 0.6;
            case 3 -> 0.4;
            case 4 -> 0.2;
            default -> 0;
        };

        if (healthRatio > nextPhase) return;

        switch (phase) {
            case 1 -> {
                for (int i = 0; i < 3; i++) {
                    wither.getWorld().spawn(wither.getLocation(), WitherSkeleton.class);
                }
            }
            case 2 -> {
                FrenzyRunnable runnable = new FrenzyRunnable(wither);
                runnable.start();
            }
            case 3 -> {
                for (int i = 0; i < 3; i++) {
                    WitherSkeleton skeleton = wither.getWorld().spawn(wither.getLocation(), WitherSkeleton.class);
                    EntityDataUtil.preventItemDrops(skeleton);
                    skeleton.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
                    skeleton.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
                }
            }
            case 4 -> {
                BarrageRunnable runnable = new BarrageRunnable(wither);
                runnable.start();
            }
        }

        EntityDataUtil.setIntegerValue(wither, "witherPhase", phase + 1);
    }

    private void shootWitherSkull(Wither wither, int head, LivingEntity target) {
        if (target == null || !target.isValid()) return;
        if (!wither.hasAI()) return;
        WitherBoss nmsWither = ((CraftWither)wither).getHandle();

        try {
            Method method = nmsWither.getClass().getDeclaredMethod("performRangedAttack", int.class, net.minecraft.world.entity.LivingEntity.class);
            method.setAccessible(true);
            method.invoke(nmsWither, head, ((CraftLivingEntity)target).getHandle());
            method.setAccessible(false);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void playWitherSound(Wither wither, Sound sound, float volume) {
        wither.getWorld().playSound(wither.getLocation(), sound, SoundCategory.HOSTILE, volume, 0.75f);
    }


    private class BarrageRunnable extends BukkitRunnable {
        private Wither wither;
        private int i;
        public BarrageRunnable(Wither wither) {
            this.wither = wither;
            this.i = 0;
        }
        @Override
        public void run() {
            if (i >= 80) {
                cancel();
                return;
            }
            if (i % 4 == 0) {
                for (int i = 0; i < 3; i++) {
                    LivingEntity target = wither.getTarget(switch (i) {
                        case 1 -> Wither.Head.LEFT;
                        case 2 -> Wither.Head.RIGHT;
                        default -> Wither.Head.CENTER;
                    });
                    shootWitherSkull(wither, i, target);
                }
            }

            i++;
        }
        public void start() {
            runTaskTimer(BetterSurvival.getInstance(), 0, 1);
        }
    }

    private class FrenzyRunnable extends BukkitRunnable {

        private Wither wither;
        private boolean mobGriefing;

        private boolean isGrounded;
        private int i;


        public FrenzyRunnable(Wither wither) {
            this.wither = wither;
            this.mobGriefing = wither.getWorld().getGameRuleValue(GameRule.MOB_GRIEFING);
            this.i = 0;
        }

        @Override
        public void run() {
            wither.getBossBar().setProgress(wither.getHealth()/wither.getAttribute(Attribute.MAX_HEALTH).getValue());
            if (!isGrounded) {
                Location loc = wither.getLocation();
                loc.subtract(0, 0.5, 0);

                if (!loc.getBlock().isPassable()) {
                    isGrounded = true;
                    wither.getWorld().playSound(wither.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 5, 0.5f);
                    wither.getWorld().spawnParticle(Particle.BLOCK, wither.getLocation(), 400, 1.5, 1.5, 1.5, 0, loc.getBlock().getBlockData());
                    wither.getWorld().spawnParticle(Particle.SCULK_SOUL, wither.getLocation(), 200, 1, 1, 1, 0.02);

                    if (mobGriefing)
                        shockwaveAnimation(loc, 1);
                    return;
                }

                wither.getWorld().spawnParticle(Particle.LARGE_SMOKE, wither.getLocation(), 15, 1, 1, 1, 0.02);
                wither.getWorld().spawnParticle(Particle.SCULK_SOUL, wither.getLocation(), 10, 1, 1, 1, 0.02);
                wither.teleport(loc);
                return;
            }

            if (i > 800) {
                end();
                return;
            }

            if (wither.getHealth()/wither.getAttribute(Attribute.MAX_HEALTH).getValue() <= 0.5) {
                playWitherSound(wither, Sound.ENTITY_WITHER_HURT, 4);
                end();
                return;
            }

            if (i % 20 == 0 && i != 0) {
                // Perform explosion attack
                int radius = i/20;

                Location location = wither.getLocation().add(0, 1, 0);
                wither.getWorld().spawnParticle(Particle.EXPLOSION, location, 50, 0, 0, 0, Math.max(0, (radius*0.5)-1));
                for (LivingEntity livingEntity : location.getWorld().getLivingEntities()) {
                    if (livingEntity.getLocation().distanceSquared(location) > radius*radius) continue;
                    livingEntity.damage(20, wither);
                }

                wither.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 3, 0.6f);

                if (mobGriefing) {
                    Set<Location> blocks = sphere(location, radius, false);
                    BlockData data;
                    FallingBlock fallingBlock;
                    Vector direction;
                    for (Location loc : blocks) {
                        if (loc.getBlock().isPassable() || loc.getBlock().getType().getHardness() > 50 || loc.getBlock().getType().getHardness() == -1) continue;
                        if (Math.abs(location.getY() - loc.getY()) > 10)
                            continue; // Only break blocks within 5 blocks on the y-axis of the wither

                        data = loc.getBlock().getBlockData();
                        loc.getBlock().setType(Material.AIR);

                        if (random.nextInt(i < 200 ? 3 : i / 10) != 0) continue;
                        direction = location.toVector().subtract(loc.toVector()).normalize();
                        direction.multiply(-1);
                        direction.setY(1);

                        fallingBlock = loc.getWorld().spawnFallingBlock(loc, data);
                        fallingBlock.setCancelDrop(true);
                        fallingBlock.setVelocity(direction);
                    }
                }
            }

            i++;
        }
        private void end() {
            wither.setAI(true);
            wither.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(0);
            wither.getAttribute(Attribute.ARMOR).setBaseValue(15);
            cancel();
        }
        public void start() {
            runTaskTimer(BetterSurvival.getInstance(), 0, 1);
            wither.setAI(false);
            wither.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(20);
            wither.getAttribute(Attribute.ARMOR).setBaseValue(20);
        }

        private void shockwaveAnimation(Location location, int radius) {
            if (radius >= 15) return;
            Set<Location> blocks = sphere(location, radius, true);

            BlockData data;
            FallingBlock fallingBlock;
            for (Location loc : blocks) {
                if (loc.getBlock().isPassable() || loc.getBlock().getType().getHardness() > 50 || loc.getBlock().getType().getHardness() == -1) continue;
                if (!shouldUseLocation(loc)) continue;

                data = loc.getBlock().getBlockData();
                loc.getBlock().setType(Material.AIR);

                fallingBlock = loc.getWorld().spawnFallingBlock(loc, data);
                fallingBlock.setCancelDrop(false);
                fallingBlock.setVelocity(new Vector(0, 0.4, 0));
            }

            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> shockwaveAnimation(location, radius + 1), 4);
        }


        private Set<Location> sphere(Location location, int radius, boolean isCircle){
            Set<Location> blocks = new HashSet<>();
            World world = location.getWorld();
            int X = location.getBlockX();
            int Y = location.getBlockY();
            int Z = location.getBlockZ();
            int radiusSquared = radius * radius;

            Location loc;
            if (isCircle) {
                for (int x = X - radius; x <= X + radius; x++)
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        loc = new Location(world, x, Y, z);
                        if ((X - x) * (X - x) + (Z - z) * (Z - z) <= radiusSquared)
                            blocks.add(loc);
                    }

            } else {
                double distanceSquared;
                for (int x = X - radius; x <= X + radius; x++)
                    for (int z = Z - radius; z <= Z + radius; z++)
                        for (int y = Y - radius; y <= Y + radius; y++) {
                            loc = new Location(world, x, y, z);
                            distanceSquared = (X - x) * (X - x) + (Z - z) * (Z - z) + (Y - y) * (Y - y);
                            if (distanceSquared <= radiusSquared && distanceSquared >= ((radius - 1) * (radius - 1)))
                                blocks.add(loc);
                        }
                return blocks;
            }
            return makeHollow(blocks, !isCircle);
        }
        private boolean shouldUseLocation(Location loc) {
            // Try and get surface block (up to 5 blocks above)
            int k = 0;
            while (!loc.getBlock().getRelative(BlockFace.UP).isPassable()) {
                if (k == 5) {
                    k = -1;
                    break;
                }
                loc.setY(loc.getY() + 1);
                k++;
            }

            return k != -1;
        }
        private Set<Location> makeHollow(Set<Location> blocks, boolean sphere) {
            Set<Location> edge = new HashSet<>();

            World w;
            int X;
            int Y;
            int Z;
            Location front;
            Location back;
            Location left;
            Location right;
            if (!sphere) {
                for (Location l : blocks) {
                    w = l.getWorld();
                    X = l.getBlockX();
                    Y = l.getBlockY();
                    Z = l.getBlockZ();
                    front = new Location(w, X + 1, Y, Z);
                    back = new Location(w, X - 1, Y, Z);
                    left = new Location(w, X, Y, Z + 1);
                    right = new Location(w, X, Y, Z - 1);

                    if (!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right)))
                        edge.add(l);
                }
                return edge;
            }

            Location top;
            Location bottom;
            for (Location l : blocks) {
                w = l.getWorld();
                X = l.getBlockX();
                Y = l.getBlockY();
                Z = l.getBlockZ();
                front = new Location(w, X + 1, Y, Z);
                back = new Location(w, X - 1, Y, Z);
                left = new Location(w, X, Y, Z + 1);
                right = new Location(w, X, Y, Z - 1);
                top = new Location(w, X, Y + 1, Z);
                bottom = new Location(w, X, Y - 1, Z);
                if (!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right) && blocks.contains(top) && blocks.contains(bottom)))
                    edge.add(l);

            }
            return edge;
        }
    }
}
