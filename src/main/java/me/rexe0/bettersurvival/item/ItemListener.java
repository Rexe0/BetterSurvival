package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.drugs.BlockOfCocaine;
import me.rexe0.bettersurvival.item.drugs.Cannabis;
import me.rexe0.bettersurvival.item.drugs.CocaLeaves;
import me.rexe0.bettersurvival.item.fishing.FishCodex;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class ItemListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        DrillBlock drillBlock = (DrillBlock) ItemType.DRILL_BLOCK.getItem();
        drillBlock.onBlockPlace(e);

        Cannabis cannabis = (Cannabis) ItemType.CANNABIS.getItem();
        cannabis.onBlockPlace(e);

        CocaLeaves cocaLeaves = (CocaLeaves) ItemType.COCA_LEAVES.getItem();
        cocaLeaves.onBlockPlace(e);

        BlockOfCocaine blockOfCocaine = (BlockOfCocaine) ItemType.BLOCK_OF_COCAINE.getItem();
        blockOfCocaine.onBlockPlace(e);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        DrillBlock drillBlock = (DrillBlock) ItemType.DRILL_BLOCK.getItem();
        drillBlock.onBlockBreak(e);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        ((FishCodex)ItemType.FISH_CODEX.getItem()).onInvClick(e);
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        DrillBlock drillBlock = (DrillBlock) ItemType.DRILL_BLOCK.getItem();
        drillBlock.onRightClick(e);
        for (ItemType itemType : ItemType.values())
            if (ItemDataUtil.isItem(e.getItem(), itemType.getItem().getID())) {
                if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND)
                    itemType.getItem().onRightClick(e.getPlayer());
                if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND)
                    itemType.getItem().onLeftClick(e.getPlayer());
            }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        for (ItemType itemType : ItemType.values())
            if (ItemDataUtil.isItem(e.getItem(), itemType.getItem().getID())) {
                itemType.getItem().onConsume(e.getPlayer());
                break;
            }
    }
    @EventHandler
    public void onBreakCrops(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL) return;
        ItemStack item = e.getPlayer().getEquipment().getBoots();
        if (!ItemDataUtil.isItem(item, ItemType.FARMER_BOOTS.getItem().getID())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlant(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null) return;
        ItemStack item = e.getItem();
        if (ItemDataUtil.isItem(item, ItemType.BAIT.getItem().getID()) || ItemDataUtil.isItem(item, ItemType.PREMIUM_BAIT.getItem().getID()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onSmith(PrepareSmithingEvent e) {
        Inventory inv = e.getInventory();

        for (Recipe recipe : BetterSurvival.getInstance().getRecipes().values()) {
            if (!(recipe instanceof SmithingTransformRecipe smithingRecipe)) continue;

            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                for (int i = 0; i < 3; i++) if (inv.getItem(i) == null) return;

                if (smithingRecipe.getTemplate().test(inv.getItem(0))
                        && smithingRecipe.getBase().test(inv.getItem(1))
                        && smithingRecipe.getAddition().test(inv.getItem(2)))
                    inv.setItem(3, smithingRecipe.getResult());

            }, 1);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Horse horse)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        // If the horse is wearing Saddle 'n' Horseshoe, halve its fall damage
        if (ItemDataUtil.isItem(horse.getInventory().getSaddle(), "SADDLE_N_HORSESHOE")) e.setDamage(e.getDamage()*0.5f);
    }


    @EventHandler
    public void onGenerateLoot(LootGenerateEvent e) {
        for (ItemType itemType : ItemType.values())
            itemType.getItem().onLootGenerate(e);
    }


    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player player)) return;
        ItemStack item = e.getConsumable();
        ItemType type = ItemDataUtil.getItemType(item);
        if (type == null || !type.isArrow()) return;
        type.getItem().onArrowShoot(player, (Arrow) e.getProjectile());
        EntityDataUtil.setStringValue(e.getProjectile(), "arrowID", type.getItem().getID());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) return;
        if (!(e.getDamager() instanceof Arrow arrow)) return;
        String ID = EntityDataUtil.getStringValue(arrow, "arrowID");
        if (ID.isEmpty()) return;
        ItemType type = ItemType.valueOf(ID);
        e.setDamage(type.getItem().onArrowDamage(entity, (Player) arrow.getShooter(), arrow, e.getDamage()));
    }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent e) {
        String name = e.getInventory().getRenameText();
        if (name == null || name.isEmpty()) return;

        ItemStack item = e.getInventory().getItem(0);
        ItemStack sac = e.getInventory().getItem(1);

        if (item == null || sac == null) return;
        if (!ItemDataUtil.isItem(sac, ItemType.COLORED_INK_SAC.getItem().getID())) return;
        ItemMeta meta = item.getItemMeta();

        ItemStack result = item.clone();
        meta.setDisplayName(sac.getItemMeta().getDisplayName().substring(0, 2)+name);
        result.setItemMeta(meta);
        e.setResult(result);
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!(e.getInventory() instanceof AnvilInventory inv)) return;
        if (e.getSlot() != 2) return;

        ItemStack result = e.getCurrentItem();
        if (result == null) return;

        ItemStack item = inv.getItem(0);
        ItemStack sac = inv.getItem(1);

        if (item == null || sac == null) return;
        if (!ItemDataUtil.isItem(sac, ItemType.COLORED_INK_SAC.getItem().getID())) return;

        e.setCancelled(true);

        int cost = (item.getItemMeta() instanceof Repairable repairable) ? repairable.getRepairCost()+1 : 1;

        if (cost > player.getLevel()) {
            player.sendMessage(ChatColor.RED+"You need at least "+cost+" Levels to rename this item.");
            return;
        }
        if (e.getClick().isShiftClick()) {
            if (player.getInventory().firstEmpty() == -1) return;
            player.getInventory().addItem(result.clone());
        } else {
            if (e.getCursor().getType() != Material.AIR) return;
            player.setItemOnCursor(result.clone());
        }

        player.setLevel(player.getLevel()-cost);

        inv.setItem(0, new ItemStack(Material.AIR));
        inv.setItem(2, new ItemStack(Material.AIR));

        sac.setAmount(sac.getAmount()-1);

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
    }
}
