package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HappyGhast;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GhastConstruct {
    private static Map<UUID, GhastConstruct> constructMap = new HashMap<>();

    public static GhastConstruct getConstruct(UUID uuid) {
        HappyGhast ghast = (HappyGhast) Bukkit.getEntity(uuid);
        if (ghast == null) return null;
        constructMap.putIfAbsent(uuid, new GhastConstruct(ghast));
        return constructMap.get(uuid);
    }

    public static void removeConstruct(UUID uuid) {
        constructMap.remove(uuid);
    }


    private HappyGhast ghast;

    private String name;
    private Harness harness;
    private Engine engine;

    private ItemStack fuel;

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

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack fuel) {
        this.fuel = fuel;
    }

    public double getHealth() {
        double health = 20;

        health += harness.getHealth();
        health += engine.getHealth();

        return health;
    }
    public double getArmor() {
        double armor = 0;

        armor += harness.getArmor();
        armor += engine.getArmor();

        return armor;
    }
    public double getSpeed() {
        double speed = 0.05;

        double multiplier = 1;
        multiplier += harness.getSpeed();
        multiplier += engine.getSpeed();

        return speed*multiplier;
    }
    public double getAcceleration() {
        double acceleration = 0.01;

        double multiplier = 1;
        multiplier += harness.getAcceleration();
        multiplier += engine.getAcceleration();

        return acceleration*multiplier;
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

        ghast.getEquipment().setItem(EquipmentSlot.BODY, harness.getHarness());

        EntityDataUtil.setStringValue(ghast, "fuelStack", ItemDataUtil.itemStackToString(fuel));
    }
}
