package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.FishData;
import me.rexe0.bettersurvival.fishing.FishFile;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FishCodex extends Item {
    public FishCodex() {
        super(Material.BOOK, ChatColor.GREEN+"Fish Codex", "FISH_CODEX");
    }

    public void onCatch(Player player, Fish.FishType type) {
        FishFile.getPlayerData(player).addFish(type);
    }
    @Override
    public void onRightClick(Player player) {
        player.openInventory(getInventory(player, 1));
    }

    public void onInvClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith(ChatColor.DARK_GRAY+"Fish Codex")) return;
        e.setCancelled(true);
        if (ItemDataUtil.isItemName(e.getCurrentItem(), ChatColor.RED+"Exit")) {
            e.getWhoClicked().closeInventory();
            return;
        }

        int page = Integer.parseInt(e.getView().getTitle().substring(13));
        if (ItemDataUtil.isItemName(e.getCurrentItem(), ChatColor.GREEN+"Next Page")) {
            e.getWhoClicked().openInventory(getInventory((Player) e.getWhoClicked(), page+1));
            return;
        }
        if (ItemDataUtil.isItemName(e.getCurrentItem(), ChatColor.GREEN+"Previous Page"))
            e.getWhoClicked().openInventory(getInventory((Player) e.getWhoClicked(), page-1));
    }

    private Inventory getInventory(Player player, int page) {
        FishData playerData = FishFile.getPlayerData(player);
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY+"Fish Codex "+page);

        for (int i : getBorder())
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        for (int i = 0; i < 54; i++) {
            if (i == 49) {
                ItemStack item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED+"Exit");
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
            if (i == 45 && page > 1) {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN+"Previous Page");
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
            if (i == 53 && (Fish.FishType.values().length > 28*page)) {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN+"Next Page");
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }
        int k = (28 * (page - 1));
        for (int i = (28 * (page - 1)); i < 28+(28*(page-1)); i++) {
            if (Fish.FishType.values().length <= k) continue;

            Fish.FishType type = Fish.FishType.values()[k];
            int j = convertArrayIndexToInvIndex(i);
            boolean hasFound = playerData.getAmountFished(type) > 0;

            ItemStack item = new ItemStack(hasFound ? type.getMaterial() : Material.PLAYER_HEAD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(hasFound ? type.getName() : type.getName().substring(0, 2)+"???");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY+"Biome: "+type.getBiome().getName());

            String seasons = type.getSeason()[0].getName();
            for (int l = 1; l < type.getSeason().length; l++) {
                seasons += ", "+type.getSeason()[l].getName();
            }
            lore.add(ChatColor.GRAY+"Season: "+ChatColor.GREEN+seasons);
            lore.add(ChatColor.GRAY+"Time: "+type.getTime().getName());
            lore.add(" ");

            String weight = hasFound ? ChatColor.GREEN+""+type.getMinimumWeight()+" - "+type.getMaximumWeight()+" lbs" : ChatColor.RED+"???";
            lore.add(ChatColor.GRAY+"Weight: "+weight);
            lore.add(" ");
            if (hasFound) {
                lore.add(ChatColor.GRAY+"Amount Caught: "+ChatColor.GREEN+playerData.getAmountFished(type));
            } else
                lore.add(ChatColor.RED+"Not Found.");
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(j, hasFound ? item : SkullUtil.getCustomSkull(item, "http://textures.minecraft.net/texture/46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82"));

            k++;
        }

        return inv;
    }

    private int convertArrayIndexToInvIndex(int i) {
        // Make sure the integer is within the 28 slots given, 0-27

        while (true) {
            if (i < 28) {
                break;
            }
            i -= 28;
        }
        // Calculate the row number, 0-3. 6 rows in a regular inventory, but minus 2 because of border.
        int rowNumber = (int) Math.floor(i/7);

        // Calculate the column to add onto the row
        int columnNumber = (int) Math.floor(i%7);

        // Increase by 1 because of inital row being the border
        rowNumber++;

        // Increase by 1 because of inital collumn being border
        columnNumber++;

        return (rowNumber*9)+columnNumber;
    }
    private List<Integer> getBorder() {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            if (i <= 8 || i >= 45) ints.add(i);
            if (i == 9 || i == 18 || i == 27 || i == 36) ints.add(i);
            if (i == 17 || i == 26 || i == 35 || i == 44) ints.add(i);
        }
        return ints;
    }
}
