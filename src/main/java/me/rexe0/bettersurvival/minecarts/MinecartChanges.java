package me.rexe0.bettersurvival.minecarts;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMinecartFurnace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class MinecartChanges implements Listener {
    private static final double DEFAULT_SPEED = 0.4d;

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart minecart)) return;
        Location location = minecart.getLocation();

        Block rail = location.getBlock();
        Block below = location.subtract(0, 1, 0).getBlock();

        if (rail.getType() != Material.POWERED_RAIL) return;
        // Copper blocks boost max speed
        if (below.getType() == Material.COPPER_BLOCK || below.getType() == Material.WAXED_COPPER_BLOCK)
            minecart.setMaxSpeed(Math.min(DEFAULT_SPEED*3, minecart.getMaxSpeed()+0.1)); // Minecart must first accelerate before hitting top speed
        else
            minecart.setMaxSpeed(DEFAULT_SPEED);

        // Obsidian quickly reduces current velocity and stop furnace minecarts
        RedstoneRail redstoneRail = (RedstoneRail) rail.getBlockData();
        if (!redstoneRail.isPowered() && below.getType() == Material.CHISELED_DEEPSLATE) {
            minecart.setVelocity(minecart.getVelocity().multiply(0.15));
            if (minecart instanceof CraftMinecartFurnace minecartFurnace) minecartFurnace.setFuel(0);
        }
    }

    @EventHandler
    public void onSpawn(VehicleCreateEvent e) {
        if (!(e.getVehicle() instanceof Minecart minecart)) return;
        minecart.setFlyingVelocityMod(new Vector(1.4, 0.95, 1.4));
        if (minecart.getType() != EntityType.MINECART_FURNACE) return;
        if (EntityDataUtil.getStringValue(minecart, "isCustomMinecartFurnace").equals("true")) return;
        // Run 1 tick later to prevent ghost item from spawning
        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            minecart.remove();

            CustomMinecartFurnace cart = new CustomMinecartFurnace(minecart.getLocation());

            EntityDataUtil.setStringValue(cart.getBukkitEntity(), "isCustomMinecartFurnace", "true");
            ((CraftWorld) minecart.getWorld()).getHandle().addFreshEntity(cart);
        }, 1);
    }
}
