package me.rexe0.bettersurvival.farming;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.HolidayListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class HarvestModifier implements Listener {
    public static final NamespacedKey BONEMEAL_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BONEMEAL_TIER");

    private final Map<Material, Material[]> cropDrops = new HashMap<>();

    public HarvestModifier() {
        cropDrops.put(Material.WHEAT, new Material[]{Material.WHEAT_SEEDS, Material.WHEAT});
        cropDrops.put(Material.CARROTS, new Material[]{Material.CARROT});
        cropDrops.put(Material.POTATOES, new Material[]{Material.POTATO});
        cropDrops.put(Material.BEETROOTS, new Material[]{Material.BEETROOT_SEEDS, Material.BEETROOT});
    }

    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (!cropDrops.containsKey(block.getType())) return;
        Material[] drops = cropDrops.get(block.getType());

        e.setDropItems(false);
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        int dropCount = 1;
        int seedCount = 2;

        // If the player bonemealed the crop, it should always yield one more of itself
        if (data.has(BONEMEAL_KEY, PersistentDataType.INTEGER)) {
            data.remove(BONEMEAL_KEY);
            dropCount++;
            seedCount++;
        }

        // If harvested early, it should only drop 1 of its seed
        if (((Ageable)block.getBlockData()).getAge() != ((Ageable)block.getBlockData()).getMaximumAge()) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drops[0], 1));
            return;
        }

        // Fortune hoes. Other fortune tools shouldn't work
        ItemStack item = player.getEquipment().getItemInMainHand();
        if (item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && item.getType().toString().contains("HOE")) {
            int level = item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
            for (int i = 0; i < level; i++)
                if (RandomUtil.getRandom().nextInt(3) == 0) {
                    dropCount++;
                    seedCount++;
                }

            Damageable damageable = (Damageable) item.getItemMeta();
            damageable.setDamage(damageable.getDamage() - 1);
            if (damageable.getDamage() == item.getType().getMaxDurability())
                player.getEquipment().setItemInMainHand(null);
        }

        if (HolidayListener.bumperCropHarvest(block.getWorld()))
            dropCount += 2;

        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drops[0], seedCount));
        if (drops.length > 1)
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drops[1], dropCount));
}

    @EventHandler
    public void onBonemeal(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = e.getItem();
        int value = onBonemeal(e.getClickedBlock(), item);
        if (value > -1) {
            e.setCancelled(true);
            if (value == 1)
                item.setAmount(item.getAmount()-1);
        }
    }

    @EventHandler
    public void onDispenseSeed(BlockDispenseEvent e) {
        if (e.getBlock().getType() != Material.DISPENSER) return;
        Material material = null;
        for (Map.Entry<Material, Material[]> entry : cropDrops.entrySet())
            if (e.getItem().getType() == entry.getValue()[0]) {
                material = entry.getKey();
                break;
            }
        if (material == null) return;

        Directional directional = (Directional) e.getBlock().getBlockData();
        Block target = e.getBlock().getLocation().add(directional.getFacing().getDirection()).getBlock();

        if (target.getType() != Material.AIR || target.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.FARMLAND) return;

        e.setCancelled(true);

        target.setType(material);

        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            Dispenser dispenser = (Dispenser) e.getBlock().getState();
            for (ItemStack item : dispenser.getInventory()) {
                if (item == null) continue;
                if (item.getType() != e.getItem().getType()) continue;
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }, 1);
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent e) {
        if (e.getItem().getType() != Material.BONE_MEAL) return;
        if (e.getBlock().getType() != Material.DISPENSER) return;

        Directional directional = (Directional) e.getBlock().getBlockData();
        Block target = e.getBlock().getLocation().add(directional.getFacing().getDirection()).getBlock();

        int value = onBonemeal(target, e.getItem());
        if (value > -1) {
            e.setCancelled(true);
            if (value == 1) {
                Dispenser dispenser = (Dispenser) e.getBlock().getState();
                for (ItemStack item : dispenser.getInventory()) {
                    if (item == null) continue;
                    if (item.getType() != e.getItem().getType()) continue;
                    item.setAmount(item.getAmount() - 1);
                    break;
                }
            }
        }
    }


    // Returns -1 if not applicable, Returns 0 if bonemeal shouldn't be consumed, Returns 1 if bonemeal should be consued
    private int onBonemeal(Block block, ItemStack item) {
        if (item == null) return -1;
        if (item.getType() != Material.BONE_MEAL) return -1;
        if (!cropDrops.containsKey(block.getType())) return -1;

        // Can only bonemeal the crop when it is nascent
        if (((Ageable)block.getBlockData()).getAge() > 1)
            return 0;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (data.has(BONEMEAL_KEY, PersistentDataType.INTEGER)) return 0;

        int tier = ItemDataUtil.getIntegerValue(item, "fertilizerTier");
        data.set(BONEMEAL_KEY, PersistentDataType.INTEGER, tier);

        block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().add(0.5, 0.1, 0.5), 20, 0.3, 0.2, 0.3, 0);
        return 1;
    }
}
