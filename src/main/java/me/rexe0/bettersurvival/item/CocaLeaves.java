package me.rexe0.bettersurvival.item;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.CocaineListener;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CocaLeaves extends Item {

    private final int potency;
    public CocaLeaves(int potency) {
        super(Material.OAK_LEAVES, ChatColor.DARK_GREEN+"Coca Leaves", "COCA_LEAVES");
        this.potency = potency;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "potency", potency));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A plant with potential psychoactive");
        lore.add(ChatColor.GRAY+"effects. It seems to like very warm");
        lore.add(ChatColor.GRAY+"and humid climates.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Potency: "+ChatColor.GREEN+potency+"%");
        return lore;
    }


    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        if (e.isCancelled()) return;
        Block block = e.getBlock();
        block.setType(Material.OAK_LEAVES);
        Leaves leaves = (Leaves) block.getBlockData();
        leaves.setPersistent(true);
        block.setBlockData(leaves);

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        int potency = ItemDataUtil.getIntegerValue(e.getItemInHand(), "potency");
        data.set(CocaineListener.COCAINE_KEY, PersistentDataType.INTEGER, potency);
    }
}
