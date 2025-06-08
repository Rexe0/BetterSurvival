package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class DrugRequest extends AmountRequest {
    private final ItemType itemType;
    public DrugRequest(boolean isCannabis, int amount) {
        super(amount);
        this.itemType = isCannabis ? ItemType.CANNABIS : ItemType.COCAINE;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getPrice(ItemStack item) {
        if (!ItemDataUtil.isItem(item, itemType.getItem().getID())) return -1;
        if (item.getAmount() < getAmount()) return -2;
        int potency = ItemDataUtil.getIntegerValue(item, "potency");

        return (int) Math.ceil((3*getAmount())/(1+Math.exp(-0.04*(potency-50))));
    }
    public String getMessage() {
        return "I want "+ ChatColor.YELLOW+getAmount()+"x "+itemType.getItem().getName()+ChatColor.RESET+".";
    }
}
