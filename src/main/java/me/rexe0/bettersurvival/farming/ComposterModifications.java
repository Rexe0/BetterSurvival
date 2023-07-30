package me.rexe0.bettersurvival.farming;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ComposterModifications implements Listener {
    @EventHandler
    public void onCompost(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.COMPOSTER) return;
        if (e.getItem() == null) return;
        int amount = getCompostAmount(e.getItem().getType());
        if (amount == 0) return;

        Levelled levelled = (Levelled) e.getClickedBlock().getBlockData();
        if (levelled.getLevel() >= levelled.getMaximumLevel()) return;

        e.setCancelled(true);

        if (levelled.getLevel() == 7) return;

        // Manually remove the item as we are cancelling the event
        e.getItem().setAmount(e.getItem().getAmount() - 1);

        calculateComposter(e.getClickedBlock(), amount);
    }

    @EventHandler
    public void onHopperMove(InventoryMoveItemEvent e) {
        if (e.getSource().getType() != InventoryType.HOPPER) return;
        Location loc = e.getSource().getLocation().subtract(0, 1, 0);
        if (loc.getBlock().getType() != Material.COMPOSTER) return;

        int amount = getCompostAmount(e.getItem().getType());
        if (amount == 0) return;
        e.setCancelled(true);

        Levelled levelled = (Levelled) loc.getBlock().getBlockData();
        if (levelled.getLevel() >= levelled.getMaximumLevel()-1) return;

        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            // Manually remove the item as we are cancelling the event
            for (ItemStack item : e.getSource().getStorageContents()) {
                if (item == null) continue;
                if (item.getType() != e.getItem().getType()) continue;
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }, 1);

        calculateComposter(loc.getBlock(), amount);

    }

    private void calculateComposter(Block block, int amount) {
        Levelled levelled = (Levelled) block.getBlockData();

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1, 1);
        block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().add(0.5, 0.6, 0.5), 10, 0.2, 0.2, 0.2, 0);

        levelled.setLevel(Math.min(levelled.getLevel() + amount, levelled.getMaximumLevel() - 1));
        block.setBlockData(levelled);
    }

    private int getCompostAmount(Material material) {
        return switch (material) {
            default -> 0;
            case JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES, DARK_OAK_LEAVES, ACACIA_LEAVES, CHERRY_LEAVES, BIRCH_LEAVES,
                    AZALEA_LEAVES, MANGROVE_LEAVES, OAK_SAPLING, SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING,
                    CHERRY_SAPLING, DARK_OAK_SAPLING, MANGROVE_PROPAGULE, BEETROOT_SEEDS, DRIED_KELP, GRASS, KELP, MELON_SEEDS,
                    PUMPKIN_SEEDS, SEAGRASS, SWEET_BERRIES, GLOW_BERRIES, WHEAT_SEEDS, MOSS_CARPET, PINK_PETALS, SMALL_DRIPLEAF,
                    HANGING_ROOTS, MANGROVE_ROOTS, TORCHFLOWER_SEEDS, PITCHER_POD ->
                    1;
            case DRIED_KELP_BLOCK, TALL_GRASS, FLOWERING_AZALEA_LEAVES, CACTUS, SUGAR_CANE, VINE, NETHER_SPROUTS, WEEPING_VINES,
                    TWISTING_VINES, MELON_SLICE, GLOW_LICHEN ->
                    2;
            case LILY_PAD, PUMPKIN, CARVED_PUMPKIN, MELON, APPLE, BEETROOT, CARROT, COCOA_BEANS, POTATO, WHEAT, BROWN_MUSHROOM,
                    RED_MUSHROOM, MUSHROOM_STEM, CRIMSON_FUNGUS, WARPED_FUNGUS, NETHER_WART, CRIMSON_ROOTS, WARPED_ROOTS, SHROOMLIGHT,
                    DANDELION, POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY,
                    CORNFLOWER, LILY_OF_THE_VALLEY, WITHER_ROSE, FERN, SUNFLOWER, LILAC, ROSE_BUSH, PEONY, LARGE_FERN, SPORE_BLOSSOM,
                    AZALEA, MOSS_BLOCK, BIG_DRIPLEAF ->
                    3;
            case HAY_BLOCK, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, NETHER_WART_BLOCK, WARPED_WART_BLOCK, FLOWERING_AZALEA, BREAD,
                    BAKED_POTATO, COOKIE, TORCHFLOWER, PITCHER_PLANT ->
                    5;
            case CAKE, PUMPKIN_PIE -> 7;
        };
    }
}
