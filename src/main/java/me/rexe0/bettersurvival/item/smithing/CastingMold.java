package me.rexe0.bettersurvival.item.smithing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CastingMold extends Item {
    private final SmithingType type;
    public CastingMold(SmithingType type) {
        super(Material.BOOK, ChatColor.GREEN+"Casting Mold", "CASTING_MOLD");
        this.type = type;
    }


    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setStringValue(item, "smithingType", type.name()));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A mold used for casting molten");
        lore.add(ChatColor.GRAY+"metals into specific shapes.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Shape: "+ChatColor.GREEN+type.getName());
        return lore;
    }

    public SmithingType getType() {
        return type;
    }
}
