package me.rexe0.bettersurvival.smithing;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmeltingListener implements Listener {
    private static final Map<SmithingOre, NamespacedKey> ORE_KEYS = new HashMap<>();
    private static final Set<SmeltingRunnable> RUNNABLES = new HashSet<>();

    public static NamespacedKey getOreKey(SmithingOre ore) {
        return ORE_KEYS.computeIfAbsent(ore, o -> new NamespacedKey(BetterSurvival.getInstance(),"smithing_ore_"+o.name().toLowerCase()));
    }

    private static void dropCauldronItems(Block block) {
        Location blockLoc = block.getLocation();
        for (ItemDisplay display : blockLoc.getWorld().getEntitiesByClass(ItemDisplay.class)) {
            if (EntityDataUtil.hasScoreboardTag(display, "isSmithingLavaDisplay")) continue;
            String data = EntityDataUtil.getStringValue(display, "smithing_ore_display");
            if (data.equals("x:" + blockLoc.getBlockX() + "y:" + blockLoc.getBlockY() + "z:" + blockLoc.getBlockZ())) {
                display.getWorld().dropItemNaturally(display.getLocation(), display.getItemStack());
                display.remove();
            }
        }
    }
    @EventHandler
    public void onBucketCauldron(CauldronLevelChangeEvent e) {
        if (e.isCancelled()) return;
        if (e.getBlock().getType() != Material.CAULDRON || e.getNewState().getType() == Material.CAULDRON) return;

        dropCauldronItems(e.getBlock());

        PersistentDataContainer data = new CustomBlockData(e.getBlock(), BetterSurvival.getInstance());
        for (SmithingOre ore : SmithingOre.values())
            data.remove(getOreKey(ore));

        RUNNABLES.stream()
                .filter(r -> r.getBlock().equals(e.getBlock()))
                .findFirst()
                .ifPresent(SmeltingRunnable::end);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.CAULDRON) return;

        dropCauldronItems(e.getBlock());

        RUNNABLES.stream()
                .filter(r -> r.getBlock().equals(e.getBlock()))
                .findFirst()
                .ifPresent(SmeltingRunnable::end);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onAddIngredient(PlayerInteractEvent e) {
        if (e.useInteractedBlock() == Event.Result.DENY || e.useItemInHand() == Event.Result.DENY) return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;
        Block block = e.getClickedBlock();
        if (block == null || block.getType() != Material.CAULDRON) return;
        Block under = block.getLocation().subtract(0, 1, 0).getBlock();
        if (!BlockUtil.isHot(under)) return;
        if (e.getItem() == null) return;
        ItemStack item = e.getItem();

        SmithingOre ore = SmithingOre.getFromMaterial(item.getType());
        if (ore == null) return;
        // Prevent custom items
        if (ItemDataUtil.getItemType(item) != null) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        int totalAmount = 0;
        for (SmithingOre o : SmithingOre.values())
            totalAmount += data.getOrDefault(getOreKey(o), PersistentDataType.INTEGER, 0);

        Player player = e.getPlayer();
        if (totalAmount >= 10) {
            player.sendActionBar(Component.text("The Cauldron is Full!", NamedTextColor.RED));
            return;
        }

        player.swingMainHand();
        item.setAmount(item.getAmount() - 1);
        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.75f);



        int currentAmount = data.getOrDefault(getOreKey(ore), PersistentDataType.INTEGER, 0);
        currentAmount++;
        data.set(getOreKey(ore), PersistentDataType.INTEGER, currentAmount);

        addItemDisplay(block, item.getType(), totalAmount+1);

        if (totalAmount+1 < 10) return;
        // Start smelting
        int timeToSmelt = 0;
        for (SmithingOre o : SmithingOre.values()) {
            int amount = data.getOrDefault(getOreKey(o), PersistentDataType.INTEGER, 0);
            timeToSmelt += amount*o.getTime();
        }
        SmeltingRunnable runnable = new SmeltingRunnable(timeToSmelt, block);
        runnable.start();
    }

    private void addItemDisplay(Block block, Material material, int currentAmount) {
        // Display flat items in cauldron
        Location loc = block.getLocation().add(0.5, 0.2, 0.5);
        loc.add(RandomUtil.getRandom().nextDouble(-0.16, 0.16), currentAmount*0.07, RandomUtil.getRandom().nextDouble(-0.16, 0.16));
        ItemDisplay display = block.getWorld().spawn(loc, ItemDisplay.class);
        display.setItemStack(new ItemStack(material));
        display.setBrightness(new Display.Brightness(15, 15));
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);

        Transformation trans = display.getTransformation();
        trans.getLeftRotation().rotateY((float) (Math.random()*Math.PI*2)).rotateX((float) Math.toRadians(90));
        display.setTransformation(trans);

        Location blockLoc = block.getLocation();
        EntityDataUtil.setStringValue(display, "smithing_ore_display", "x:"+blockLoc.getBlockX()+"y:"+blockLoc.getBlockY()+"z:"+blockLoc.getBlockZ());
    }



    public static class SmeltingRunnable extends BukkitRunnable {
        private final Block block;
        private final int timeToSmelt;
        private int i;

        public SmeltingRunnable(int timeToSmelt, Block block) {
            this.timeToSmelt = timeToSmelt;
            this.block = block;
        }

        public Block getBlock() {
            return block;
        }

        public int getTimeToSmelt() {
            return timeToSmelt;
        }

        @Override
        public void run() {
            if (i >= timeToSmelt) {
                finish();
                return;
            }
            if (!BlockUtil.isHot(block.getRelative(0, -1, 0))) {
                dropCauldronItems(block);
                end();
                return;
            }

            if (i % (int)Math.ceil(timeToSmelt/4d) == 0 && i != 0) {
                playEffect();
                Location blockLoc = block.getLocation();

                ItemDisplay display = getDisplay();
                if (display == null)
                    display = spawnDisplay();

                int stage = i / (int)Math.ceil(timeToSmelt/4d);
                Location loc = blockLoc.clone().add(0.5, 0.3, 0.5);
                loc.setY(loc.getBlockY() + 0.3 + stage*0.15);
                display.teleport(loc);

                for (ItemDisplay itemDisplay : blockLoc.getWorld().getEntitiesByClass(ItemDisplay.class)) {
                    if (EntityDataUtil.hasScoreboardTag(itemDisplay, "isSmithingLavaDisplay")) continue;
                    String data = EntityDataUtil.getStringValue(itemDisplay, "smithing_ore_display");
                    if (data.equals("x:" + blockLoc.getBlockX() + "y:" + blockLoc.getBlockY() + "z:" + blockLoc.getBlockZ())) {
                        Location itemLoc = itemDisplay.getLocation();
                        itemLoc.setY(Math.max(blockLoc.getY()+0.2, itemLoc.getY() - 0.1));
                        itemDisplay.teleport(itemLoc);
                    }
                }

            }

            i++;
        }

        public void end() {
            Location blockLoc = block.getLocation();
            for (ItemDisplay display : blockLoc.getWorld().getEntitiesByClass(ItemDisplay.class)) {
                String data = EntityDataUtil.getStringValue(display, "smithing_ore_display");
                if (data.equals("x:"+blockLoc.getBlockX()+"y:"+blockLoc.getBlockY()+"z:"+blockLoc.getBlockZ()))
                    display.remove();

            }

            RUNNABLES.remove(this);
            cancel();
        }

        public void finish() {
            playEffect();
            block.setType(Material.LAVA_CAULDRON);

            end();
        }
        public void start() {
            runTaskTimer(BetterSurvival.getInstance(), 0, 20);

            spawnDisplay();
            playEffect();

            RUNNABLES.add(this);
        }

        private ItemDisplay getDisplay() {
            Location blockLoc = block.getLocation();
            for (ItemDisplay display : blockLoc.getWorld().getEntitiesByClass(ItemDisplay.class)) {
                if (!EntityDataUtil.hasScoreboardTag(display, "isSmithingLavaDisplay")) continue;
                String data = EntityDataUtil.getStringValue(display, "smithing_ore_display");
                if (data.equals("x:"+blockLoc.getBlockX()+"y:"+blockLoc.getBlockY()+"z:"+blockLoc.getBlockZ()))
                    return display;
            }
            return null;
        }

        private ItemDisplay spawnDisplay() {
            Location loc = block.getLocation().add(0.5, 0.3, 0.5);
            ItemDisplay display = block.getWorld().spawn(loc, ItemDisplay.class);
            ItemStack head = SkullUtil.getCustomSkull(new ItemStack(Material.PLAYER_HEAD), "http://textures.minecraft.net/texture/9ae97da35dd323d4db866a16e473b1a2b43d6206aa398430e91a2cef0caa706d");
            display.setItemStack(head);
            display.setBrightness(new Display.Brightness(15, 15));
            display.setTeleportDuration(1);

            Transformation trans = display.getTransformation();
            trans.getScale().set(1.5, 0.05, 1.5);
            display.setTransformation(trans);


            Location blockLoc = block.getLocation();
            display.addScoreboardTag("isSmithingLavaDisplay");
            EntityDataUtil.setStringValue(display, "smithing_ore_display", "x:"+blockLoc.getBlockX()+"y:"+blockLoc.getBlockY()+"z:"+blockLoc.getBlockZ());
            return display;
        }

        private void playEffect() {
            block.getWorld().playSound(block.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.4f, 1.1f);
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1f, 0.8f);
            block.getWorld().spawnParticle(Particle.LAVA, block.getLocation().add(0.5, 0.3, 0.5), 10, 0, 0, 0, 0);
        }
    }
}
