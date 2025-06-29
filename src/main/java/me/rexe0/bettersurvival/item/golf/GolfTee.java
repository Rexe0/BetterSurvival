package me.rexe0.bettersurvival.item.golf;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.golf.GolfBallSpawner;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GolfTee extends Item {
    private static final double SCALE = 0.25;
    public GolfTee() {
        super(Material.HOPPER, ChatColor.GREEN+"Golf Tee", "GOLF_TEE");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A small stand used to hold");
        lore.add(ChatColor.GRAY+"the golf ball.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        Location loc = block.getLocation().add(0.5, 0, 0.5);

        for (BlockDisplay display : loc.getWorld().getEntitiesByClass(BlockDisplay.class)) {
            if (display.getLocation().distanceSquared(loc) < 1) {
                player.sendMessage(ChatColor.RED+"There is already a golf tee here!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 0);
                return true;
            }
        }

        if (!loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) return true;

        BlockDisplay display = loc.getWorld().spawn(loc, BlockDisplay.class);
        display.setBlock(Material.HOPPER.createBlockData());
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(SCALE, SCALE, SCALE);
        transformation.getTranslation().set(-SCALE/2, 0, -SCALE/2);
        display.setTransformation(transformation);

        Interaction interaction = loc.getWorld().spawn(loc, Interaction.class);
        interaction.setInteractionHeight(0.35f);
        interaction.setInteractionWidth(0.35f);
        interaction.setResponsive(true);
        interaction.addScoreboardTag("golfTee");
        EntityDataUtil.setStringValue(interaction, "golfTeeUUID", display.getUniqueId().toString());

        item.setAmount(item.getAmount()-1);
        player.playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1, 1);
        return true;
    }

    public static void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Interaction interaction : world.getEntitiesByClass(Interaction.class)) {
                if (!interaction.getScoreboardTags().contains("golfTee")) continue;

                Location loc = interaction.getLocation();
                if (!loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                    remove(interaction);
                    continue;
                }

                // On right click, place a golf ball if possible
                if (interaction.getLastInteraction() != null) {
                    Interaction.PreviousInteraction prev = interaction.getLastInteraction();
                    long prevTime = prev.getTimestamp();

                    if (EntityDataUtil.getLongValue(interaction, "lastInteract") == prevTime) continue;
                    EntityDataUtil.setLongValue(interaction, "lastInteract", prevTime);

                    Player player = interaction.getLastInteraction().getPlayer().getPlayer();
                    if (player == null || !player.isOnline()) continue;
                    ItemStack item = player.getEquipment().getItemInMainHand();
                    if (!ItemDataUtil.isItem(item, ItemType.GOLF_BALL.getItem().getID())) continue;

                    BlockDisplay tee = (BlockDisplay) Bukkit.getEntity(UUID.fromString(EntityDataUtil.getStringValue(interaction, "golfTeeUUID")));
                    if (tee != null) {
                        item.setAmount(item.getAmount()-1);
                        GolfBallSpawner.spawnGolfBall(player, tee);
                    }
                    continue;
                }

                // On left click, break it
                if (interaction.getLastAttack() != null) {
                    Player player = interaction.getLastAttack().getPlayer().getPlayer();
                    if (player == null || !player.isOnline()) continue;
                    if (player.getGameMode() == GameMode.ADVENTURE) continue;
                    remove(interaction);
                    continue;
                }
            }
        }
    }

    private static void remove(Interaction interaction) {
        Location loc = interaction.getLocation();
        loc.getWorld().dropItemNaturally(loc, new GolfTee().getItem());
        loc.getWorld().playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1, 0);

        interaction.remove();
        BlockDisplay tee = (BlockDisplay) Bukkit.getEntity(UUID.fromString(EntityDataUtil.getStringValue(interaction, "golfTeeUUID")));
        if (tee != null)
            tee.remove();
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setGroup("GOLF");
        return recipe;
    }
}
