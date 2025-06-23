package me.rexe0.bettersurvival.farming;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.farming.Fertilizer;
import me.rexe0.bettersurvival.item.fishing.Fish;
import me.rexe0.bettersurvival.util.ItemDataUtil;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ComposterChanges implements Listener {
    private final NamespacedKey key = new NamespacedKey(BetterSurvival.getInstance(), "COMPOST_FERTILIZER_LEVEL");

    @EventHandler
    public void onEmptyComposter(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.COMPOSTER) return;

        Levelled levelled = (Levelled) e.getClickedBlock().getBlockData();
        if (levelled.getLevel() < 8) return;
        Block block = e.getClickedBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (!data.has(key, PersistentDataType.INTEGER)) return;

        e.setCancelled(true);
        levelled.setLevel(0);
        block.setBlockData(levelled);

        int tier = data.get(key, PersistentDataType.INTEGER);
        block.getWorld().dropItemNaturally(block.getLocation().add(0, 1, 0), new Fertilizer(tier).getItem());

        data.remove(key);
    }

    @EventHandler
    public void onCompost(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.COMPOSTER) return;
        if (e.getItem() == null) return;
        int amount = getCompostAmount(e.getItem());
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
    public void onCompostTake(InventoryMoveItemEvent e) {
        if (e.getSource().getType() != InventoryType.COMPOSTER) return;

        Location loc = e.getDestination().getLocation();
        if (loc.getBlock().getType() != Material.HOPPER) return;

        Block block = loc.add(0, 1, 0).getBlock();
        Levelled levelled = (Levelled) block.getBlockData();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (!data.has(key, PersistentDataType.INTEGER)) return;
        e.setCancelled(true);

        int tier = data.get(key, PersistentDataType.INTEGER);
        ItemStack fertilizer = new Fertilizer(tier).getItem();

        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            for (int i = 0; i < e.getDestination().getSize(); i++) {
                ItemStack item = e.getDestination().getStorageContents()[i];
                // Look for an empty space or an existing fertilizer of the same type
                if (item != null && (!item.isSimilar(fertilizer) || item.getAmount() >= 64)) continue;

                levelled.setLevel(0);
                block.setBlockData(levelled);

                data.remove(key);

                if (item != null)
                    item.setAmount(item.getAmount() + 1);
                else item = fertilizer;

                ItemStack[] items = e.getDestination().getStorageContents();
                items[i] = item;
                e.getDestination().setStorageContents(items);
                break;
            }
        }, 1);
    }
    @EventHandler
    public void onHopperMove(InventoryMoveItemEvent e) {
        if (e.getSource().getType() != InventoryType.HOPPER) return;
        Location loc = e.getSource().getLocation().subtract(0, 1, 0);
        if (loc.getBlock().getType() != Material.COMPOSTER) return;

        int amount = getCompostAmount(e.getItem());
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
        block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, block.getLocation().add(0.5, 0.6, 0.5), 10, 0.2, 0.2, 0.2, 0);

        levelled.setLevel(Math.min(levelled.getLevel() + amount, levelled.getMaximumLevel() - 1));
        block.setBlockData(levelled);
        if (amount > 7) {
            PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

            data.set(key, PersistentDataType.INTEGER, amount-7);
        }
    }

    private int getCompostAmount(ItemStack item) {
        if (!ItemDataUtil.getStringValue(item, "fishType").isEmpty()) {
            Fish.FishType fish = Fish.FishType.valueOf(ItemDataUtil.getStringValue(item, "fishType"));
            if (fish.getName().startsWith(ChatColor.GREEN+"")) return 8;
            if (fish.getName().startsWith(ChatColor.BLUE+"")) return 9;
            if (fish.getName().startsWith(ChatColor.DARK_PURPLE+"")) return 10;
            if (fish.getName().startsWith(ChatColor.GOLD+"")) return 11;
        }

        return switch (item.getType()) {
            default -> 0;
            case JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES, DARK_OAK_LEAVES, ACACIA_LEAVES, CHERRY_LEAVES, BIRCH_LEAVES,
                    AZALEA_LEAVES, MANGROVE_LEAVES, OAK_SAPLING, SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING,
                    CHERRY_SAPLING, DARK_OAK_SAPLING, MANGROVE_PROPAGULE, BEETROOT_SEEDS, DRIED_KELP, SHORT_GRASS, KELP, MELON_SEEDS,
                    PUMPKIN_SEEDS, SEAGRASS, SWEET_BERRIES, GLOW_BERRIES, WHEAT_SEEDS, MOSS_CARPET, PINK_PETALS, SMALL_DRIPLEAF,
                    HANGING_ROOTS, MANGROVE_ROOTS, TORCHFLOWER_SEEDS, PITCHER_POD,LEAF_LITTER ->
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
