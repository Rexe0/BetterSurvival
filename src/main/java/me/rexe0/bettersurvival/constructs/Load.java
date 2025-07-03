package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class Load extends Modification {
    private static int currentId = 0; // Used for id'ing the load

    public static Load NONE = new Load(ChatColor.RED + "None", Material.BARRIER);

    public static Load EXPLOSIVE_DAMPENER = new Load(ChatColor.GREEN + "Explosive Dampener", Material.WATER_BUCKET, -0.2, 0);
    public static Load TNT = new Load(ChatColor.GREEN + "TNT", Material.TNT, -0.3, 10);
    public static Load HIGH_CAPACITY_TNT = new Load(ChatColor.BLUE + "High Capacity TNT", Material.TNT_MINECART, -0.4, 30);
    public static Load HIGH_POWER_TNT = new Load(ChatColor.BLUE + "High Power TNT", Material.TNT, -0.4, 5);
    public static Load EJECTION_PARACHUTE = new Load(ChatColor.BLUE + "Ejection Parachute", Material.WHITE_WOOL, -0.2, 4);
    public static Load MISSILES = new Load(ChatColor.DARK_PURPLE + "Missiles", Material.FIREWORK_ROCKET, -0.5, 3);
    public static Load HEAT_SEEKING_MISSILES = new Load(ChatColor.GOLD + "Heat-Seeking Missiles", Material.TARGET, -0.6, 2);
    public static Load SCULK_SENSING_MISSILES = new Load(ChatColor.GOLD + "Sculk-Sensing Missiles", Material.SCULK_SHRIEKER, -0.6, 2);

    private static final List<Load> allLoads;

    static {
        allLoads = List.of(
                NONE,
                EXPLOSIVE_DAMPENER,
                TNT,
                HIGH_CAPACITY_TNT,
                HIGH_POWER_TNT,
                EJECTION_PARACHUTE,
                MISSILES,
                HEAT_SEEKING_MISSILES,
                SCULK_SENSING_MISSILES
        );
        for (Load load : allLoads) {
            load.addCosts();
        }
    }
    public static List<Load> getAllLoads() {
        return allLoads;
    }


    private int uses;
    public Load(String name, Material icon) {
        this(name, icon, 0, 0);
    }

    public Load(String name, Material icon, double speed, int uses) {
        super(name, icon, 0, 0, speed, 0);
        this.id = currentId++;
        this.uses = uses;
    }

    private boolean hasAbility() {
        return getUses() > 0;
    }
    public int getUses() {
        return uses;
    }

    private void addCosts() {
        if (this == NONE) return;
        addResearchCost(Material.LEAD, 1);
        addCraftCost(Material.STRING, 2);

        if (this == EXPLOSIVE_DAMPENER) {
            addResearchCost(Material.WATER_BUCKET, 1);
            addResearchCost(Material.OBSIDIAN, 2);
            addResearchCost(Material.CLAY_BALL, 128);
            addCraftCost(Material.CLAY_BALL, 64);
            addCraftCost(Material.OBSIDIAN, 1);
        } else if (this == TNT) {
            addResearchCost(Material.TNT, 5);
            addCraftCost(Material.GUNPOWDER, 25);
            addCraftCost(Material.REDSTONE_TORCH, 5);
        } else if (this == HIGH_CAPACITY_TNT) {
            addResearchCost(Material.TNT, 15);
            addResearchCost(Material.IRON_INGOT, 10);
            addCraftCost(Material.GUNPOWDER, 75);
            addCraftCost(Material.REDSTONE_TORCH, 15);
            addCraftCost(Material.IRON_INGOT, 5);
        } else if (this == HIGH_POWER_TNT) {
            addResearchCost(Material.TNT, 12);
            addResearchCost(Material.QUARTZ, 60);
            addCraftCost(Material.GUNPOWDER, 60);
            addCraftCost(Material.GLOWSTONE, 9);
        } else if (this == EJECTION_PARACHUTE) {
            addResearchCost(Material.FEATHER, 32);
            addResearchCost(Material.WHITE_WOOL, 16);
            addCraftCost(Material.FEATHER, 10);
            addCraftCost(Material.WHITE_WOOL, 5);
        } else if (this == MISSILES) {
            addResearchCost(Material.TNT, 3);
            addResearchCost(Material.REPEATER, 5);
            addResearchCost(Material.QUARTZ, 30);
            addCraftCost(Material.GUNPOWDER, 30);
            addCraftCost(Material.REPEATER, 3);
        } else if (this == HEAT_SEEKING_MISSILES) {
            addResearchCost(Material.TNT, 2);
            addResearchCost(Material.REPEATER, 5);
            addResearchCost(Material.COMPARATOR, 5);
            addResearchCost(Material.NETHERITE_INGOT, 1);
            addCraftCost(Material.GUNPOWDER, 40);
            addCraftCost(Material.COMPARATOR, 2);
            addCraftCost(Material.NETHERITE_SCRAP, 1);
        } else if (this == SCULK_SENSING_MISSILES) {
            addResearchCost(Material.TNT, 2);
            addResearchCost(Material.REPEATER, 5);
            addResearchCost(Material.COMPARATOR, 5);
            addResearchCost(new RecipeChoice.ExactChoice(ItemType.RESONANT_INGOT.getItem().getItem()), 1);
            addCraftCost(Material.GUNPOWDER, 40);
            addCraftCost(Material.COMPARATOR, 2);
            addCraftCost(new RecipeChoice.ExactChoice(ItemType.RESONANT_INGOT.getItem().getItem()), 1);
        }
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        if (this == NONE) return description;

        if (this == EXPLOSIVE_DAMPENER) {
            description.add(ChatColor.GRAY+"Prevents the construct from");
            description.add(ChatColor.GRAY+"exploding on death.");
        } else if (this == TNT) {
            description.add(ChatColor.GRAY+"Drop up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" TNT that explode");
            description.add(ChatColor.GRAY+"on contact with terrain.");
        } else if (this == HIGH_CAPACITY_TNT) {
            description.add(ChatColor.GRAY+"Drop up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" TNT that explode");
            description.add(ChatColor.GRAY+"on contact with terrain.");
        } else if (this == HIGH_POWER_TNT) {
            description.add(ChatColor.GRAY+"Drop up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" TNT that detonate");
            description.add(ChatColor.GRAY+"in a larger blast on contact with");
            description.add(ChatColor.GRAY+"terrain.");
        } else if (this == EJECTION_PARACHUTE) {
            description.add(ChatColor.GRAY+"Quickly eject yourself from the");
            description.add(ChatColor.GRAY+"construct and gain Slow Falling");
            description.add(ChatColor.GRAY+"for "+ChatColor.GREEN+"25"+ChatColor.GRAY+" seconds.");
        } else if (this == MISSILES) {
            description.add(ChatColor.GRAY+"Launch up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" missiles");
            description.add(ChatColor.GRAY+"forwards that explode on");
            description.add(ChatColor.GRAY+"contact with any entity or");
            description.add(ChatColor.GRAY+"terrain, dealing massive damage.");
        } else if (this == HEAT_SEEKING_MISSILES) {
            description.add(ChatColor.GRAY+"Launch up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" heat-seeking");
            description.add(ChatColor.GRAY+"missiles after locking onto a");
            description.add(ChatColor.GRAY+"target.");
            description.add(" ");
            description.add(ChatColor.GRAY+"These missiles will follow");
            description.add(ChatColor.GRAY+"the target relentlessly");
            description.add(ChatColor.GRAY+"based on its heat signature");
            description.add(ChatColor.GRAY+"and will explode on contact.");
        } else if (this == SCULK_SENSING_MISSILES) {
            description.add(ChatColor.GRAY+"Launch up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" sculk-sensing");
            description.add(ChatColor.GRAY+"missiles after locking onto a");
            description.add(ChatColor.GRAY+"target.");
            description.add(" ");
            description.add(ChatColor.GRAY+"These missiles will follow");
            description.add(ChatColor.GRAY+"the target relentlessly");
            description.add(ChatColor.GRAY+"based on vibrational activity");
            description.add(ChatColor.GRAY+"and will explode on contact.");
        }
        if (hasAbility()) {
            description.add(" ");
            description.add(ChatColor.YELLOW + "Left-Click Control Stick to activate.");
        }

        return description;
    }

    public void onLeftClick(GhastConstruct construct, Player player) {
        if (!hasAbility()) return;
        int ammo = construct.getLoadAmmo();

        String str = "TNT";
        if (this == MISSILES || this == HEAT_SEEKING_MISSILES || this == SCULK_SENSING_MISSILES)
            str = "Missiles";
        else if (this == EJECTION_PARACHUTE)
            str = "Parachutes";

        if (ammo <= 0) {

            construct.sendActionBar(ChatColor.RED+"No "+str+" Left!", 30);
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 2, 1.8f);
            return;
        }

        if (this == EJECTION_PARACHUTE) {
            construct.getGhast().removePassenger(player);

            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                Vector velocity = construct.getGhast().getVelocity();
                player.setVelocity(velocity.setY(1.5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 500, 0, true, false));

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.3f, 0.85f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.1f, 1.25f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WHIRL, 2f, 1.6f);
            }, 2);
        } else if (this == TNT || this == HIGH_CAPACITY_TNT || this == HIGH_POWER_TNT) {
            spawnTNT(construct, player, this == HIGH_POWER_TNT ? 12 : 4);
        } else {
            double dot = construct.getGhast().getLocation().getDirection().dot(player.getLocation().getDirection());
            if (dot < 0.3) {
                construct.sendActionBar(ChatColor.RED+"Invalid Missile Direction!", 30);
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 2, 1.8f);
                return;
            }

            if (this == MISSILES)
                spawnMissile(construct, player);
            else {
                if (LockOnMinigame.inMinigame.contains(player))
                    return;

                // Lock on target
                RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 250, 2, e -> (e instanceof HappyGhast || e instanceof Player) && !e.equals(construct.getGhast()) && !construct.getGhast().getPassengers().contains(e));
                if (rayTraceResult == null) {
                    construct.sendActionBar(ChatColor.RED+"No Target Found!", 30);
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 2, 1.8f);
                    return;
                }
                LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
                if (target == null ) {
                    construct.sendActionBar(ChatColor.RED+"No Target Found!", 30);
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 2, 1.8f);
                    return;
                }
                LockOnMinigame minigame = new LockOnMinigame(player, construct, target,
                        this == HEAT_SEEKING_MISSILES ? Missile.MissileType.HEAT_SEEKING : Missile.MissileType.SCULK_SENSING);
                minigame.start();
                return;
            }
        }

        useAmmo(construct, ammo, str);
    }

    private void useAmmo(GhastConstruct construct, int ammo, String str) {
        ammo--;
        construct.setLoadAmmo(ammo);

        ChatColor color = ChatColor.GREEN;
        if (ammo <= getUses()/4) color = ChatColor.RED;
        else if (ammo <= getUses()/2) color = ChatColor.YELLOW;
        construct.sendActionBar(ChatColor.GRAY+str+" Left: "+color+ammo, 30);
    }


    public void spawnTNT(GhastConstruct construct, Player player, int power) {
        Location loc = construct.getGhast().getLocation();
        TNTPrimed tnt = player.getWorld().spawn(loc, TNTPrimed.class);
        tnt.setFuseTicks(400);
        tnt.setYield(power);
        tnt.setSource(player);

        loc.getWorld().playSound(loc, Sound.ENTITY_TNT_PRIMED, 0.5f, 1.05f);
        loc.getWorld().playSound(loc, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, 1f, 1f);
        loc.getWorld().playSound(loc, Sound.BLOCK_COPPER_BREAK, 1f, 0.6f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!tnt.isValid()) {
                    cancel();
                    return;
                }
                if (tnt.isOnGround()) {
                    tnt.setFuseTicks(1);
                    tnt.getWorld().playSound(tnt.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, power/2f, 0.6f);
                }
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
    }

    public void spawnMissile(GhastConstruct construct, Player player) {
        spawnMissile(construct, player, Missile.MissileType.STANDARD, null);
    }
    public void spawnMissile(GhastConstruct construct, Player player, Missile.MissileType type, LivingEntity target) {
        Missile missile = new Missile(player, construct, type, target);
        missile.start();
    }

    public class LockOnMinigame extends BukkitRunnable {
        private static final Set<Player> inMinigame = new HashSet<>();

        private final Player player;
        private final GhastConstruct construct;
        private final LivingEntity target;
        private final Missile.MissileType type;

        private double progress;

        public LockOnMinigame(Player player, GhastConstruct construct, LivingEntity target, Missile.MissileType type) {
            this.player = player;
            this.construct = construct;
            this.target = target;
            this.type = type;
            this.progress = 0.1;
        }

        @Override
        public void run() {
            if (!target.isValid() || !player.isValid() || !construct.getGhast().isValid()) {
                end();
                return;
            }

            Vector targetVec = target.getBoundingBox().getCenter().subtract(player.getEyeLocation().toVector());
            Vector playerVec = player.getLocation().getDirection();
            double angle = targetVec.angle(playerVec);

            StringBuilder builder = new StringBuilder();

            for (int i = -40; i < 41; i++) {
                ChatColor color = Math.abs(i) <= 40*progress ? ChatColor.GREEN : ChatColor.RED;
                builder.append(color).append("|");
            }
            construct.sendActionBar(builder.toString(), 5);

            if (progress >= 1) win();
            else if (progress <= 0) lose();
            else {
                if (angle < 0.12) {
                    progress += 0.04;
                } else if (angle < 0.18) progress -= 0.02;
                else progress -= 0.1;
            }
        }

        private void win() {
            String str = ChatColor.GOLD+"";
            for (int i = 0; i < 81; i++) str += "|";
            construct.sendActionBar(str, 30);

            spawnMissile(construct, player, type, target);

            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);

            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                useAmmo(construct, construct.getLoadAmmo(), "Missiles");
            }, 5);
            end();
        }

        private void lose() {
            String str = ChatColor.RED+"Target Lost";
            construct.sendActionBar(str, 30);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.6f);
            end();
        }

        private void end() {
            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                inMinigame.remove(player);
            }, 5);
            cancel();
        }

        public void start() {
            runTaskTimer(BetterSurvival.getInstance(), 0, 1);
            inMinigame.add(player);
        }
    }

    public static class Missile extends BukkitRunnable {
        private static final int POWER = 10;
        private static final int MAX_SPEED = 2;

        private final Player player;
        private final GhastConstruct construct;
        private final Vector direction;
        private final double initialSpeed;

        private final MissileType type;
        private final LivingEntity target;
        private Location flareTarget;

        private Vector velocity;
        private TNTPrimed tnt;
        private int i;

        public Missile(Player player, GhastConstruct construct, MissileType type, LivingEntity target) {
            this.player = player;
            this.construct = construct;
            this.type = type;
            this.target = target;
            this.initialSpeed = construct.getGhast().getVelocity().length();
            if (target == null)
                this.direction = player.getLocation().getDirection().normalize();
            else
                this.direction = target.getBoundingBox().getCenter().subtract(player.getEyeLocation().toVector()).normalize();


            this.velocity = direction.clone().multiply(initialSpeed).setY(-0.4);
        }

        @Override
        public void run() {
            if (!tnt.isValid()) {
                cancel();
                return;
            }
            ServerTickManager manager = Bukkit.getServerTickManager();
            if (manager.isFrozen() && manager.getFrozenTicksToRun() <= 0) return;

            if (i > 3 && i < 6) {
                velocity.setY(velocity.getY()*0.2);
            } else if (i == 6) {
                velocity.setY(0);
                tnt.setGravity(false);
            } else if (i == 7) {
                Location loc = tnt.getLocation();
                tnt.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 4f, 0.5f);
                tnt.getWorld().playSound(loc, Sound.ITEM_FIRECHARGE_USE, 2f, 0.75f);
                tnt.getWorld().playSound(loc, Sound.ENTITY_TNT_PRIMED, 4f, 1.05f);
            }
            if (i >= 7) {
                if (i > 10 && target != null) {
                    Vector target = flareLogic();

                    Vector targetVec = target.subtract(tnt.getLocation().toVector());

                    double dot = targetVec.clone().normalize().dot(direction);
                    double pullMultiplier = 0.003;
                    if (dot < 0) pullMultiplier = 0.0004;
                    else if (dot < 0.5) pullMultiplier = 0.001;

                    // Homing Calculations
                    targetVec.multiply(pullMultiplier);
                    direction.add(targetVec).normalize();
                }
                Vector vec;
                if (i <= 7+(2*MAX_SPEED)) {
                    double multiplier = ((i - 7) * 0.5);
                    vec = direction.clone().multiply(initialSpeed);
                    vec.add(direction.clone().multiply(multiplier));
                } else
                    vec = direction.clone().multiply(initialSpeed + MAX_SPEED);

                velocity = vec;

                if (checkCollision()) {
                    tnt.setFuseTicks(1);
                    tnt.getWorld().playSound(tnt.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, POWER, 0.6f);
                }
            }

            velocity.multiply(0.25);
            Location loc = tnt.getLocation();
            for (int i = 0; i < 4; i++) {
                tnt.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 1, 0, 0, 0, 0, null, true);
                if (type == MissileType.HEAT_SEEKING)
                    tnt.getWorld().spawnParticle(Particle.FLAME, loc, 5, 0.15, 0.15, 0.15, 0.01, null, true);
                else if (type == MissileType.SCULK_SENSING)
                    tnt.getWorld().spawnParticle(Particle.SONIC_BOOM, loc, 1, 0, 0, 0, 0, null, true);

                loc.add(velocity);
            }
            velocity.multiply(4);

            tnt.setVelocity(velocity);
            i++;
        }

        private Vector flareLogic() {
            if (flareTarget == null) {
                flareTarget = Miscellaneous.getActiveFlares().stream()
                        .filter(flareLoc -> flareLoc.distanceSquared(target.getLocation()) <= 25*25 && Math.random() < 0.9)
                        .min(Comparator.comparing(flareLoc -> flareLoc.distanceSquared(tnt.getLocation())))
                        .orElse(null);
            } else {
                // If flare burns out, start targeting the target again
                if (!Miscellaneous.getActiveFlares().contains(flareTarget)) flareTarget = null;
            }

            if (flareTarget == null) return target.getBoundingBox().getCenter();
            return flareTarget.toVector();
        }

        private boolean checkCollision() {
            if (tnt.isOnGround()) return true;

            if (flareTarget != null && flareTarget.distanceSquared(tnt.getLocation()) <= 25) return true;

            Block block = tnt.getLocation().getBlock();
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block relativeBlock = block.getRelative(x, 0, z);
                    if (!relativeBlock.isPassable()) return true;
                }
            }
            for (Entity entity : tnt.getNearbyEntities(10, 10, 10)) {
                if (!(entity instanceof LivingEntity livingEntity)) continue;

                double distanceReq = livingEntity instanceof HappyGhast ? 6 : 4;
                if (livingEntity.getBoundingBox().getCenter().distanceSquared(tnt.getLocation().toVector()) > distanceReq*distanceReq) continue;

                if (construct.getGhast().equals(livingEntity)) continue;
                if (construct.getGhast().getPassengers().contains(livingEntity)) continue;
                return true;
            }
            return false;
        }

        public void start() {
            Location loc = player.getLocation().subtract(0, 3, 0);
            this.tnt = player.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(600);
            tnt.setYield(POWER);
            tnt.setSource(player);

            loc.getWorld().playSound(loc, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, 2f, 1f);
            loc.getWorld().playSound(loc, Sound.BLOCK_COPPER_BREAK, 2f, 0.6f);

            runTaskTimer(BetterSurvival.getInstance(), 0, 1);
        }

        public enum MissileType {
            STANDARD,
            HEAT_SEEKING,
            SCULK_SENSING
        }
    }
}
