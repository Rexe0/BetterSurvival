package me.rexe0.bettersurvival.basketball;

import com.google.common.primitives.Doubles;
import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.basketball.Basketball;
import me.rexe0.bettersurvival.item.basketball.BasketballHoop;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasketballEntity {
    private static final List<BasketballEntity> basketballs = new ArrayList<>();
    private static final int LIFETIME = 6000; // Number of ticks the ball will exist
    private static final ItemStack silktouch;
    public static final float SCALE = 0.8f;

    public static List<BasketballEntity> getBasketballs() {
        return basketballs;
    }

    static {
        silktouch = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = silktouch.getItemMeta();
        meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        silktouch.setItemMeta(meta);
    }

    private Player owner;

    private Location location;
    private Vector velocity;

    private ItemDisplay display;
    private int i;

    private boolean isDribbling;
    private boolean isThrown;
    private boolean isDunk;
    private int bounces;


    public BasketballEntity(Player player) {
        this.owner = player;
        this.velocity = new Vector(0, 0, 0);
        this.i = 0;
    }

    public Player getOwner() {
        return owner;
    }

    public ItemDisplay getDisplay() {
        return display;
    }

    public boolean isDribbling() {
        return isDribbling;
    }

    public boolean canLeftClick() {
        return !(isThrown && i < 10);
    }

    public double getSpeedSquared() {
        return velocity.lengthSquared();
    }

    private void playSound(Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void onLeftClick(Player player) {
        if (!player.getEquipment().getItemInMainHand().getType().isAir()) return;
        if (isDribbling && player.equals(owner)) return;

        double distance = player.getEyeLocation().distance(location);

        // Grab ball
        if (distance <= (velocity.lengthSquared() == 0 ? 3 : 2)) {
            BasketballListener.getInstance().setDribbleCooldown(player, 5);
            playSound(Sound.ENTITY_ITEM_PICKUP, 1, 1);
            remove(false);
            player.getEquipment().setItemInMainHand(new Basketball().getItem());
            return;
        }
        if (distance <= 4) {
            hit(player, distance);
        }
    }


    public void hit(Player player, double distance) {
        Vector dir = location.clone().subtract(player.getEyeLocation()).toVector().normalize();
        dir.multiply(1/distance);
        if (!location.clone().subtract(0, 0.1, 0).getBlock().isPassable()) dir.setY(0.1);

        this.velocity = dir;
        this.location.setDirection(velocity);
    }

    public void run() {
        if (display.isDead() || i > LIFETIME) {
            remove();
            return;
        }
        if (isDribbling && bounces > 0 && location.getY()-owner.getLocation().getY() > 1) {
            remove();
            return;
        }
        if (isDribbling && (!owner.isOnGround() || bounces > 1)) {
            isDribbling = false;
        }

        movementStep();

        // If the ball is not on the ground apply gravity
        Location loc = location.clone().subtract(0, 0.1, 0);
        if (loc.getBlock().isPassable() || !loc.getBlock().getBoundingBox().contains(loc.toVector()))
            velocity.setY(velocity.getY() - 0.08);
        else {
            // Perform additional check for clean hooping. Don't always ray trace to increase performance
            if (loc.getBlock().getType() == Material.CAULDRON || loc.getBlock().getType().name().contains("STAIRS")) {
                RayTraceResult hit = location.getWorld().rayTraceBlocks(location.clone(), new Vector(0, -1, 0), 0.1, FluidCollisionMode.NEVER, true);

                if (hit == null) {
                    velocity.setY(velocity.getY() - 0.08);
                }
            }
        }
        if (velocity.lengthSquared() == 0) {

            PersistentDataContainer data = new CustomBlockData(location.getBlock(), BetterSurvival.getInstance());

            if (data.has(BasketballHoop.BASKETBALL_HOOP_KEY)) {
                score();
                return;
            }
        }
        i++;
    }
    public void score() {
        playSound(Sound.ENTITY_PLAYER_LEVELUP, 2, 0);

        Firework firework = (Firework) location.getWorld().spawnEntity(location.clone().add(0, 1, 0), EntityType.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(242, 157, 0)).withFlicker().with(FireworkEffect.Type.BURST).build());
        firework.setFireworkMeta(fireworkMeta);
        firework.addScoreboardTag("noDamage");
        firework.detonate();

        location.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location.getBlock().getLocation().add(0.5, 1, 0.5), 200, 0, 0, 0, 1);

        String message = ChatColor.GREEN + owner.getName() + ChatColor.WHITE + " has scored a basket!";
        if (isDunk) message += ChatColor.GOLD+" "+ChatColor.BOLD+"DUNK!";
        broadcastMessage(message);

        // Make ball pass through hoop
        location.setY(location.getBlockY()-0.3);
        // If no air under hoop, just remove
        if (!location.getBlock().isPassable()) remove();
    }
    private void broadcastMessage(String message) {
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) > 200*200) continue;
            player.sendMessage(ChatColor.GOLD + "[Basketball] " + ChatColor.WHITE + message);
        }
    }

    private void movementStep() {
        int stepCount = 4;
        Vector vec = velocity.clone().multiply(1d / stepCount);


        for (int i = 0; i < stepCount; i++) {
            double friction = getFrictionMultiplier(location.clone().subtract(0, 0.1, 0).getBlock(), vec.getY(), false);
            friction = Math.pow(friction, 1d / stepCount);
            vec.multiply(friction);

            // Make sure the ball stops if the velocity from friction is too low
            if (vec.lengthSquared() < 0.000000001) vec.multiply(0);

            vec = collisionDetection(vec, stepCount);

            Location loc = location.clone().add(0, SCALE / 4, 0);
            display.teleport(loc);

        }
        velocity = vec.multiply(stepCount);
    }

    private Vector collisionDetection(Vector vec, int stepCount) {
        if (vec.lengthSquared() > 0) {
            double maxDistance = vec.length();
            RayTraceResult hit = display.getWorld().rayTraceBlocks(location.clone(), vec, maxDistance, FluidCollisionMode.NEVER, true);

            if (hit == null) {
                // No collision
                location.add(vec);
            } else {
                bounces++;

                // Set location to collision point
                Vector collisionPoint = hit.getHitPosition();
                location.setX(collisionPoint.getX());
                location.setY(collisionPoint.getY());
                location.setZ(collisionPoint.getZ());

                SoundGroup group = hit.getHitBlock().getBlockData().getSoundGroup();
                playSound(isDribbling ? group.getBreakSound() : group.getFallSound(), 1f, 0.8f);
                location.getWorld().spawnParticle(Particle.BLOCK, location, 10, 0.1, 0.1, 0.1, 0, hit.getHitBlock().getBlockData());

                // Get the normal vector to the block face
                Vector normal = new Vector(
                        hit.getHitBlockFace().getModX(),
                        hit.getHitBlockFace().getModY(),
                        hit.getHitBlockFace().getModZ()
                );

                // r = d - 2(d.n)n
                Vector v = vec.clone().subtract(normal.clone().multiply(2 * vec.dot(normal)));
                double length = vec.lengthSquared();

                // Apply bounce multiplier.   The length < Math.pow(0.28285 / stepCount, 2) part is to make sure the ball doesn't keep bouncing and starts 'rolling'
                double multiplier = length < Math.pow(0.28285 / stepCount, 2) ? 0 : getFrictionMultiplier(hit.getHitBlock(), 0, true);

                Vector xAxis = new Vector(1, 0, 0);
                Vector yAxis = new Vector(0, 1, 0);
                Vector zAxis = new Vector(0, 0, 1);

                // Apply multiplier based on angle of the collision. If collides with the floor, instead ignore this so that the ball rolls
                Vector forceDistribution = normal.getY() == 1 ? normal.clone() :
                        new Vector(
                        Math.abs(Doubles.constrainToRange(vec.dot(xAxis) / (vec.length() * xAxis.length()), -1.0, 1.0)),
                        Math.abs(Doubles.constrainToRange(vec.dot(yAxis) / (vec.length() * yAxis.length()), -1.0, 1.0)),
                        Math.abs(Doubles.constrainToRange(vec.dot(zAxis) / (vec.length() * zAxis.length()), -1.0, 1.0)));

                forceDistribution.multiply(1-multiplier);
                forceDistribution.setX(1-forceDistribution.getX());
                forceDistribution.setY(1-forceDistribution.getY());
                forceDistribution.setZ(1-forceDistribution.getZ());

                v.multiply(forceDistribution);

                if (v.getY() != 0) {
                    v.setX(v.getX() * 0.8);
                    v.setZ(v.getZ() * 0.8);
                }

                // Compute remaining distance in this substep
                double used = hit.getHitPosition().subtract(location.toVector()).length();
                double remain = maxDistance - used;
                if (remain > 0 && v.lengthSquared() > 0) {
                    // step out along the reflected vector, scaled to `remain`
                    // Run additional collision check to ensure the ball doesn't get stuck in a block if it does collide again
                    v = collisionDetection(v.multiply(remain / maxDistance), stepCount);
                }
                this.location.setDirection(v);

                return v;
            }
        }
        return vec;
    }

    // Velocity multiplier when a ball moves along a material
    private double getFrictionMultiplier(Block contactBlock, double yVelocity, boolean isCollision) {
        float fluidMultiplier = getFluidMultiplier(location.getBlock().getType());

        double groundMultiplier = 1;

        // Only apply ground friction when the ball has low y-velocity (not bouncing/flying)
        if (Math.abs(yVelocity) < 0.01) {
            groundMultiplier = switch (contactBlock.getType()) {
                case BLUE_ICE -> isCollision ? 0.92f : 0.97f;
                case ICE, PACKED_ICE -> isCollision ? 0.9f : 0.95f;
                case SLIME_BLOCK -> isCollision ? 0.99f : 0.25f;
                default -> 0.93f;
                case GRASS_BLOCK, DIRT, PODZOL,DIRT_PATH,GRAVEL -> 0.91f;
                case SNOW, SNOW_BLOCK, HAY_BLOCK -> 0.8f;
                case SAND, RED_SAND, SOUL_SAND, MUD -> 0.75f;
                case HONEY_BLOCK -> 0f;
            };
            if (isCollision) groundMultiplier *= 0.85;
            else if (contactBlock.isPassable()) groundMultiplier = 1;
        }

        return groundMultiplier * fluidMultiplier;
    }

    private float getFluidMultiplier(Material fluid) {
        return switch (fluid) {
            case TALL_GRASS,LARGE_FERN, ROSE_BUSH, PEONY, FIREFLY_BUSH,
                    BUSH, PITCHER_PLANT -> 0.8f;
            case WATER, LAVA,VINE,TWISTING_VINES,CAVE_VINES,CAVE_VINES_PLANT,WEEPING_VINES_PLANT,WEEPING_VINES,TWISTING_VINES_PLANT -> 0.6f;
            case POWDER_SNOW, COBWEB -> 0.4f;
            default -> 0.99f;
        };
    }

    public void dribbleBall() {
        Vector dir = owner.getLocation().getDirection();
        dir.setY(0);
        dir.normalize().multiply(0.7);

        this.location = owner.getLocation().add(0, 1.4, 0).add(dir);
        location.setPitch(-90);
        location.setYaw(0);

        Vector velocity = BasketballListener.getInstance().getPlayerVelocity(owner);
        velocity.setY(-0.4);

        this.velocity = velocity;

        spawnDisplay();

        isDribbling = true;

        basketballs.add(this);
    }
    public void throwBall() {
        Vector dir = owner.getLocation().getDirection();
        dir.normalize().multiply(0.7);

        this.location = owner.getLocation().add(0, 1.4, 0).add(dir);
        location.setPitch(-90);
        location.setYaw(0);

        dir.setY(dir.getY()*1.8);
        this.velocity = dir;

        spawnDisplay();

        isThrown = true;

        basketballs.add(this);

        playSound(Sound.ITEM_TRIDENT_THROW, 0.9f, 0.75f);
        playSound(Sound.ENTITY_BREEZE_SHOOT, 0.15f, 1.15f);
    }

    public void dunkBall(Block hoop) {
        this.location = hoop.getLocation().add(0.5, 0.7, 0.5);
        location.setPitch(-90);
        location.setYaw(0);

        this.velocity = new Vector(0, 0, 0);

        spawnDisplay();

        isThrown = true;
        isDunk = true;

        basketballs.add(this);

        playSound(Sound.ENTITY_IRON_GOLEM_HURT, 0.3f, 1.1f);
        playSound(Sound.ENTITY_BLAZE_HURT, 0.7f, 0.8f);
        playSound(Sound.ITEM_TRIDENT_THUNDER, 0.2f, 1.1f);

        long playersNearby = location.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(location) <= 2500)
                .count();
        if (playersNearby < 2 || RandomUtil.getRandom().nextInt(100) != 0) return;
        // 1 in 100 to shatter glass backboard
        Location loc = hoop.getLocation();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    loc.add(x, y, z);
                    if (loc.getBlock().getType().name().contains("GLASS"))
                        loc.getBlock().breakNaturally(silktouch, true);
                    loc.subtract(x, y, z);
                }
            }
        }
    }

    private void spawnDisplay() {
        this.display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        ItemStack item = SkullUtil.getCustomSkull(new ItemStack(Material.PLAYER_HEAD), "http://textures.minecraft.net/texture/8c240881b12729a51525517d2dd074e370006028fc044a030189cb1de08e2805");
        display.setItemStack(item);
        display.setTeleportDuration(1);
        display.addScoreboardTag("basketballDisplay");

        Transformation transformation = display.getTransformation();
        transformation.getScale().set(SCALE, SCALE, SCALE);
        transformation.getTranslation().set(0, SCALE/4, 0);
        display.setTransformation(transformation);
    }


    public void remove() {
        remove(true);
    }
    public void remove(boolean refund) {
        if (refund) {
            Map<Integer, ItemStack> items = owner.getInventory().addItem(new Basketball().getItem());
            if (!items.isEmpty()) {
                Item item = owner.getWorld().dropItemNaturally(owner.getLocation(), new Basketball().getItem());
                item.setOwner(owner.getUniqueId());
                item.setPickupDelay(0);
            }
        }

        display.remove();

        basketballs.remove(this);
    }
}
