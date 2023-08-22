package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.fishing.Fish;
import me.rexe0.bettersurvival.item.fishing.TreasureChest;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.weather.Season;
import net.minecraft.world.entity.projectile.FishingHook;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftFishHook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CatchListener implements Listener {
    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.FISHING) return;
        Player player = e.getPlayer();
        ItemStack fishingRod = e.getHand() == EquipmentSlot.HAND
                ? player.getEquipment().getItemInMainHand() : player.getEquipment().getItemInOffHand();
        ItemType itemType = ItemDataUtil.getItemType(fishingRod);

        int min = 100;
        int max = 600;
        if (itemType != null) {
            min = switch (itemType) {
                default -> 100;
                case COPPER_FISHING_ROD -> 75;
                case PLATINUM_FISHING_ROD -> 50;
                case RESONANT_FISHING_ROD -> 25;
            };

            max = switch (itemType) {
                default -> 600;
                case COPPER_FISHING_ROD -> 550;
                case PLATINUM_FISHING_ROD -> 500;
                case RESONANT_FISHING_ROD -> 400;
            };

            ItemType bait = null;
            for (ItemStack itemStack : player.getInventory().getContents()) {
                ItemType type = ItemDataUtil.getItemType(itemStack);
                if (type != null && type.isBait()) {
                    bait = type;
                    break;
                }
            }
            if (bait != null && itemType.canUseBait()) {
                min *= 0.5;
                max *= 0.5;
            }
        }

        if (fishingRod.containsEnchantment(Enchantment.LURE)) {
            min *= 1 - (fishingRod.getEnchantmentLevel(Enchantment.LURE) * 0.1);
            max *= 1 - (fishingRod.getEnchantmentLevel(Enchantment.LURE) * 0.1);
        }

        e.getHook().setMinWaitTime(min);
        e.getHook().setMaxWaitTime(max);

        FishingHook hook = ((CraftFishHook)e.getHook()).getHandle();
        hook.applyLure = false;
    }


    @EventHandler
    public void onCatch(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(e.getCaught() instanceof Item item)) return;
        Player player = e.getPlayer();

        ItemType bait = null;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            ItemType type = ItemDataUtil.getItemType(itemStack);
            if (type != null && type.isBait()) {
                bait = type;
                itemStack.setAmount(itemStack.getAmount() - 1);
                break;
            }
        }

        BiomeGroup biome = null;

        if (e.getHook().getLocation().getY() < 20) biome = BiomeGroup.CAVERNS;
        else {
            for (BiomeGroup group : BiomeGroup.values()) {
                for (Biome bio : group.getBiomes())
                    if (bio == e.getHook().getLocation().getBlock().getBiome()) {
                        biome = group;
                        break;
                    }
                if (biome != null) break;
            }
            if (biome == null) biome = BiomeGroup.FOREST;
        }


        // Fish
        item.setItemStack(getCatch(biome, bait));

        // Treasure
        if (Math.random() < (bait == ItemType.MAGNET ? 0.2 : 0.1)) {
            Item treasure = player.getWorld().dropItem(item.getLocation(), getTreasure());
            treasure.setVelocity(item.getVelocity());
            treasure.setOwner(player.getUniqueId());
            treasure.setPickupDelay(0);

            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
            player.sendMessage(ChatColor.GREEN+"You managed to pull up some additional treasure.");
        }
    }


    @EventHandler
    public void onPlaceTreasureChest(BlockPlaceEvent e) {
        ((TreasureChest)ItemType.TREASURE_CHEST.getItem()).onBlockPlace(e);
        ((TreasureChest)ItemType.TREASURE_SAND.getItem()).onBlockPlace(e);
    }

    private ItemStack getTreasure() {
        // Treasure Chest
        if (Math.random() < 0.25)
            return ItemType.TREASURE_CHEST.getItem().getItem();
        return ItemType.TREASURE_SAND.getItem().getItem();
    }

    private ItemStack getCatch(BiomeGroup biome, ItemType bait) {
        Fish.FishType[] possibleFish = Arrays.stream(Fish.FishType.values())
                .filter(f -> f.getBiome() == biome
                        && Arrays.stream(f.getSeason()).toList().contains(Season.getSeason())
                        && f.getTime().isValid(BetterSurvival.getInstance().getDefaultWorld().getTime()))
                .toArray(Fish.FishType[]::new);

        int totalWeight = 0;
        for (Fish.FishType type : possibleFish)
            totalWeight += type.getWeight();

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < possibleFish.length - 1; ++idx) {
            r -= possibleFish[idx].getWeight();
            if (r <= 0.0) break;
        }
        return new Fish(possibleFish[idx]).getItem();
    }
}
