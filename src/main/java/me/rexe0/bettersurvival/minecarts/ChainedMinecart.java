package me.rexe0.bettersurvival.minecarts;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChainedMinecart implements Listener {
    private static final Particle.DustOptions COLOR = new Particle.DustOptions(Color.fromRGB(84, 84, 84), 0.8f);

    public static void run() {
        for (World world : Bukkit.getWorlds())
            for (Minecart minecart : world.getEntitiesByClass(Minecart.class)) {
                if (minecart.getType() == EntityType.MINECART_FURNACE) {
                    Chunk chunk = minecart.getLocation().getChunk();
                    boolean successful = chunk.addPluginChunkTicket(BetterSurvival.getInstance());

                    if (successful)
                        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                            if (minecart.getLocation().getChunk().equals(chunk)) return;
                            chunk.removePluginChunkTicket(BetterSurvival.getInstance());
                        }, 1);
                }

                setChildSpeed(minecart);
            }
    }

    private static Minecart setChildSpeed(Minecart minecart) {

        String uuid = EntityDataUtil.getStringValue(minecart, "childMinecart");
        if (uuid.equals("")) return null;
        Minecart childCart = (Minecart) Bukkit.getEntity(UUID.fromString(uuid));

        // Unlink the carts if they are too far away from each other or the child cart is non-existent
        if (childCart == null || childCart.isDead() || childCart.getLocation().distanceSquared(minecart.getLocation()) > 16) {
            EntityDataUtil.removeStringValue(minecart, "childMinecart");
            minecart.getWorld().dropItemNaturally(minecart.getLocation(), new ItemStack(Material.CHAIN, 2));
            minecart.getWorld().playSound(minecart.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1, 1.4f);
            return null;
        }

        if (!childCart.getLocation().getBlock().getType().toString().contains("RAIL")) return null;

        // Particle Animation

        // Find both ends of the minecart
        Vector direction = childCart.getLocation().subtract(minecart.getLocation()).toVector().normalize().multiply(0.25);

        Location origin = minecart.getLocation().add(direction).add(0, 0.3, 0);
        Location target = childCart.getLocation().add(0, 0.3, 0);

        // Create particle effect, with a particle dust every 0.25 blocks
        direction.normalize().multiply(0.25);
        for (double i = 0; i < origin.distance(target); i += 0.25) {
            origin.add(direction);
            origin.getWorld().spawnParticle(Particle.REDSTONE, origin, 1, 0, 0, 0, 0, COLOR);
        }




        childCart.setMaxSpeed(minecart.getMaxSpeed());

        if (minecart.getLocation().getYaw() != childCart.getLocation().getYaw()) {
            if (childCart.getVelocity().length() == 0) return null;
            Vector velocity = childCart.getVelocity().normalize().setY(0);
            double magnitude = minecart.getVelocity().length();
            childCart.setVelocity(velocity.multiply(magnitude));
            return null;
        }

        childCart.setVelocity(minecart.getVelocity().setY(0));
        return childCart;
    }

    private final Map<UUID, UUID> currentMinecartChain = new HashMap<>();

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getPlayer().getEquipment().getItemInMainHand();
        if (item.getType() != Material.CHAIN) return;
        if (!(e.getRightClicked() instanceof Minecart minecart)) return;

        Player player = e.getPlayer();

        e.setCancelled(true);

        if (currentMinecartChain.containsKey(player.getUniqueId())) {
            Minecart cart = (Minecart) Bukkit.getEntity(currentMinecartChain.get(player.getUniqueId()));

            if (cart == null || cart.isDead() || !cart.getWorld().equals(minecart.getWorld())
                    || cart.getLocation().distanceSquared(minecart.getLocation()) > 4) {
                currentMinecartChain.put(player.getUniqueId(), minecart.getUniqueId());
                player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1, 0.6f);
                item.setAmount(item.getAmount()-1);
                return;
            }
            if (cart.equals(minecart)) return;
            if (EntityDataUtil.getStringValue(minecart, "childMinecart").equals(cart.getUniqueId().toString())) return;


            item.setAmount(item.getAmount()-1);
            EntityDataUtil.setStringValue(cart, "childMinecart", minecart.getUniqueId().toString());
            player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1, 1.4f);
            currentMinecartChain.remove(player.getUniqueId());
            return;
        }
        currentMinecartChain.put(player.getUniqueId(), minecart.getUniqueId());
        player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1, 0.6f);
        item.setAmount(item.getAmount()-1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // Prevent memory leaks
        currentMinecartChain.remove(e.getPlayer().getUniqueId());
    }
}
