package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GhastConstruct {
    private static Map<UUID, GhastConstruct> constructMap = new HashMap<>();

    public static GhastConstruct getConstruct(UUID uuid) {
        HappyGhast ghast = (HappyGhast) Bukkit.getEntity(uuid);
        if (ghast == null) return null;
        if (!constructMap.containsKey(uuid))
            constructMap.put(uuid, new GhastConstruct(ghast));
        return constructMap.get(uuid);
    }

    public static void removeConstruct(UUID uuid) {
        constructMap.remove(uuid);
    }
    public static void tick() {
        for (GhastConstruct construct : constructMap.values())
            construct.run();

    }


    private HappyGhast ghast;

    private String name;
    private Harness harness;
    private Engine engine;
    private Load load;
    private Miscellaneous miscellaneous;

    private int loadAmmo;
    private int miscellaneousAmmo;

    private ItemStack fuel;
    private boolean isOnFire;

    private ActionBarText actionBarText;

    public GhastConstruct(HappyGhast ghast) {
        this.ghast = ghast;
        this.name = ghast.getCustomName() != null ? ghast.getCustomName() : "Ghast Construct";
        this.harness = (Harness) ModificationType.HARNESS.getModification(EntityDataUtil.getIntegerValue(ghast, "harnessID"));
        Material type = ghast.getEquipment().getItem(EquipmentSlot.BODY).getType();
        if (this.harness == Harness.NONE && !type.isAir()) {
            for (Harness harness : Harness.getAllHarnesses())
                if (harness.getHarness().getType() == type) {
                    setHarness(harness);
                    break;
                }
        }
        this.engine = (Engine) ModificationType.ENGINE.getModification(EntityDataUtil.getIntegerValue(ghast, "engineID"));
        this.load = (Load) ModificationType.LOAD.getModification(EntityDataUtil.getIntegerValue(ghast, "loadID"));
        this.miscellaneous = (Miscellaneous) ModificationType.MISCELLANEOUS.getModification(EntityDataUtil.getIntegerValue(ghast, "miscellaneousID"));

        this.loadAmmo = EntityDataUtil.getIntegerValue(ghast, "loadAmmo");
        this.miscellaneousAmmo = EntityDataUtil.getIntegerValue(ghast, "miscellaneousAmmo");

        this.fuel = ItemDataUtil.stringToItemStack(EntityDataUtil.getStringValue(ghast, "fuelStack"));

        update();
    }

    public HappyGhast getGhast() {
        return ghast;
    }

    public String getName() {
        return name;
    }

    public Harness getHarness() {
        return harness;
    }

    public void setHarness(Harness harness) {
        this.harness = harness;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Load getLoad() {
        return load;
    }

    public void setLoad(Load load) {
        this.load = load;
        setLoadAmmo(load.getUses());
    }

    public Miscellaneous getMiscellaneous() {
        return miscellaneous;
    }

    public void setMiscellaneous(Miscellaneous miscellaneous) {
        this.miscellaneous = miscellaneous;
        setMiscellaneousAmmo(miscellaneous.getUses());
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack fuel) {
        this.fuel = fuel;
    }

    public int getLoadAmmo() {
        return loadAmmo;
    }

    public void setLoadAmmo(int loadAmmo) {
        this.loadAmmo = loadAmmo;
        EntityDataUtil.setIntegerValue(ghast, "loadAmmo", loadAmmo);
    }

    public int getMiscellaneousAmmo() {
        return miscellaneousAmmo;
    }

    public void setMiscellaneousAmmo(int miscellaneousAmmo) {
        this.miscellaneousAmmo = miscellaneousAmmo;
        EntityDataUtil.setIntegerValue(ghast, "miscellaneousAmmo", miscellaneousAmmo);
    }

    public boolean hasNightVision() {
        return getMiscellaneous() == Miscellaneous.NIGHT_VISION || getMiscellaneous() == Miscellaneous.ULTIMATE_FIGHTER_TECHNOLOGY;
    }
    public boolean hasCloaking() {
        return getMiscellaneous() == Miscellaneous.CLOAKING;
    }
    public boolean hasRadar() {
        return getMiscellaneous() == Miscellaneous.RADAR || getMiscellaneous() == Miscellaneous.ULTIMATE_FIGHTER_TECHNOLOGY ||
               getMiscellaneous() == Miscellaneous.SCULK_RADAR;
    }
    public boolean hasEnhancedRadar() {
        return getMiscellaneous() == Miscellaneous.SCULK_RADAR || getMiscellaneous() == Miscellaneous.ULTIMATE_FIGHTER_TECHNOLOGY;
    }
    public boolean hasAimAssistance() {
        return getMiscellaneous() == Miscellaneous.AIM_ASSISTANCE || getMiscellaneous() == Miscellaneous.ULTIMATE_FIGHTER_TECHNOLOGY;
    }

    public double getHealth() {
        double health = 20;

        health += harness.getHealth();
        health += engine.getHealth();
        health += load.getHealth();
        health += miscellaneous.getHealth();

        return Math.max(1, health);
    }
    public double getArmor() {
        double armor = 0;

        armor += harness.getArmor();
        armor += engine.getArmor();
        armor += load.getArmor();
        armor += miscellaneous.getArmor();

        return Math.max(0, armor);
    }
    public double getSpeed() {
        double speed = 0.05;

        double multiplier = 1;
        multiplier += harness.getSpeed();
        multiplier += engine.getSpeed();
        multiplier += load.getSpeed();
        multiplier += miscellaneous.getSpeed();

        return Math.max(0, speed*multiplier);
    }
    public double getAcceleration() {
        double acceleration = 0.01;

        double multiplier = 1;
        multiplier += harness.getAcceleration();
        multiplier += engine.getAcceleration();
        multiplier += load.getAcceleration();
        multiplier += miscellaneous.getAcceleration();

        return Math.max(0, acceleration*multiplier);
    }

    public List<Player> getPassengers() {
        return ghast.getPassengers().stream()
                .filter(player -> player instanceof Player)
                .map(player -> (Player) player)
                .toList();
    }

    public void sendActionBar(String message, int ticks) {
        if (actionBarText != null)
            actionBarText.cancel();

        actionBarText = new ActionBarText(message, ticks);
        actionBarText.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
    }
    public void sendTitle(String title, String subtitle, int ticks) {
        getPassengers().forEach(p -> p.sendTitle(title, subtitle, 5, ticks, 20));
    }

    public void update() {
        ghast.getAttribute(Attribute.MAX_HEALTH).setBaseValue(getHealth());
        ghast.getAttribute(Attribute.ARMOR).setBaseValue(getArmor());
        ghast.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(20);
        ghast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(getSpeed());
        ghast.setHealth(getHealth());

        EntityDataUtil.setDoubleValue(ghast, "acceleration", getAcceleration());

        EntityDataUtil.setIntegerValue(ghast, "harnessID", harness.getId());
        EntityDataUtil.setIntegerValue(ghast, "engineID", engine.getId());
        EntityDataUtil.setIntegerValue(ghast, "loadID", load.getId());
        EntityDataUtil.setIntegerValue(ghast, "miscellaneousID", miscellaneous.getId());


        ghast.getEquipment().setItem(EquipmentSlot.BODY, harness.getHarness());

        EntityDataUtil.setStringValue(ghast, "fuelStack", ItemDataUtil.itemStackToString(fuel));
    }
    public void run() {
        Location location = ghast.getEyeLocation();
        List<Player> passengers = getPassengers();

        if (hasNightVision())
            passengers.forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0, true, false)));

        if (hasCloaking())
            ghast.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, true, false));

        if (hasRadar())
            sendRadarActionBar(location, passengers, hasEnhancedRadar());

        if (!isOnFire && ghast.getFireTicks() > 0) {
            isOnFire = true;

            boolean willKill = ghast.getFireTicks() / 20 >= ghast.getHealth();
            sendTitle(ChatColor.DARK_RED+"⚠ Fire ⚠", ChatColor.RED+"Extinguish "+(willKill ? "or Eject" : "Now"), 30);
        } else if (isOnFire && ghast.getFireTicks() <= 0) {
            isOnFire = false;
            sendTitle(ChatColor.GREEN+"Fire Extinguished", "", 20);
            getPassengers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1000, 1));
        }
    }

    private void sendRadarActionBar(Location location, List<Player> passengers, boolean isEnhanced) {
        if (actionBarText != null) return;

        List<Player> players = ghast.getWorld().getPlayers();
        players.removeAll(passengers);

        // Only detect players within 250 blocks
        int range = isEnhanced ? 500*500 : 250*250;
        players.removeIf(p -> p.getLocation().distanceSquared(location) > range || !ghast.hasLineOfSight(p));

        Map<Integer, Double> activatedDegrees = new HashMap<>();
        for (Player p : players) {

            if (p.getVehicle() instanceof HappyGhast en) {
                GhastConstruct construct = GhastConstruct.getConstruct(en.getUniqueId());
                // Cloaked constructs aren't picked up by radar
                if (construct != null && construct.getMiscellaneous() == Miscellaneous.CLOAKING) continue;
            }

            Vector target = p.getLocation().subtract(location).toVector().setY(0);
            Vector dir = location.getDirection().setY(0).normalize();

            double angleDir = Math.toDegrees(Math.atan2(dir.getZ(), dir.getX()));
            double angleTarget = Math.toDegrees(Math.atan2(target.getZ(), target.getX()));

            // Difference between angles
            double angle = angleTarget - angleDir;

            // Normalize to range [-180, 180]
            angle = ((angle + 180) % 360 + 360) % 360 - 180;

            int slot = (int) Math.round(angle*0.5);
            if (isEnhanced) {
                Location pLoc = p.getLocation();
                pLoc.setY(0);
                Location loc = location.clone();
                loc.setY(0);
                double distance = pLoc.distance(loc);
                if (!activatedDegrees.containsKey(slot))
                    activatedDegrees.put(slot, distance);
                else if (activatedDegrees.get(slot) > distance)
                        activatedDegrees.put(slot, distance);

            } else {
                activatedDegrees.put(slot, 100d);
            }
        }

        int yaw = Math.round(location.getYaw());

        StringBuilder radar = new StringBuilder();
        radar.append(ChatColor.DARK_GRAY+"[");
        for (int i = -30; i < 31; i++) {
            boolean found = activatedDegrees.containsKey(i);

            ChatColor color = ChatColor.GRAY;
            if (found) {
                double distance = activatedDegrees.get(i);
                if (distance < 100) color = ChatColor.DARK_RED;
                else if (distance < 250) color = ChatColor.RED;
                else if (distance < 350) color = ChatColor.GOLD;
                else if (distance < 500) color = ChatColor.YELLOW;
            } else if (i == 0) color = ChatColor.AQUA;

            String str = switch (yaw+i) {
                case 0 -> "S";
                case 45 -> "SW";
                case 90 -> "W";
                case 135 -> "NW";
                case 180,-180 -> "N";
                case -135 -> "NE";
                case -90 -> "E";
                case -45 -> "SE";
                default -> found || i == 0 ? "*" : " ";
            };

            radar.append(color).append(str);
        }
        radar.append(ChatColor.DARK_GRAY+"]");

        passengers.forEach(p -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(radar.toString())));

    }


    private class ActionBarText extends BukkitRunnable {
        private String text;
        private int ticks;

        public ActionBarText(String text, int ticks) {
            this.text = text;
            this.ticks = ticks;
        }

        @Override
        public void run() {
            for (Player p : getPassengers())
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));

            ticks--;
            if (ticks <= 0) {
                cancel();
                actionBarText = null;
            }
        }
    }
}
