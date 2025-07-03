package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Miscellaneous extends Modification {
    private static final List<Location> activeFlares = new ArrayList<>();

    public static List<Location> getActiveFlares() {
        return activeFlares;
    }

    private static final FireworkEffect FLARE_EFFECT = FireworkEffect.builder()
            .trail(true)
            .with(FireworkEffect.Type.BURST)
            .withColor(Color.fromRGB(255, 242, 59), Color.fromRGB(237, 166, 0), Color.fromRGB(255, 153, 0), Color.fromRGB(255, 221, 0))
            .withFade(Color.fromRGB(179, 184, 174), Color.fromRGB(148, 148, 144)).build();

    private static int currentId = 0; // Used for id'ing the Miscellaneous items

    public static Miscellaneous NONE = new Miscellaneous(ChatColor.RED + "None", Material.BARRIER);

    public static Miscellaneous NIGHT_VISION = new Miscellaneous(ChatColor.GREEN + "Night Vision", Material.ENDER_EYE);
    public static Miscellaneous RADAR = new Miscellaneous(ChatColor.BLUE + "Radar", Material.COMPASS, -2, 0);
    public static Miscellaneous FIRE_EXTINGUISHER = new Miscellaneous(ChatColor.BLUE + "Fire Extinguisher", Material.SPLASH_POTION, -3, 3);
    public static Miscellaneous FLARES = new Miscellaneous(ChatColor.BLUE + "Flares", Material.BLAZE_POWDER, -4, 2);
    public static Miscellaneous SCULK_RADAR = new Miscellaneous(ChatColor.DARK_PURPLE + "Sculk Radar", Material.RECOVERY_COMPASS, -5, 0);
    public static Miscellaneous AIM_ASSISTANCE = new Miscellaneous(ChatColor.DARK_PURPLE + "Aim Assistance", Material.SPECTRAL_ARROW, -5, 0);
    public static Miscellaneous CLOAKING = new Miscellaneous(ChatColor.DARK_PURPLE + "Cloaking", Material.ECHO_SHARD, -10, 0);
    public static Miscellaneous ULTIMATE_FIGHTER_TECHNOLOGY = new Miscellaneous(ChatColor.GOLD + "Ultimate Fighter Technology", Material.TOTEM_OF_UNDYING, -8, 3);





    private static final List<Miscellaneous> allMiscellaneous;

    static {
        allMiscellaneous = List.of(
                NONE,
                NIGHT_VISION,
                RADAR,
                FIRE_EXTINGUISHER,
                FLARES,
                SCULK_RADAR,
                AIM_ASSISTANCE,
                CLOAKING

        );
        for (Miscellaneous miscellaneous : allMiscellaneous) {
            miscellaneous.addCosts();
        }
    }
    public static List<Miscellaneous> getAllMiscellaneous() {
        return allMiscellaneous;
    }


    private int uses;
    public Miscellaneous(String name, Material icon) {
        this(name, icon, 0, 0);
    }

    public Miscellaneous(String name, Material icon, double armor, int uses) {
        super(name, icon, 0, armor, 0, 0);
        this.id = currentId++;
        this.uses = uses;
    }

    public int getUses() {
        return uses;
    }

    private boolean hasAbility() {
        return getUses() > 0;
    }
    private void addCosts() {
        if (this == NONE) return;

        if (this == NIGHT_VISION) {
            addResearchCost(Material.GOLDEN_CARROT, 8);
            addCraftCost(Material.IRON_INGOT, 10);
        } else if (this == RADAR) {
            addResearchCost(Material.COMPASS, 4);
            addResearchCost(Material.GOLD_INGOT, 20);
            addResearchCost(Material.COPPER_INGOT, 32);
            addCraftCost(Material.COMPASS, 2);
            addCraftCost(Material.GOLD_INGOT, 16);
        } else if (this == FIRE_EXTINGUISHER) {
            addResearchCost(Material.WATER_BUCKET, 1);
            addResearchCost(Material.IRON_INGOT, 16);
            addResearchCost(Material.REDSTONE, 32);
            addCraftCost(Material.WATER_BUCKET, 1);
            addCraftCost(Material.IRON_INGOT, 8);
        } else if (this == FLARES) {
            addResearchCost(Material.FLINT_AND_STEEL, 1);
            addResearchCost(Material.GLOWSTONE_DUST, 64);
            addResearchCost(Material.REDSTONE_TORCH, 8);
            addCraftCost(Material.GLOWSTONE_DUST, 32);
            addCraftCost(Material.REDSTONE_TORCH, 4);
        } else if (this == SCULK_RADAR) {
            addResearchCost(Material.COMPASS, 4);
            addResearchCost(Material.DIAMOND, 5);
            addResearchCost(new RecipeChoice.ExactChoice(ItemType.RESONANT_INGOT.getItem().getItem()), 2);
            addCraftCost(Material.COMPASS, 4);
            addCraftCost(new RecipeChoice.ExactChoice(ItemType.RESONANT_INGOT.getItem().getItem()), 1);
        } else if (this == AIM_ASSISTANCE) {
            addResearchCost(Material.TARGET, 16);
            addResearchCost(Material.DIAMOND, 8);
            addResearchCost(Material.REDSTONE, 64);
            addResearchCost(new RecipeChoice.ExactChoice(ItemType.PLATINUM_INGOT.getItem().getItem()), 2);
            addCraftCost(Material.REDSTONE, 64);
            addCraftCost(new RecipeChoice.ExactChoice(ItemType.PLATINUM_INGOT.getItem().getItem()), 2);
        } else if (this == CLOAKING) {
            addResearchCost(Material.END_STONE, 64);
            addResearchCost(Material.BLACKSTONE, 32);
            addResearchCost(Material.DIAMOND, 8);
            addCraftCost(Material.END_STONE, 64);
            addCraftCost(Material.BLACKSTONE, 32);
        } else if (this == ULTIMATE_FIGHTER_TECHNOLOGY) {
            addResearchCost(Material.RECOVERY_COMPASS, 1);
            addResearchCost(Material.NETHERITE_INGOT, 3);
            addResearchCost(new RecipeChoice.ExactChoice(ItemType.RESONANT_INGOT.getItem().getItem()), 4);
            addResearchCost(Material.REDSTONE_TORCH, 64);
            addResearchCost(Material.GLOWSTONE, 64);
            addResearchCost(Material.COPPER_BLOCK, 64);

            addCraftCost(Material.COMPASS, 4);
            addCraftCost(Material.NETHERITE_INGOT, 1);
            addCraftCost(new RecipeChoice.ExactChoice(ItemType.RESONANT_INGOT.getItem().getItem()), 1);
            addCraftCost(Material.DIAMOND, 4);
            addCraftCost(Material.REDSTONE_TORCH, 64);
            addCraftCost(Material.GLOWSTONE_DUST, 64);
            addCraftCost(Material.COPPER_INGOT, 64);
        }

    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        if (this == NONE) return description;

        if (this == NIGHT_VISION) {
            description.add(ChatColor.GRAY+"Gain Night Vision while");
            description.add(ChatColor.GRAY+"riding this construct.");
        } else if (this == RADAR) {
            description.add(ChatColor.GRAY+"Gain a radar which detects");
            description.add(ChatColor.GRAY+"the unobstructed presence of");
            description.add(ChatColor.GRAY+"players within "+ChatColor.GREEN+"250"+ChatColor.GRAY+" blocks.");
        } else if (this == FIRE_EXTINGUISHER) {
            description.add(ChatColor.GRAY+"Extinguish any fires");
            description.add(ChatColor.GRAY+"present on the construct,");
            description.add(ChatColor.GRAY+"up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" times.");
        } else if (this == FLARES) {
            description.add(ChatColor.GRAY+"Release flares, up to "+ChatColor.GREEN+getUses()+ChatColor.GRAY+" times");
            description.add(ChatColor.GRAY+"which burn brightly, serving");
            description.add(ChatColor.GRAY+"as decoys for heat-seeking");
            description.add(ChatColor.GRAY+"missiles.");
        } else if (this == SCULK_RADAR) {
            description.add(ChatColor.GRAY+"Gain an enhanced radar which");
            description.add(ChatColor.GRAY+"detects the unobstructed presence");
            description.add(ChatColor.GRAY+"of players within "+ChatColor.GREEN+"500"+ChatColor.GRAY+" blocks");
            description.add(ChatColor.GRAY+"with distance indicators.");
        } else if (this == AIM_ASSISTANCE) {
            description.add(ChatColor.GRAY+"Increases the target acquisition");
            description.add(ChatColor.GRAY+"of arrows shot from bows and");
            description.add(ChatColor.GRAY+"crossbows and increases the");
            description.add(ChatColor.GRAY+"speed of firework rockets");
            description.add(ChatColor.GRAY+"shot from crossbows.");
        } else if (this == CLOAKING) {
            description.add(ChatColor.GRAY+"Gain partial invisibility and");
            description.add(ChatColor.GRAY+"become undetectable by passive");
            description.add(ChatColor.GRAY+"radar technology.");
        } else if (this == ULTIMATE_FIGHTER_TECHNOLOGY) {
            description.add(ChatColor.GRAY+"Gain Night Vision, Enhanced Radar,");
            description.add(ChatColor.GRAY+"Aim Assistance and up to "+ChatColor.GREEN+getUses());
            description.add(ChatColor.GRAY+"flares.");
        }

        if (hasAbility()) {
            description.add(" ");
            description.add(ChatColor.YELLOW + "Right-Click Control Stick to activate.");
        }
        return description;
    }


    public void onRightClick(GhastConstruct construct, Player player) {
        if (!hasAbility()) return;
        int ammo = construct.getMiscellaneousAmmo();

        String str = "Flares";
        if (this == FIRE_EXTINGUISHER) str = "Extinguishes";

        if (ammo <= 0) {
            construct.sendActionBar(ChatColor.RED+"No "+str+" Left!", 30);
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 2, 1.8f);
            return;
        }

        if (this == FLARES || this == ULTIMATE_FIGHTER_TECHNOLOGY)
            releaseFlares(construct);
        else if (this == FIRE_EXTINGUISHER) {
            if (construct.getGhast().getFireTicks() > 0)
                construct.getGhast().setFireTicks(0);
            else {
                construct.sendActionBar(ChatColor.RED+"No Fire to Extinguish!", 30);
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 2, 1.8f);
                return;
            }
        }


        useAmmo(construct, ammo, str);
    }

    private void useAmmo(GhastConstruct construct, int ammo, String str) {
        ammo--;
        construct.setMiscellaneousAmmo(ammo);

        ChatColor color = ChatColor.GREEN;
        if (ammo <= getUses()/4) color = ChatColor.RED;
        else if (ammo <= getUses()/2) color = ChatColor.YELLOW;
        construct.sendActionBar(ChatColor.GRAY+str+" Left: "+color+ammo, 30);
    }


    private void releaseFlares(GhastConstruct construct) {
        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                if (i > 20) {
                    cancel();
                    return;
                }
                ServerTickManager manager = Bukkit.getServerTickManager();
                if (manager.isFrozen() && manager.getFrozenTicksToRun() <= 0) return;

                if (i % 10 == 0) {
                    Location location = construct.getGhast().getLocation().add(0, 2, 0);
                    Vector dir = location.getDirection().setY(0).normalize().multiply(-4);
                    location.add(dir);

                    Vector vec = dir.clone().rotateAroundY(Math.PI/2).normalize().multiply(2);
                    for (int j = -2; j <= 2; j++) {
                        if (j == 0) continue;
                        Location loc = location.clone();
                        loc.add(vec.clone().multiply(j));

                        Firework flare = loc.getWorld().spawn(loc, Firework.class);
                        FireworkMeta meta = flare.getFireworkMeta();
                        meta.addEffect(FLARE_EFFECT);
                        flare.setFireworkMeta(meta);
                        flare.setShotAtAngle(true);

                        Vector travelDirection = dir.clone().normalize().multiply(2);
                        if (j == -2) travelDirection.rotateAroundY(-Math.PI/6);
                        else if (j == 2) travelDirection.rotateAroundY(Math.PI/6);

                        flare.setVelocity(travelDirection);
                        flare.detonate();

                        travelDirection.multiply(6);
                        activeFlares.add(loc.add(travelDirection));

                        new BukkitRunnable() {
                            private int i = 0;
                            @Override
                            public void run() {

                                ServerTickManager manager = Bukkit.getServerTickManager();
                                if (manager.isFrozen() && manager.getFrozenTicksToRun() <= 0) return;

                                if (i >= 20) {
                                    activeFlares.remove(loc);
                                    loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 1, 0.1, 0.1, 0.1, 0.001);
                                    cancel();
                                    return;
                                }
                                i++;
                            }
                        }.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
                    }
                }

                i++;
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
    }
}
