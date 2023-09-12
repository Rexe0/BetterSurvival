package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class JumboHook extends Item {
    public JumboHook() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Jumbo Hook", "JUMBO_HOOK");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"When reeling in higher tier fish, the");
        lore.add(ChatColor.GRAY+"green zone reduces in size much slower");
        lore.add(ChatColor.GRAY+"when the fish is not within it.");
        return lore;
    }
    public void onAcquireTrade(Villager villager, VillagerAcquireTradeEvent e) {
        if (villager.getProfession() != Villager.Profession.FISHERMAN) return;

        if (e.getRecipe().getIngredients().get(0).getType() == Material.TROPICAL_FISH) {
            MerchantRecipe trade = new MerchantRecipe(ItemType.JUMBO_HOOK.getItem().getItem(), 0, 4, true, 15, 0);
            trade.addIngredient(new ItemStack(Material.EMERALD, 24));
            trade.addIngredient(new ItemStack(Material.IRON_BLOCK, 2));
            e.setRecipe(trade);
        }
    }
}
