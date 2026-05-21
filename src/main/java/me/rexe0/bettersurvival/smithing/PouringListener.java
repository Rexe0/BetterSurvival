package me.rexe0.bettersurvival.smithing;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.smithing.SmithingType;
import me.rexe0.bettersurvival.util.InventoryUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PouringListener implements Listener {
    private static PouringListener instance;

    public static PouringListener getInstance() {
        if (instance == null) instance = new PouringListener();
        return instance;
    }


    private Map<UUID, PouringMinigame> minigameMap = new HashMap<>();


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (minigameMap.containsKey(uuid))
            minigameMap.get(uuid).end();
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        if (minigameMap.containsKey(uuid))
            minigameMap.get(uuid).fail();
    }


    @EventHandler
    public void onTakeMoltenMetal(PlayerInteractEvent e) {
        if (e.useInteractedBlock() == Event.Result.DENY || e.useItemInHand() == Event.Result.DENY) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;
        Block block = e.getClickedBlock();
        if (block == null || block.getType() != Material.LAVA_CAULDRON) return;
        if (e.getItem() == null) return;
        ItemStack item = e.getItem();
        if (!ItemDataUtil.isItem(item, ItemType.CASTING_MOLD.getItem().getID())) return;

        SmithingType type = SmithingType.valueOf(ItemDataUtil.getStringValue(item, "smithingType"));


        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        Map<SmithingOre, Integer> oreCounts = new HashMap<>();
        for (SmithingOre ore : SmithingOre.values()) {
            int amount = data.getOrDefault(SmeltingListener.getOreKey(ore), PersistentDataType.INTEGER, 0);
            if (amount > 0)
                oreCounts.put(ore, amount);
            data.remove(SmeltingListener.getOreKey(ore));
        }

        block.setType(Material.CAULDRON);

        PouringMinigame minigame = new PouringMinigame(e.getPlayer(), 2, type, oreCounts);
        minigame.start();
    }
    @EventHandler
    public void onPourClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY+"Pouring Molten Metal")) return;
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (!minigameMap.containsKey(uuid)) return;
        e.setCancelled(true);
        minigameMap.get(uuid).onClick();
    }
    @EventHandler
    public void onClaimClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY+"Forged Item")) return;
        e.setCancelled(true);

        Inventory inv = e.getClickedInventory();
        if (!inv.equals(e.getView().getTopInventory())) return;
        if (e.getSlot() != 22) return;
        ItemStack item = inv.getItem(22);

        Player player = (Player) e.getWhoClicked();

        giveItem(player, item);

        inv.setItem(22, null);
        player.closeInventory();
    }

    @EventHandler
    public void onClaimClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY+"Forged Item")) return;

        Inventory inv = e.getInventory();
        ItemStack item = inv.getItem(22);
        if (item == null) return;

        giveItem((Player) e.getPlayer(), item);
    }

    private void giveItem(Player player, ItemStack item) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2f);

        if (!hasFullInventory(player)) {
            player.getInventory().addItem(item);
            return;
        }

        Item itemEntity = player.getWorld().dropItem(player.getLocation(), item);
        itemEntity.setPickupDelay(0);
        itemEntity.setCanMobPickup(false);
        itemEntity.setOwner(player.getUniqueId());
        itemEntity.setHealth(1000);
    }

    private boolean hasFullInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) return false;
        }
        return true;
    }

    private class PouringMinigame extends BukkitRunnable {
        private final ItemStack GRAY_PANE;
        private final ItemStack ORANGE_PANE;
        private final ItemStack RED_PANE;

        private final Player player;
        private final SmithingType type;
        private final Map<SmithingOre, Integer> oreCounts;
        private final Inventory inv;
        private final int speed; // Lower is faster - number of ticks between each movement of the panes

        private List<Integer> previousPanes;

        private int stage;
        private int paneCount;
        private int panePos;

        private boolean goingRight;

        private boolean isStopped;

        private int i;

        public PouringMinigame(Player player, int speed, SmithingType type, Map<SmithingOre, Integer> oreCounts) {
            this.player = player;
            this.speed = speed;
            this.type = type;
            this.oreCounts = oreCounts;
            this.previousPanes = new ArrayList<>();
            this.panePos = 0;
            this.paneCount = 3;
            this.goingRight = true;
            this.inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY+"Pouring Molten Metal");


            GRAY_PANE = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = GRAY_PANE.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"Click anywhere to pour!");
            GRAY_PANE.setItemMeta(meta);

            ORANGE_PANE = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
            meta = ORANGE_PANE.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"Click anywhere to pour!");
            ORANGE_PANE.setItemMeta(meta);
            RED_PANE = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            meta = RED_PANE.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"Click anywhere to pour!");
            RED_PANE.setItemMeta(meta);
        }

        @Override
        public void run() {
            if (i % speed == 0 && !isStopped)
                movePanes();

            if (!player.getOpenInventory().getTitle().equals(ChatColor.DARK_GRAY+"Pouring Molten Metal")) {
                player.openInventory(inv);
                return;
            }

            i++;
        }
        private void movePanes() {
            if (goingRight) {
                panePos++;
                if (panePos+paneCount >= 9) goingRight = false;
            } else {
                panePos--;
                if (panePos <= 0) goingRight = true;
            }


            for (int i = 0; i < 9; i++) {
                if (i >= panePos && i < panePos+paneCount)
                    inv.setItem(stage*9 + i, ORANGE_PANE);
                else
                    inv.setItem(stage*9 + i, GRAY_PANE);
            }
        }

        public void onClick() {
            List<Integer> originalPanePositions = new ArrayList<>();
            for (int i = 0; i < paneCount; i++)
                originalPanePositions.add(i+panePos);

            List<Integer> nextPanePositions = new ArrayList<>(originalPanePositions);
            if (stage != 0) {
                nextPanePositions.retainAll(previousPanes);

                for (int i = 0; i < 9; i++) {
                    if (nextPanePositions.contains(i))
                        inv.setItem(stage*9 + i, ORANGE_PANE);
                    else if (originalPanePositions.contains(i))
                        inv.setItem(stage*9 + i, RED_PANE);
                    else
                        inv.setItem(stage*9 + i, GRAY_PANE);
                }
                if (nextPanePositions.size() != paneCount && (stage != 5 || nextPanePositions.isEmpty())) {
                    isStopped = true;
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1+stage*0.1f);

                    new BukkitRunnable() {
                        private int i;
                        @Override
                        public void run() {
                            if (i > 6) {
                                isStopped = false;
                                advanceStage(nextPanePositions);
                                cancel();
                                return;
                            }
                            if (i % 2 == 0)
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.8f);
                            for (int i = 0; i < 9; i++)
                                if (!nextPanePositions.contains(i) && originalPanePositions.contains(i))
                                    inv.setItem(stage * 9 + i, this.i % 2 == 0 ? GRAY_PANE : RED_PANE);

                            i++;
                        }
                    }.runTaskTimer(BetterSurvival.getInstance(), 4, 4);
                    return;
                }
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1+stage*0.1f);
            advanceStage(nextPanePositions);

        }

        private void advanceStage(List<Integer> nextPanePositions) {
            if (nextPanePositions.isEmpty()) {
                fail();
                return;
            }

            paneCount = nextPanePositions.size();
            goingRight = true;
            panePos = RandomUtil.getRandom().nextInt(0, 10-paneCount);
            previousPanes = nextPanePositions;
            stage++;

            if (stage == 6) {
                win();
                return;
            }
        }

        public void start() {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, GRAY_PANE);
            }

            player.openInventory(inv);
            minigameMap.put(player.getUniqueId(), this);

            runTaskTimer(BetterSurvival.getInstance(), 0, 1);
        }

        public void win() {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);

            createGrantItemAnimation(player);

            end();
        }
        public void fail() {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0);
            player.sendMessage(ChatColor.RED+"You failed to pour the molten metal correctly!");
            end();
        }
        public void end() {
            if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_GRAY+"Pouring Molten Metal"))
                player.closeInventory();

            minigameMap.remove(player.getUniqueId());
            cancel();
        }


        private void createGrantItemAnimation(Player player) {
            Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Forged Item");
            for (int i = 0; i < 54; i++)
                inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

            ItemStack item = type.getItem(oreCounts);
            inv.setItem(22, item);


            player.openInventory(inv);
            new BukkitRunnable() {
                private int i = 0;

                @Override
                public void run() {
                    switch (i) {
                        case 0 -> {
                            int[] integers = new int[]{
                                    12, 13, 14, 21, 30, 31, 32, 23
                            };
                            ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                            for (int j : integers)
                                inv.setItem(j, item);

                        }
                        case 1 -> {
                            int[] integers = new int[]{
                                    2, 3, 4, 5, 6, 11, 20, 29, 38, 39, 40, 41, 42, 15, 24, 33
                            };
                            ItemStack item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);

                            for (int j : integers)
                                inv.setItem(j, item);
                        }
                        case 2 -> {
                            int[] integers = new int[]{
                                    1, 10, 19, 28, 37, 46, 47, 48, 49, 50, 51, 52, 7, 16, 25, 34, 43
                            };

                            ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                            for (int j : integers)
                                inv.setItem(j, item);
                        }
                        case 3 -> {
                            List<Integer> integers = new ArrayList<>(InventoryUtil.getRightSide());
                            integers.addAll(InventoryUtil.getLeftSide());

                            ItemStack item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
                            for (int j : integers)
                                inv.setItem(j, item);
                            cancel();
                            return;
                        }
                    }
                    i++;
                }
            }.runTaskTimer(BetterSurvival.getInstance(), 1, 5);
        }
    }
}
