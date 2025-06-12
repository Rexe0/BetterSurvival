package me.rexe0.bettersurvival.mining;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.SculkCatalyst;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DeepDarkChanges implements Listener {
    private static boolean heartBeating = false;
    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.SCULK_SHRIEKER) return;
        if (e.getPlayer().getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) return;
        e.setDropItems(false);
        if (RandomUtil.getRandom().nextInt(3) != 0) return;
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.ECHO_SHARD));
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.TORCH && block.getType() != Material.LANTERN) return;
        if (block.getBiome() != Biome.DEEP_DARK) return;

        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            for (Player player : block.getWorld().getPlayers())
                if (player.getLocation().distanceSquared(block.getLocation()) < 400) {
                    if (!player.hasPotionEffect(PotionEffectType.DARKNESS) || (player.hasPotionEffect(PotionEffectType.DARKNESS) && player.getPotionEffect(PotionEffectType.DARKNESS).getDuration() < 200)) {
                        player.removePotionEffect(PotionEffectType.DARKNESS);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 0, true, false));
                    }
                }
        }, 20);

        if (heartBeating) return;
        // If warden doesn't exist, play heartbeat SFX
        for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 50, 50, 50))
            if (entity instanceof Warden) return;
        heartBeating = true;
        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                if (i >= 3) {
                    heartBeating = false;
                    cancel();
                    return;
                }
                block.getWorld().playSound(block.getLocation().subtract(0, 100, 0), Sound.ENTITY_WARDEN_HEARTBEAT, 10, 0.8f);
                i++;
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 20, 40);
    }

    public static void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block;
            for (int i = 0; i < 50; i++) {
                Location loc = player.getLocation().add(
                        RandomUtil.getRandom().nextInt(-64, 64),
                        0,
                        RandomUtil.getRandom().nextInt(-64, 64));
                loc.setY(RandomUtil.getRandom().nextInt(-60, -25));
                block = loc.getBlock();

                if (block.getType() == Material.SCULK_CATALYST) {
                    SculkCatalyst catalyst = (SculkCatalyst) block.getState();
                    for (int x = -1; x <= 1; x++)
                        for (int z = -1; z <= 1; z++)
                            catalyst.bloom(loc.add(x, 0, z).getBlock(), 3);
                }
                if (block.getType() == Material.SCULK_SHRIEKER) {
                    SculkShrieker shrieker = (SculkShrieker) block.getBlockData();
                    shrieker.setCanSummon(true);
                }


            }
        }
    }
}
