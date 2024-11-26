package me.rexe0.bettersurvival.item;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DrillEntity {
    public static final NamespacedKey key = new NamespacedKey(BetterSurvival.getInstance(), "IS_DRILL");
    private static List<DrillEntity> drillList = new ArrayList<>();
    private static List<DrillEntity> toRemove = new LinkedList<>();

    public static void addDrillEntity(Location location) {
        drillList.add(new DrillEntity(location));
        PersistentDataContainer data = new CustomBlockData(location.getBlock(), BetterSurvival.getInstance());

        data.set(key, PersistentDataType.BOOLEAN, true);
    }
    public static void removeDrillEntity(Location location, boolean addToQueue) {
        for (Iterator<DrillEntity> iterator = drillList.iterator(); iterator.hasNext();) {
            DrillEntity drill = iterator.next();
            if (!drill.getLocation().equals(location)) continue;
            PersistentDataContainer data = new CustomBlockData(location.getBlock(), BetterSurvival.getInstance());
            data.remove(key);
            if (addToQueue)
                toRemove.add(drill);
            else
                iterator.remove();
            break;
        }
    }


    public static void runTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (DrillEntity drill : drillList)
                    drill.run();
                for (DrillEntity drill : toRemove)
                    drillList.remove(drill);
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 20);
    }

    private Location location;

    public DrillEntity(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    // Should run every second
    public void run() {
        Block block = location.getBlock();

        if (!block.getChunk().isLoaded()) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (block.getType() != Material.DROPPER || !data.has(key, PersistentDataType.BOOLEAN)) {
            toRemove.add(this);
            return;
        }
        Dropper dropper = (Dropper) block.getState();
        Inventory inv = dropper.getInventory();

        if (!inv.contains(Material.COAL)) return;

        List<Block> blocks = getBlocks();
        double hardness = 0;
        for (Block blk : blocks) {
            // Check if the block is unbreakable or too hard to break to prevent breaking of certain blocks such as bedrock
            if (blk.getType().getHardness() > 20 || blk.getType().getHardness() == -1) {
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
                return;
            }
            if (blk.getType().isBlock()) hardness += blk.getType().getHardness();
        }
        hardness /= blocks.size();

        int requiredAmount = (int) Math.max(1, Math.floor(hardness));

        if (!inv.contains(Material.COAL, requiredAmount)) return;
        // Remove the required amount of coal from the inventory
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            int amount = item.getAmount();
            if (amount >= requiredAmount) {
                item.setAmount(amount - requiredAmount);
                break;
            }
            requiredAmount -= amount;
            item.setAmount(0);
        }
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) items.add(inv.getItem(i));

        for (Block blk : blocks)
            if (blk.getType().isBlock()) {
                blk.getWorld().spawnParticle(Particle.BLOCK, blk.getLocation().add(0.5, 0.5, 0.5), 30, 0.4, 0.4, 0.4, 0, blk.getType().createBlockData());

                PersistentDataContainer data1 = new CustomBlockData(blk, BetterSurvival.getInstance());
                if (data1.has(DrillEntity.key, PersistentDataType.BOOLEAN)) {
                    DrillEntity.removeDrillEntity(blk.getLocation(), true);
                    blk.setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getLocation(), ItemType.DRILL_BLOCK.getItem().getItem());
                } else
                    blk.breakNaturally(new ItemStack(Material.DIAMOND_PICKAXE));
            }

        // Move the drill forwards
        data.remove(key);


        Directional directional = (Directional) block.getBlockData();
        block.setType(Material.AIR);
        switch (directional.getFacing()) {
            case DOWN -> location.add(0, -1, 0);
            case UP -> location.add(0, 1, 0);
            case EAST -> location.add(1, 0, 0);
            case WEST -> location.add(-1, 0, 0);
            case SOUTH -> location.add(0, 0, 1);
            case NORTH -> location.add(0, 0, -1);
        }

        block = location.getBlock();
        block.setType(Material.DROPPER);
        block.setBlockData(directional);

        // Transfer the inventory over
        dropper = (Dropper) block.getState();
        dropper.setCustomName(ChatColor.DARK_GREEN+"Add Fuel");
        dropper.update();
        for (int i = 0; i < items.size(); i++)
            dropper.getInventory().setItem(i, items.get(i));

        data = new CustomBlockData(block, BetterSurvival.getInstance());

        data.set(key, PersistentDataType.BOOLEAN, true);

        // Effects
        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0);
        location.getWorld().playSound(location, Sound.BLOCK_STONE_BREAK, 0.7f, 1);
        location.getWorld().spawnParticle(Particle.LARGE_SMOKE, location.clone().add(0.5, 0.8, 0.5), 10, 0.1, 0.1, 0.1, 0);

    }

    private List<Block> getBlocks() {
        Block block = location.getBlock();
        Directional directional = (Directional) block.getBlockData();

        Location loc = location.clone();
        List<Block> blocks = new ArrayList<>();
        switch (directional.getFacing()) {
            case DOWN -> {
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        blocks.add(loc.add(x, -1, z).getBlock());
                        loc.subtract(x, -1, z);
                    }
                }
            }
            case UP -> {
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        blocks.add(loc.add(x, 1, z).getBlock());
                        loc.subtract(x, 1, z);
                    }
                }
            }
            case EAST -> {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        blocks.add(loc.add(1, y, z).getBlock());
                        loc.subtract(1, y, z);
                    }
                }
            }
            case WEST -> {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        blocks.add(loc.add(-1, y, z).getBlock());
                        loc.subtract(-1, y, z);
                    }
                }
            }
            case SOUTH -> {
                for (int y = -1; y < 2; y++) {
                    for (int x = -1; x < 2; x++) {
                        blocks.add(loc.add(x, y, 1).getBlock());
                        loc.subtract(x, y, 1);
                    }
                }
            }
            case NORTH -> {
                for (int y = -1; y < 2; y++) {
                    for (int x = -1; x < 2; x++) {
                        blocks.add(loc.add(x, y, -1).getBlock());
                        loc.subtract(x, y, -1);
                    }
                }
            }
        }
        return blocks;
    }

}
