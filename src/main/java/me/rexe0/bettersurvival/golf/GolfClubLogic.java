package me.rexe0.bettersurvival.golf;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.golf.GolfClub;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.HashMap;
import java.util.Map;

public class GolfClubLogic implements Listener {
    private static GolfClubLogic instance;
    public static GolfClubLogic getInstance() {
        if (instance == null)
            instance = new GolfClubLogic();
        return instance;
    }

    private final Map<Player, GolfClubMenu> golfMenu = new HashMap<>();


    @EventHandler
    public void onLoot(LootGenerateEvent e) {
        // Gives knowledge book for golf recipes from trial chamber pots, so long as at least one player nearby hasn't discovered them.

        String key = e.getLootTable().getKey().getKey();
        if (!key.equals("pots/trial_chambers/corridor")) return;

        Location location = e.getLootContext().getLocation();
        boolean hasGolfRecipes = true;
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) > 2500) continue;
            if (player.getDiscoveredRecipes().contains(new NamespacedKey(BetterSurvival.getInstance(), ItemType.WEDGE.getItem().getID()))) continue;

            boolean hasGolferKnowledge = false;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().isAir()) continue;
                if (ItemDataUtil.isItemName(item, ChatColor.DARK_AQUA+"Lost Golfer Knowledge")) {
                    hasGolferKnowledge = true;
                    break;
                }
            }
            if (!hasGolferKnowledge) {
                hasGolfRecipes = false;
                break;
            }
        }

        if (hasGolfRecipes) return;

        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA+"Lost Golfer Knowledge");
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.GOLF_CUP.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.GOLF_TEE.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.GOLF_HORN.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.GOLF_BALL.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.DRIVER.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.IRON.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.WEDGE.getItem().getID()));
        meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.PUTTER.getItem().getID()));
        item.setItemMeta(meta);

        if (RandomUtil.getRandom().nextBoolean()) return;

        e.getLoot().add(item);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        golfMenu.remove(e.getPlayer());

        GolfBallEntity golfBall = getGolfBall(e.getPlayer());
        if (golfBall != null) {
            golfBall.setItemsVisible(true);
            golfBall.remove();
        }
    }
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player player = e.getPlayer();
        ItemStack item = player.getEquipment().getItemInMainHand();

        ItemType type = ItemDataUtil.getItemType(item);
        if (!isValid(player, type)) return;
        e.setCancelled(true);

        if (!golfMenu.containsKey(player)) return;

        double progress = golfMenu.get(player).getProgress();

        GolfClub club = (GolfClub) type.getItem();

        GolfBallEntity golfBall = getGolfBall(player);
        golfBall.hit(club, progress);
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = e.getPlayer();

        GolfBallEntity golfBall = getGolfBall(player);
        if (golfBall == null || golfBall.getSpeedSquared() == 0) return;
        golfBall.setCamera(true);
        e.setCancelled(true);
    }


    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack item = player.getEquipment().getItemInMainHand();
            ItemType type = ItemDataUtil.getItemType(item);

            if (!isValid(player, type)) {
                golfMenu.remove(player);
                continue;
            }

            showGolfMenu(player);
        }
    }


    public GolfBallEntity getGolfBall(Player player) {
        for (GolfBallEntity golfBall : GolfBallEntity.getGolfBalls())
            if (golfBall.getOwner().equals(player))
                return golfBall;
        return null;
    }
    private boolean isValid(Player player, ItemType type) {
        GolfBallEntity golfBall = getGolfBall(player);
        return type != null && type.getItem() instanceof GolfClub && player.isSneaking()
                && golfBall != null && golfBall.canBeHit();
    }

    private void showGolfMenu(Player player) {
        golfMenu.putIfAbsent(player, new GolfClubMenu());
        GolfClubMenu menu = golfMenu.get(player);
        menu.run();
        menu.showUI(player);
    }

    public static class GolfClubMenu {
        private double progress;
        private boolean goingUp;

        public GolfClubMenu() {
            reset();
        }
        public double getProgress() {
            return progress;
        }

        public void run() {
            if (goingUp) {
                progress += 0.04;
                if (progress >= 1.0) {
                    progress = 1;
                    goingUp = false;
                }
            } else {
                progress -= 0.04;
                if (progress <= 0.0) {
                    progress = 0;
                    goingUp = true;
                }
            }


        }
        public void reset() {
            this.progress = 0;
            this.goingUp = true;
        }

        public void showUI(Player player) {
            String UI = "";
            for (int i = 0; i < 41; i++) {
                if ((int) (progress * 40) == i)
                    UI += ChatColor.BLUE+"|";
                else
                    UI += ItemDataUtil.getFormattedColorString("|", i, 40);
            }
            player.sendTitle(UI, "", 0, 2, 10);
        }
    }
}
