package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import net.minecraft.world.entity.projectile.FishingHook;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftFishHook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
        Player player = e.getPlayer();

        ItemType bait = null;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            ItemType type = ItemDataUtil.getItemType(itemStack);
            if (type != null && type.isBait()) {
                bait = type;
                itemStack.setAmount(itemStack.getAmount()-1);
                break;
            }
        }
        if (bait != null) {
        }
    }
}
