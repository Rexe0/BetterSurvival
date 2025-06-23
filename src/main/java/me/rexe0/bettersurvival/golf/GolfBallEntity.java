package me.rexe0.bettersurvival.golf;

import com.google.common.primitives.Doubles;
import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.golf.GolfBall;
import me.rexe0.bettersurvival.item.golf.GolfClub;
import me.rexe0.bettersurvival.item.golf.GolfCup;
import me.rexe0.bettersurvival.item.golf.Wedge;
import me.rexe0.bettersurvival.util.SkullUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GolfBallEntity {
    private static final List<GolfBallEntity> golfBalls = new ArrayList<>();
    private static final int LIFETIME = 6000; // Number of ticks the ball will exist
    private static final float SCALE = 0.5f;

    public static List<GolfBallEntity> getGolfBalls() {
        return golfBalls;
    }

    private Player owner;
    private BlockDisplay tee;
    private BlockDisplay originalTee;

    private Location location;
    private Vector velocity;
    private List<Location> previousLocations;

    private ItemDisplay display;
    private ItemDisplay camera;
    private int strokes;
    private int i;

    private boolean cameraSet;
    private boolean isGlowing;
    private boolean queuedCameraChanged;

    public GolfBallEntity(Player player, BlockDisplay tee) {
        this.owner = player;
        this.tee = tee;
        this.originalTee = tee;
        this.velocity = new Vector(0, 0, 0);
        this.previousLocations = new ArrayList<>();
        this.i = 0;
        this.strokes = 0;
    }

    public Player getOwner() {
        return owner;
    }

    public BlockDisplay getTee() {
        return tee;
    }
    public double getSpeedSquared() {
        return velocity.lengthSquared();
    }

    public void hit(GolfClub club, double progress) {
        double power = club.getPower();
        double loft = club.getLoft();

        Vector velocity = owner.getLocation().getDirection();

        double y = velocity.getY();
        if (y < -1) y = -1;
        y++;

        velocity.setY(0);
        velocity.normalize();

        y *= loft;

        velocity.setX(velocity.getX() * power);
        velocity.setZ(velocity.getZ() * power);
        velocity.setY(y);

        double multiplier = getResistanceMultiplier(location.clone().subtract(0, 0.1, 0).getBlock());
        if (multiplier < 0.7 && club instanceof Wedge) multiplier = 0.7;
        velocity.multiply(progress+1)
                .multiply(multiplier);

        this.strokes++;
        this.i = 0;
        this.location.setDirection(velocity);
        this.velocity = velocity;
        this.tee = null;

        owner.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW+"Right Click to follow the ball!"));

        owner.playSound(display.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.3f, (float) (1.2+(progress*0.2)));
        owner.playSound(display.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.25f,  (float) (1.5+(progress*0.2)));
        owner.playSound(display.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1.5f);
    }
    public void returnToLastLocation() {
        if (previousLocations.size() <= 1) return;
        if (getSpeedSquared() != 0) return;
        strokes++;
        previousLocations.removeLast();
        location = previousLocations.getLast().clone();
        if (previousLocations.size() == 1) tee = originalTee;
        velocity = new Vector(0, 0, 0);
    }

    public void setCamera(boolean set) {
        if (cameraSet == set) return;
        cameraSet = set;
        Entity entity = set ? camera : owner;

        setItemsVisible(!set);

        ClientboundSetCameraPacket packet = new ClientboundSetCameraPacket(((CraftEntity)entity).getHandle());
        ((CraftPlayer)owner).getHandle().connection.sendPacket(packet);

        if (set)
            playFlyingSound();
        else
            queuedCameraChanged = false;
    }

    private void playFlyingSound() {
        if (!cameraSet) return;
        float multiplier = (float) Math.min(velocity.length(), 2);

        owner.playSound(owner.getLocation(), Sound.ITEM_ELYTRA_FLYING, 0.5f+(multiplier/4), 1f+(multiplier/4));
    }
    private void stopFlyingSound() {
        owner.stopSound(Sound.ITEM_ELYTRA_FLYING);
    }
    public void setItemsVisible(boolean visible) {
        if (!visible)
            owner.getAttribute(Attribute.ATTACK_SPEED).addModifier(new AttributeModifier(new NamespacedKey(BetterSurvival.getInstance(), "hideItemsGolf"), -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        else {
            for (AttributeModifier modifier : owner.getAttribute(Attribute.ATTACK_SPEED).getModifiers())
                if (modifier.getKey().equals(new NamespacedKey(BetterSurvival.getInstance(), "hideItemsGolf"))) {
                    owner.getAttribute(Attribute.ATTACK_SPEED).removeModifier(modifier);
                    break;
                }

        }
    }
    private void setGlow(boolean glow) {
        if (isGlowing == glow) return;
        isGlowing = glow;

        byte glowingByte = (byte) (glow ? 0x40 : 0x00);
        List<SynchedEntityData.DataValue<?>> eData = new ArrayList<>();
        eData.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), glowingByte));
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(display.getEntityId(), eData);

        ((CraftPlayer) owner).getHandle().connection.sendPacket(packet);
    }

    public boolean canBeHit() {
        Location loc = owner.getLocation();
        loc.add(loc.getDirection().setY(0).normalize());
        return loc.distanceSquared(location) < 1 && velocity.lengthSquared() == 0;
    }
    public void run() {
        if (display.isDead() || i > LIFETIME) {
            remove();
            return;
        }

        movementStep();

        if (tee == null) {
            // If the ball is not on the ground apply gravity

            Location loc = location.clone().subtract(0, 0.1, 0);
            if (loc.getBlock().isPassable() || !loc.getBlock().getBoundingBox().contains(loc.toVector()))
                velocity.setY(velocity.getY() - 0.08);
            else {
                // Perform additional check for clean hole sinking. Don't always ray trace to increase performance
                if (loc.getBlock().getType() == Material.CAULDRON || loc.getBlock().getType().name().contains("STAIRS")) {
                    RayTraceResult hit = location.getWorld().rayTraceBlocks(location.clone(), new Vector(0, -1, 0), 0.1, FluidCollisionMode.NEVER, true);

                    if (hit == null) {
                        velocity.setY(velocity.getY() - 0.08);
                    }
                }
            }
        } else if (tee.isDead()) tee = null;

        if (velocity.lengthSquared() == 0) {
            location.setYaw(0);
            location.setPitch(0);
            if (!previousLocations.getLast().equals(location))
                previousLocations.add(location.clone());


            if (tee == null) {
                if (strokes >= 1) {
                    PersistentDataContainer data = new CustomBlockData(location.getBlock(), BetterSurvival.getInstance());

                    if (data.has(GolfCup.GOLF_CUP_KEY)) {
                        stopFlyingSound();
                        setCamera(false);
                        sink();
                        return;
                    }
                }


                Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> setGlow(true), 10);
                if (cameraSet && !queuedCameraChanged) {
                    queuedCameraChanged = true;
                    stopFlyingSound();

                    Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> setCamera(false), 60);
                    Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                        owner.playSound(camera.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                        Location loc = display.getLocation().clone();

                        for (int k = 0; k < 20; k++) {
                            loc.setY(loc.getY() + 0.5);
                            if (!loc.getBlock().isPassable()) {
                                loc.setY(loc.getY() - 0.5);
                                break;
                            }
                        }
                        loc.setDirection(display.getLocation().subtract(camera.getLocation()).toVector());
                        camera.teleport(loc);
                    }, 10);
                }
            }
        } else setGlow(false);

        i++;
    }

    private void movementStep() {
        int stepCount = 4;
        Vector vec = velocity.clone().multiply(1d / stepCount);


        for (int i = 0; i < stepCount; i++) {
            double friction = getFrictionMultiplier(location.clone().subtract(0, 0.1, 0).getBlock(), vec.getY(), false);
            friction = Math.pow(friction, 1d / stepCount);
            vec.multiply(friction);

//            if (location.getBlock().getType() == Material.WATER) {
//                ServerLevel serverLevel = ((CraftWorld)location.getWorld()).getHandle();
//
//                BlockPos.MutableBlockPos blockposition_mutableblockposition = new BlockPos.MutableBlockPos();
//                FluidState fluid = serverLevel.getFluidState(blockposition_mutableblockposition);
//                Vec3 vec3d1 = fluid.getFlow(serverLevel, blockposition_mutableblockposition);
//                Bukkit.broadcastMessage(vec3d1.toString());
//
//            }

            // Make sure the ball stops if the velocity from friction is too low
            if (vec.lengthSquared() < 0.000000001) vec.multiply(0);

            vec = collisionDetection(vec, stepCount);

            Location loc = location.clone().add(0, SCALE / 4, 0);
            display.teleport(loc);

            if (this.i < 10) {
                Vector cameraDirection = display.getLocation().subtract(camera.getLocation()).toVector();
                Location cameraLoc = camera.getLocation().setDirection(cameraDirection);
                camera.teleport(cameraLoc);
            }
            if (getSpeedSquared() > 0)
                Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                    Vector cameraDirection = display.getLocation().subtract(camera.getLocation()).toVector();
                    loc.setDirection(cameraDirection);
                    camera.teleport(loc);
                }, 10);

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
                // Set location to collision point
                Vector collisionPoint = hit.getHitPosition();
                location.setX(collisionPoint.getX());
                location.setY(collisionPoint.getY());
                location.setZ(collisionPoint.getZ());

                location.getWorld().playSound(location, hit.getHitBlock().getBlockData().getSoundGroup().getFallSound(), SoundCategory.BLOCKS, 1f, 0.8f);
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
                case BLUE_ICE -> isCollision ? 0.52f : 0.9f;
                case ICE, PACKED_ICE -> isCollision ? 0.5f : 0.8f;
                case SLIME_BLOCK -> isCollision ? 0.9f : 0.25f;
                case ACACIA_PLANKS, PALE_OAK_PLANKS, BAMBOO_PLANKS,
                        BIRCH_PLANKS, CHERRY_PLANKS, CRIMSON_PLANKS,
                        DARK_OAK_PLANKS, JUNGLE_PLANKS, MANGROVE_PLANKS,
                        OAK_PLANKS, SPRUCE_PLANKS, WARPED_PLANKS -> 0.55f;
                default -> 0.52f;
                case GRASS_BLOCK, DIRT, PODZOL,DIRT_PATH,GRAVEL -> 0.4f;
                case SNOW, SNOW_BLOCK, HAY_BLOCK -> 0.08f;
                case SAND, RED_SAND, SOUL_SAND, MUD -> 0.06f;
                case HONEY_BLOCK -> 0f;
            };
            if (isCollision) groundMultiplier *= 0.7;
            else if (contactBlock.isPassable()) groundMultiplier = 1;
        }

        return groundMultiplier * fluidMultiplier;
    }

    // Velocity multiplier when a ball is hit from somewhere
    private double getResistanceMultiplier(Block contactBlock) {
        if (tee != null) return 1.5;
        float fluidMultiplier = getFluidMultiplier(location.getBlock().getType());

        double groundMultiplier  = switch (contactBlock.getType()) {
            case GRASS_BLOCK,DIRT_PATH,GREEN_WOOL,LIME_WOOL,GREEN_CARPET,LIME_CARPET,
                    GREEN_TERRACOTTA,LIME_TERRACOTTA,GREEN_CONCRETE,LIME_CONCRETE,GREEN_CONCRETE_POWDER,LIME_CONCRETE_POWDER -> 1.2f;
            case DIRT, PODZOL,GRAVEL -> 1.05f;
            default -> 1f;
            case SNOW, SNOW_BLOCK, HAY_BLOCK -> 0.63f;
            case SLIME_BLOCK -> 0.45f;
            case SAND, RED_SAND, SOUL_SAND, MUD -> 0.21f;
            case HONEY_BLOCK -> 0.14f;
        };
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

    public void sink() {
        if (strokes == 1) {
            owner.playSound(owner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.2f);
            broadcastMessage(ChatColor.GREEN + owner.getName() + ChatColor.WHITE + " has sunk the ball in a "+ChatColor.GOLD+"hole in one!");
        } else {
            owner.playSound(owner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0f);
            broadcastMessage(ChatColor.GREEN + owner.getName() + ChatColor.WHITE + " has sunk the ball in " + ChatColor.GREEN + strokes + ChatColor.WHITE + " strokes!");
        }
        Firework firework = (Firework) location.getWorld().spawnEntity(location.clone().add(0, 4, 0), EntityType.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        if (strokes != 1)
            fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(3, 181, 0)).withFade(Color.fromRGB(72, 255, 0)).with(FireworkEffect.Type.BALL_LARGE).build());
        else
            fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(242, 157, 0)).withFlicker().with(FireworkEffect.Type.STAR).build());

        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);

        location.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location.getBlock().getLocation().add(0.5, 1, 0.5), strokes == 1 ? 500 : 200, 0, 0, 0, 1);

        remove();
    }
    private void broadcastMessage(String message) {
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) > 200*200) continue;
            player.sendMessage(ChatColor.DARK_GREEN + "[Golf] " + ChatColor.WHITE + message);
        }
    }

    public void spawn() {
        this.location = tee.getLocation().add(0, 0.25, 0);
        location.setPitch(0);
        location.setYaw(0);

        this.previousLocations.add(location.clone());

        this.display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        ItemStack item = SkullUtil.getCustomSkull(new ItemStack(Material.PLAYER_HEAD), "http://textures.minecraft.net/texture/b4936a032c688050a36d33a4c3f0d56a4a705d8a89dfdded1472438ec000c9d0");
        display.setItemStack(item);
        display.setTeleportDuration(1);

        this.camera = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        camera.setItemStack(new ItemStack(Material.AIR));
        camera.setTeleportDuration(1);

        Transformation transformation = display.getTransformation();
        transformation.getScale().set(SCALE, SCALE, SCALE);
        transformation.getTranslation().set(0, SCALE/4, 0);
        display.setTransformation(transformation);

        golfBalls.add(this);
    }


    public void remove() {
        Map<Integer, ItemStack> items = owner.getInventory().addItem(new GolfBall().getItem());
        if (!items.isEmpty()) {
            Item item = owner.getWorld().dropItemNaturally(owner.getLocation(), new GolfBall().getItem());
            item.setOwner(owner.getUniqueId());
            item.setPickupDelay(0);
        }

        display.remove();
        camera.remove();

        golfBalls.remove(this);
    }
}
