package me.rexe0.bettersurvival.basketball;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.basketball.BasketballHoop;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class BasketballListener implements Listener {
    private static BasketballListener instance;
    public static BasketballListener getInstance() {
        if (instance == null) instance = new BasketballListener();
        return instance;
    }

    private final Map<UUID, Integer> jumpCharge = new HashMap<>();
    private final Map<UUID, Integer> dribbleCooldown = new HashMap<>();
    private final Map<UUID, List<Location>> previousLocations = new HashMap<>();

    public void setDribbleCooldown(Player player, int cd) {
        dribbleCooldown.put(player.getUniqueId(), cd);
    }

    public Vector getPlayerVelocity(Player player) {
        List<Location> prevLocs = previousLocations.get(player.getUniqueId());
        if (prevLocs.size() < 2) return new Vector();
        if (getVectorDelta(player, 0).lengthSquared() == 0) return new Vector();
        Vector vec = new Vector();

        int n = prevLocs.size()-1;
        for (int i = 0; i < n; i++)
            vec.add(getVectorDelta(player, i));

        vec.divide(new Vector(n, n, n));
        return vec;
    }
    private Vector getVectorDelta(Player player, int index) {
        List<Location> prevLocs = previousLocations.get(player.getUniqueId());
        if (prevLocs.size() <= index+1) return new Vector();

        Location end = prevLocs.get(index);
        Location start = prevLocs.get(index+1);

        if (!end.getWorld().equals(start.getWorld())) return new Vector();
        return end.clone().subtract(start).toVector().multiply(0.25);
    }

    @EventHandler
    public void onDamageByFirework(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework firework && firework.getScoreboardTags().contains("noDamage")) e.setCancelled(true);
        if (e.getEntity() instanceof Player player && e.getDamager() instanceof Player p) {
            if (hasBall(player) && p.getEquipment().getItemInMainHand().getType().isAir()) {

                if (player.isOnGround() && !player.isSneaking()) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (dunkCheck(e)) return;

        if (e.getAction().isLeftClick()) onLeftClick(e.getPlayer());
        if (e.getAction().isRightClick()) onRightClick(e.getPlayer());
    }
    // Returns whether successful
    private boolean dunkCheck(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getBlockFace() != BlockFace.UP) return false;

        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.CAULDRON) return false;
        PersistentDataContainer data = new CustomBlockData(e.getClickedBlock(), BetterSurvival.getInstance());

        if (!data.has(BasketballHoop.BASKETBALL_HOOP_KEY)) return false;
        Player player = e.getPlayer();

        ItemStack item = player.getEquipment().getItemInMainHand();
        if (!ItemDataUtil.isItem(item, ItemType.BASKETBALL.getItem().getID())) return false;

        item.setAmount(item.getAmount()-1);

        BasketballEntity basketball = createBasketball(player);
        basketball.dunkBall(e.getClickedBlock());
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        previousLocations.put(e.getPlayer().getUniqueId(), new ArrayList<>());
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        jumpCharge.remove(player.getUniqueId());
        previousLocations.remove(player.getUniqueId());
        BasketballEntity basketball = getBasketball(player);
        if (basketball != null)
            basketball.remove();
    }

    private void onLeftClick(Player player) {
        // We call player.swingMainHand() from throwing, this prevents any funny business from happening
        BasketballEntity ball = getBasketball(player);
        if (ball != null && !ball.canLeftClick()) return;

        // Dribble ball
        ItemStack item = player.getEquipment().getItemInMainHand();
        if (ItemDataUtil.isItem(item, ItemType.BASKETBALL.getItem().getID()) && !dribbleCooldown.containsKey(player.getUniqueId())) {
            item.setAmount(item.getAmount()-1);

            BasketballEntity basketball = createBasketball(player);
            basketball.dribbleBall();
            return;
        }

        // Hitting ball entity
        if (BasketballEntity.getBasketballs().isEmpty()) return;

        RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 4, BasketballEntity.SCALE, e -> !e.equals(player));
        if (result == null) return;
        if (result.getHitEntity() == null) return;
        if (!(result.getHitEntity() instanceof ItemDisplay display) || !display.getScoreboardTags().contains("basketballDisplay")) return;
        ball = null;

        for (BasketballEntity basketball : BasketballEntity.getBasketballs())
            if (basketball.getDisplay().equals(display)) {
                ball = basketball;
                break;
            }
        if (ball == null) return;
        ball.onLeftClick(player);
    }
    private void onRightClick(Player player) {
        // Throw ball
        ItemStack item = player.getEquipment().getItemInMainHand();
        if (!ItemDataUtil.isItem(item, ItemType.BASKETBALL.getItem().getID())) return;
        item.setAmount(item.getAmount()-1);

        BasketballEntity basketball = createBasketball(player);
        player.swingMainHand();
        basketball.throwBall();
    }
    public BasketballEntity createBasketball(Player player) {
        for (BasketballEntity basketball : BasketballEntity.getBasketballs().toArray(new BasketballEntity[0]))
            if (basketball.getOwner().equals(player)) basketball.remove();

        return new BasketballEntity(player);
    }


    public BasketballEntity getBasketball(Player player) {
        for (BasketballEntity basketball : BasketballEntity.getBasketballs())
            if (basketball.getOwner().equals(player))
                return basketball;
        return null;
    }


    private int i;
    public void run() {
        i++;
        for (BasketballEntity basketball : BasketballEntity.getBasketballs().toArray(new BasketballEntity[0]))
            basketball.run();

        if (i % 4 == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<Location> prevLocs = previousLocations.get(player.getUniqueId());
                prevLocs.addFirst(player.getLocation());
                if (prevLocs.size() > 6) prevLocs.removeLast();
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (dribbleCooldown.containsKey(uuid)) {
                dribbleCooldown.put(uuid, dribbleCooldown.get(uuid)-1);
                if (dribbleCooldown.get(uuid) <= 0) dribbleCooldown.remove(uuid);
            }
            // Marker for who has the ball - player can't see their own marker
            if (hasBall(player))
                player.getWorld().getPlayers().stream()
                        .filter(p -> !p.equals(player))
                        .filter(p -> p.getLocation().distanceSquared(player.getLocation()) < 2500)
                        .forEach(p -> p.spawnParticle(Particle.ENTITY_EFFECT, player.getEyeLocation().add(0, 1, 0), 1, 0, 0, 0, 0, Color.fromRGB(173, 36, 36)));

            if (!ItemDataUtil.isItem(player.getEquipment().getBoots(), ItemType.AIR_JORDANS.getItem().getID())) continue;

            if (!nearGround(player)) {
                jumpCharge.remove(uuid);
                return;
            }

            int charge = jumpCharge.getOrDefault(uuid, 0);
            if (player.isSneaking()) {
                if (charge < 15) {
                    charge++;
                    if (charge % 2 == 0)
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.6f, 0.8f + (charge / 15f));

                    jumpCharge.put(uuid, charge);
                }
                continue;
            }

            jumpCharge.remove(uuid);
            if (charge < 6) continue;

            // Super jump
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 0.35f, 1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 0.35f, 0.8f);

            Vector velocity = player.getVelocity();
            velocity.setY(charge/15f);
            player.setVelocity(velocity);
        }
    }

    // Check if player is close enough to ground (one block max above)
    private boolean nearGround(Player player) {
        Location loc = player.getLocation();
        return loc.getY() - loc.getWorld().getHighestBlockYAt(loc) <= 2.3;
    }

    private boolean hasBall(Player player) {
        BasketballEntity ball = getBasketball(player);
        return (ball != null && ball.isDribbling()) || ItemDataUtil.isItem(player.getEquipment().getItemInMainHand(), ItemType.BASKETBALL.getItem().getID());
    }
}
