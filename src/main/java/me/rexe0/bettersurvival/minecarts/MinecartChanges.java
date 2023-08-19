package me.rexe0.bettersurvival.minecarts;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMinecartFurnace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;

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
            minecart.setMaxSpeed(Math.min(DEFAULT_SPEED*3, minecart.getMaxSpeed()+0.4)); // Minecart must first accelerate before hitting top speed
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

    @EventHandler
    public void onDispense(BlockDispenseEvent e) {
        if (e.getItem().getType() != Material.COAL && e.getItem().getType() != Material.CHARCOAL) return;
        if (e.getBlock().getType() != Material.DISPENSER) return;

        Directional directional = (Directional) e.getBlock().getBlockData();
        Block target = e.getBlock().getLocation().add(directional.getFacing().getDirection()).getBlock();

        Optional<Entity> furnace = target.getWorld().getNearbyEntities(target.getLocation(), 1.5, 1.5, 1.5).stream()
                .filter(en -> en.getType() == EntityType.MINECART_FURNACE)
                .findFirst();

        if (furnace.isEmpty()) return;

        e.setCancelled(true);

        MinecartFurnace minecart = ((CraftMinecartFurnace) furnace.get()).getHandle();
        float yaw = minecart.getBukkitYaw();
        if (yaw >= -45 && yaw < 45)
            minecart.zPush = 1;
        else if (yaw >= 45 && yaw < 135)
            minecart.xPush = -1;
        else if (yaw >= 135 || yaw < -135)
            minecart.zPush = -1;
        else if (yaw >= -135)
            minecart.xPush = 1;

        minecart.fuel += Math.min(3600, 32767);

        Dispenser dispenser = (Dispenser) e.getBlock().getState();
        for (ItemStack item : dispenser.getInventory()) {
            if (item == null) continue;
            if (item.getType() != e.getItem().getType()) continue;
            item.setAmount(item.getAmount() - 1);
            break;
        }
    }
}
