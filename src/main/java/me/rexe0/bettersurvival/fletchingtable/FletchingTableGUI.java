package me.rexe0.bettersurvival.fletchingtable;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FletchingTableGUI implements Listener {
    private final Map<Material, ItemStack> arrowRecipes;

    public FletchingTableGUI() {
        arrowRecipes = new HashMap<>();
        arrowRecipes.put(Material.AMETHYST_SHARD, ItemType.AMETHYST_ARROW.getItem().getItem());
        arrowRecipes.put(Material.TNT, ItemType.EXPLOSIVE_ARROW.getItem().getItem());
        arrowRecipes.put(Material.RED_MUSHROOM, ItemType.TOXIC_ARROW.getItem().getItem());
        arrowRecipes.put(Material.ECHO_SHARD, ItemType.SONIC_ARROW.getItem().getItem());
        arrowRecipes.put(Material.GLOWSTONE, new ItemStack(Material.SPECTRAL_ARROW));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY+"Fletching Table")) return;
        if (e.getClickedInventory() == e.getView().getTopInventory() && e.getSlot() != 10 && e.getSlot() != 12 && e.getSlot() != 16) e.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> clickLogic(e.getView().getTopInventory()), 1);
        thirdSlotLogic(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY+"Fletching Table")) return;
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (player.getInventory().firstEmpty() == -1) {
            if (inv.getItem(10) != null)
                player.getWorld().dropItem(player.getLocation(), inv.getItem(10)).setOwner(player.getUniqueId());
            if (inv.getItem(12) != null)
                player.getWorld().dropItem(player.getLocation(), inv.getItem(12)).setOwner(player.getUniqueId());
        } else {
            if (inv.getItem(10) != null)
                player.getInventory().addItem(inv.getItem(10));
            if (inv.getItem(12) != null)
                player.getInventory().addItem(inv.getItem(12));
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.FLETCHING_TABLE) return;
        e.setCancelled(true);
        e.getPlayer().openInventory(getInventory());
    }

    private Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY+"Fletching Table");

        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 12)
                inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            if (getLeftSide().contains(i) || getRightSide().contains(i))
                inv.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
            if (i == 14) {
                ItemStack item = new ItemStack(Material.FLETCHING_TABLE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN+"Craft");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY+"Place an arrow in the first slot and");
                lore.add(ChatColor.GRAY+"an ingredient in the second slot.");
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
            if (i == 16) {
                ItemStack item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED+"Fletch");
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }

        return inv;
    }
    private void clickLogic(Inventory inv) {
        boolean left = inv.getItem(10) != null && inv.getItem(10).getType() == Material.ARROW;
        getLeftSide().forEach(i -> inv.setItem(i, new ItemStack(
                left ? Material.LIME_STAINED_GLASS_PANE
                        : Material.RED_STAINED_GLASS_PANE)));

        ItemStack item = inv.getItem(12);
        boolean right = item != null && (item.getType().toString().contains("POTION") || arrowRecipes.containsKey(item.getType()));
        getRightSide().forEach(i -> inv.setItem(i, new ItemStack(
                right ? Material.LIME_STAINED_GLASS_PANE
                        : Material.RED_STAINED_GLASS_PANE)));

        if (!left || !right) {
            inv.getItem(14).removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);

            item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED+"Fletch");
            item.setItemMeta(meta);
            inv.setItem(16, item);
            return;
        }
        inv.getItem(14).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        if (arrowRecipes.containsKey(item.getType())) {
            ItemStack result = arrowRecipes.get(item.getType()).clone();
            result.setAmount(inv.getItem(10).getAmount());
            inv.setItem(16, result);
            return;
        }
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        ItemStack result = new ItemStack(Material.TIPPED_ARROW);
        result.setAmount(inv.getItem(10).getAmount());
        PotionMeta arrowMeta = (PotionMeta) result.getItemMeta();
        arrowMeta.setBasePotionType(meta.getBasePotionType());
        result.setItemMeta(arrowMeta);

        inv.setItem(16, result);
    }
    private void thirdSlotLogic(InventoryClickEvent e) {
        if (e.getSlot() != 16) return;
        Inventory inv = e.getInventory();
        if ((inv.getItem(10) == null || inv.getItem(10).getType() != Material.ARROW)
                || inv.getItem(12) == null || (!inv.getItem(12).getType().toString().contains("POTION") && !arrowRecipes.containsKey(inv.getItem(12).getType()))) {
            e.setCancelled(true);
            return;
        }
        inv.setItem(10, null);
        inv.setItem(12, null);

        Player player = ((Player)e.getWhoClicked());
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
    }

    private List<Integer> getLeftSide() {
        return List.of(0, 9, 18);
    }
    private List<Integer> getRightSide() {
        return List.of(8, 17, 26);
    }
}
