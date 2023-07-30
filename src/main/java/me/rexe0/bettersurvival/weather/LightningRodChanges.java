package me.rexe0.bettersurvival.weather;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LightningRodChanges implements Listener {
    private final short BURN_TIME = 1200;
    private final NamespacedKey key = new NamespacedKey(BetterSurvival.getInstance(), "TIME_WHEN_STRUCK");

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent e) {
        Block block = e.getBlock();
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (data.has(key, PersistentDataType.LONG))
            if (block.getWorld().getGameTime() - data.get(key, PersistentDataType.LONG) < BURN_TIME)
                e.setTotalCookTime(e.getTotalCookTime() / 2);
    }

    @EventHandler
    public void onStrikeLightning(LightningStrikeEvent e) {
        if (e.getCause() != LightningStrikeEvent.Cause.WEATHER) return;
        Block block = e.getLightning().getLocation().subtract(0, 1, 0).getBlock();
        if (block.getType() != Material.LIGHTNING_ROD) return;

        block = block.getLocation().subtract(0, 1, 0).getBlock();

        if (block.getType() == Material.COPPER_BLOCK || block.getType() == Material.WAXED_COPPER_BLOCK)
            channelLightning(block);
    }

    private void channelLightning(Block block) {
        block.getWorld().playSound(block.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1, 2);

        List<Block> alreadyChanneled = new ArrayList<>();
        List<Block> blocks = new LinkedList<>();
        blocks.add(block);

        int j = 32;
        int power = 9;

        while (blocks.size() > 0) {
            List<Block> list = new ArrayList<>(blocks);
            for (Block blk : list) {
                blocks.addAll(getAdjacentCopperBlocks(blk, alreadyChanneled));

                for (Block furnaceBlock : getAdjacentFurnaces(blk)) {
                    Furnace furnace = (Furnace) furnaceBlock.getState();

                    // Prevent a furnace being detected twice
                    if (furnace.getBurnTime() != BURN_TIME) power--;

                    PersistentDataContainer data = new CustomBlockData(furnaceBlock, BetterSurvival.getInstance());
                    data.set(key, PersistentDataType.LONG, furnaceBlock.getWorld().getGameTime());

                    furnace.setBurnTime(BURN_TIME);
                    furnace.update();

                    if (power <= 1) return;
                }

                blocks.remove(blk);
                alreadyChanneled.add(blk);
                j--;
                if (j <= 0) return;
            }
        }
    }

    private List<Block> getAdjacentCopperBlocks(Block block, List<Block> alreadyChanneled) {
        List<Block> blocks = new ArrayList<>();
        Location loc = block.getLocation();
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0 -> {
                    loc.add(1, 0, 0);
                    if (!alreadyChanneled.contains(loc.getBlock())
                            && (loc.getBlock().getType() == Material.COPPER_BLOCK
                            || loc.getBlock().getType() == Material.WAXED_COPPER_BLOCK))
                        blocks.add(loc.getBlock());
                    loc.subtract(1, 0, 0);
                }
                case 1 -> {
                    loc.add(-1, 0, 0);
                    if (!alreadyChanneled.contains(loc.getBlock())
                            && (loc.getBlock().getType() == Material.COPPER_BLOCK
                            || loc.getBlock().getType() == Material.WAXED_COPPER_BLOCK))
                        blocks.add(loc.getBlock());
                    loc.subtract(-1, 0, 0);
                }
                case 2 -> {
                    loc.add(0, 1, 0);
                    if (!alreadyChanneled.contains(loc.getBlock())
                            && (loc.getBlock().getType() == Material.COPPER_BLOCK
                            || loc.getBlock().getType() == Material.WAXED_COPPER_BLOCK))
                        blocks.add(loc.getBlock());
                    loc.subtract(0, 1, 0);
                }
                case 3 -> {
                    loc.add(0, -1, 0);
                    if (!alreadyChanneled.contains(loc.getBlock())
                            && (loc.getBlock().getType() == Material.COPPER_BLOCK
                            || loc.getBlock().getType() == Material.WAXED_COPPER_BLOCK))
                        blocks.add(loc.getBlock());
                    loc.subtract(0, -1, 0);
                }
                case 4 -> {
                    loc.add(0, 0, 1);
                    if (!alreadyChanneled.contains(loc.getBlock())
                            && (loc.getBlock().getType() == Material.COPPER_BLOCK
                            || loc.getBlock().getType() == Material.WAXED_COPPER_BLOCK))
                        blocks.add(loc.getBlock());
                    loc.subtract(0, 0, 1);
                }
                case 5 -> {
                    loc.add(0, 0, -1);
                    if (!alreadyChanneled.contains(loc.getBlock())
                            && (loc.getBlock().getType() == Material.COPPER_BLOCK
                            || loc.getBlock().getType() == Material.WAXED_COPPER_BLOCK))
                        blocks.add(loc.getBlock());
                    loc.subtract(0, 0, -1);
                }
            }
        }
        return blocks;
    }
    private List<Block> getAdjacentFurnaces(Block block) {
        List<Block> blocks = new ArrayList<>();
        Location loc = block.getLocation();
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0 -> {
                    loc.add(1, 0, 0);
                    if (loc.getBlock().getState() instanceof Furnace)
                        blocks.add(loc.getBlock());
                    loc.subtract(1, 0, 0);
                }
                case 1 -> {
                    loc.add(-1, 0, 0);
                    if (loc.getBlock().getState() instanceof Furnace)
                        blocks.add(loc.getBlock());
                    loc.subtract(-1, 0, 0);
                }
                case 2 -> {
                    loc.add(0, 1, 0);
                    if (loc.getBlock().getState() instanceof Furnace)
                        blocks.add(loc.getBlock());
                    loc.subtract(0, 1, 0);
                }
                case 3 -> {
                    loc.add(0, -1, 0);
                    if (loc.getBlock().getState() instanceof Furnace)
                        blocks.add(loc.getBlock());
                    loc.subtract(0, -1, 0);
                }
                case 4 -> {
                    loc.add(0, 0, 1);
                    if (loc.getBlock().getState() instanceof Furnace)
                        blocks.add(loc.getBlock());
                    loc.subtract(0, 0, 1);
                }
                case 5 -> {
                    loc.add(0, 0, -1);
                    if (loc.getBlock().getState() instanceof Furnace)
                        blocks.add(loc.getBlock());
                    loc.subtract(0, 0, -1);
                }
            }
        }
        return blocks;
    }
}
