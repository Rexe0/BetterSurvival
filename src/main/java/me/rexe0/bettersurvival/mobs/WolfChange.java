package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.pathfinder.PathType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftWolf;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class WolfChange implements Listener {
    @EventHandler
    public void onFeedDog(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!(e.getRightClicked() instanceof Wolf wolf)) return;
        if (!wolf.isTamed() || !wolf.isAdult()) return;
        Player player = e.getPlayer();

        if (!wolf.getOwner().equals(player)) return;
        ItemStack food = player.getEquipment().getItemInMainHand();

        if (!wolf.isBreedItem(food)) return;
        ItemStack item = player.getEquipment().getItemInOffHand();
        ItemType type = ItemDataUtil.getItemType(item);

        if (type == null && item.getType().getMaxDurability() > 0) return;

        // Consume food even if they didn't heal the dog
        if (wolf.getHealth() >= wolf.getAttribute(Attribute.MAX_HEALTH).getValue())
            food.setAmount(food.getAmount()-1);

        if (!player.getScoreboardTags().contains("hasTrainedDog")) {
            player.addScoreboardTag("hasTrainedDog");
            player.sendMessage(ChatColor.GREEN+"You have trained your dog to the scent of the item in your offhand. If any nearby players have that item in their inventory, the dog will follow and bark at them.");
        }

        player.playSound(wolf.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1, 0.8f);
        player.spawnParticle(Particle.HAPPY_VILLAGER, wolf.getEyeLocation(), 10, 0.2, 0.2, 0.2, 0);
        EntityDataUtil.setStringValue(wolf, "wolfTrainedItem", type == null ? item.getType().name() : type.name());
    }

    public static void startRunnable() {
        Bukkit.getScheduler().runTaskTimer(BetterSurvival.getInstance(), () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Wolf wolf : world.getEntitiesByClass(Wolf.class)) {
                    net.minecraft.world.entity.animal.wolf.Wolf nmsWolf = ((CraftWolf)wolf).getHandle();
                    if (nmsWolf.goalSelector.getAvailableGoals().size() == 12)
                        nmsWolf.goalSelector.addGoal(6, new FollowItemScent(nmsWolf, 1.0, 10, 2.0f));
                }
            }
        }, 20, 100);

    }


    public static class FollowItemScent extends Goal {
        private final TamableAnimal tamable;
        @Nullable
        private net.minecraft.world.entity.player.Player target;
        @Nullable
        private LivingEntity owner;

        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float range;
        private float oldWaterCost;

        public FollowItemScent(TamableAnimal tamable, double speedModifier, float range, float stopDistance) {
            this.tamable = tamable;
            this.speedModifier = speedModifier;
            this.navigation = tamable.getNavigation();
            this.range = range;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(tamable.getNavigation() instanceof GroundPathNavigation) && !(tamable.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public TargetingConditions getTargetingConditions(LivingEntity owner) {
            return TargetingConditions.forNonCombat().range(this.range)
                    .selector((entity, serverLevel) -> entity instanceof net.minecraft.world.entity.player.Player player && !player.equals(owner) && hasItem(player, EntityDataUtil.getStringValue(tamable.getBukkitEntity(), "wolfTrainedItem")));
        }

        private boolean hasItem(net.minecraft.world.entity.player.Player player, String id) {
            if (id == null || id.isEmpty() || id.equals("AIR")) return false;

            ItemType type = null;
            try {
                type = ItemType.valueOf(id);
            } catch (IllegalArgumentException ignored) {}

            ItemStack item;
            for (net.minecraft.world.item.ItemStack nmsStack : player.getInventory().getContents()) {
                item = CraftItemStack.asBukkitCopy(nmsStack);
                if ((type == null && item.getType().name().equals(id)) || (type != null && ItemDataUtil.isItem(item, type.name())))
                    return true;
                if (item.getType() == Material.SHULKER_BOX) {
                    for (ItemStack shulkerItem : ((ShulkerBox)((BlockStateMeta)item.getItemMeta()).getBlockState()).getInventory().getContents()) {
                        if (shulkerItem == null) continue;
                        if ((type == null && shulkerItem.getType().name().equals(id)) || (type != null && ItemDataUtil.isItem(shulkerItem, type.name())))
                            return true;
                    }
                }
                if (item.getType() == Material.BUNDLE) {
                    BundleMeta meta = ((BundleMeta)item.getItemMeta());
                    if (!meta.hasItems()) continue;
                    for (ItemStack bundleItem : meta.getItems()) {
                        if (bundleItem == null) continue;
                        if ((type == null && bundleItem.getType().name().equals(id)) || (type != null && ItemDataUtil.isItem(bundleItem, type.name())))
                            return true;
                    }
                }
            }

            return false;
        }
        private String getItemID() {
            return EntityDataUtil.getStringValue(tamable.getBukkitEntity(), "wolfTrainedItem");
        }

        public boolean canUse() {
            LivingEntity var0 = this.tamable.getOwner();
            if (var0 == null) return false;
            if (tamable.isOrderedToSit() || tamable.isPassenger() || tamable.mayBeLeashed()) return false;

            if (!(tamable.level() instanceof ServerLevel serverLevel)) return false;
            net.minecraft.world.entity.player.Player player = serverLevel.getNearestPlayer(getTargetingConditions(var0), this.tamable);

            if (player == null || player.distanceToSqr(this.tamable) > (this.range * this.range)) {
                this.target = null;
                return false;
            }
            this.target = player;
            this.owner = var0;
            return true;
        }

        public boolean canContinueToUse() {
            if (tamable.isOrderedToSit() || tamable.isPassenger() || tamable.mayBeLeashed()) return false;

            if (target == null || tamable.getServer() == null || tamable.getServer().server.getPlayer(target.getUUID()) == null
                    || !target.isAlive() || target.distanceToSqr(tamable) > (this.range * this.range)
                    || (owner != null && owner.distanceToSqr(tamable) > (this.range * this.range))) {
                this.target = null;
                return false;
            }

            return hasItem(target, getItemID());
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tamable.getPathfindingMalus(PathType.WATER);
            this.tamable.setPathfindingMalus(PathType.WATER, 0.0F);
        }

        public void stop() {
            this.target = null;
            this.owner = null;
            this.navigation.stop();
            this.tamable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.tamable.getLookControl().setLookAt(this.target, 10.0F, (float)this.tamable.getMaxHeadXRot());

            if (--this.timeToRecalcPath <= 0) {
                if (this.tamable.distanceToSqr(this.target) <= this.stopDistance*this.stopDistance)
                    playEffect();

                this.timeToRecalcPath = this.adjustedTickDelay(10);
                this.navigation.moveTo(this.target, this.speedModifier);
            }
        }
        private void playEffect() {
            Wolf wolf = (CraftWolf)this.tamable.getBukkitEntity();
            wolf.getWorld().playSound(wolf.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1, 0.8f);
            wolf.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, wolf.getEyeLocation(), 1, 0, 0, 0, 0);
        }
    }
}
