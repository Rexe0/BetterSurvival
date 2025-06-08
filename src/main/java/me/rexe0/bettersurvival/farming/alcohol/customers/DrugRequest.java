package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class DrugRequest extends Request {
    private final ItemType itemType;
    private final int amount;

    public DrugRequest(boolean isCannabis, int amount) {
        this.itemType = isCannabis ? ItemType.CANNABIS : ItemType.COCAINE;
        this.amount = amount;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getAmount() {
        return amount;
    }
    public int getPrice(ItemStack item) {
        if (!ItemDataUtil.isItem(item, itemType.getItem().getID())) return -1;
        if (item.getAmount() < amount) return -1;
        int potency = ItemDataUtil.getIntegerValue(item, "potency");
        int amount = item.getAmount();

        return (int) Math.ceil((3*amount)/(1+Math.exp(-0.04*(potency-50))));
    }
    public String getMessage() {
        return "I want "+ ChatColor.YELLOW+amount+"x "+itemType.getItem().getName()+ChatColor.RESET+".";
    }
}
