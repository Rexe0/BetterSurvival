package me.rexe0.bettersurvival.farming;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.BlockOfCocaine;
import me.rexe0.bettersurvival.item.drugs.CocaLeaves;
import me.rexe0.bettersurvival.item.drugs.Cocaine;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.Season;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Crafter;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CocaineListener implements Listener {
    public static final NamespacedKey COCAINE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "COCAINE_POTENCY");

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        ItemStack[] items = e.getInventory().getMatrix();
        ItemStack item;

        for (int i = 0; i < 3; i++) {
            item = switch (i) {
                default -> cocaineCraft(e.getRecipe(), items);
                case 1 -> blockOfCocaineCraft(e.getRecipe(), items);
                case 2 -> cocaineBlockCraft(e.getRecipe(), items);
            };
            if (item != null) {
                e.getInventory().setResult(item);
                return;
            }
        }
    }

    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent e) {
        ItemStack[] items = ((Crafter) e.getBlock().getState()).getInventory().getContents();
        ItemStack item;
        for (int i = 0; i < 3; i++) {
            item = switch (i) {
                default -> cocaineCraft(e.getRecipe(), items);
                case 1 -> blockOfCocaineCraft(e.getRecipe(), items);
                case 2 -> cocaineBlockCraft(e.getRecipe(), items);
            };
            if (item != null) {
                e.setResult(item);
                return;
            }
        }
    }

    private ItemStack cocaineCraft(Recipe recipe, ItemStack[] items) {
        if (recipe == null) return null;
        if (!ItemDataUtil.isItem(recipe.getResult(), ItemType.COCAINE.getItem().getID())) return null;
        if (recipe.getResult().getAmount() > 1) return null;

        int averagePotency = 0;
        for (ItemStack item : items)
            if (ItemDataUtil.isItem(item, ItemType.COCA_LEAVES.getItem().getID()))
                averagePotency += ItemDataUtil.getIntegerValue(item, "potency");

        averagePotency /= 5;
        return new Cocaine(averagePotency).getItem();
    }
    private ItemStack blockOfCocaineCraft(Recipe recipe, ItemStack[] items) {
        if (recipe == null) return null;
        if (!ItemDataUtil.isItem(recipe.getResult(), ItemType.BLOCK_OF_COCAINE.getItem().getID())) return null;

        int averagePotency = 0;
        for (ItemStack item : items)
            if (ItemDataUtil.isItem(item, ItemType.COCAINE.getItem().getID()))
                averagePotency += ItemDataUtil.getIntegerValue(item, "potency");

        averagePotency /= 9;
        return new BlockOfCocaine(averagePotency).getItem();
    }

    private ItemStack cocaineBlockCraft(Recipe recipe, ItemStack[] items) {
        if (recipe == null) return null;
        if (!ItemDataUtil.isItem(recipe.getResult(), ItemType.COCAINE.getItem().getID())) return null;
        if (recipe.getResult().getAmount() != 9) return null;

        for (ItemStack item : items)
            if (ItemDataUtil.isItem(item, ItemType.BLOCK_OF_COCAINE.getItem().getID())) {
                ItemStack cocaine = new Cocaine(ItemDataUtil.getIntegerValue(item, "potency")).getItem();
                cocaine.setAmount(9);
                return cocaine;
            }
        return null;
    }



    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(COCAINE_KEY, PersistentDataType.INTEGER)) {
            e.setDropItems(false);
            int potency = data.get(COCAINE_KEY, PersistentDataType.INTEGER);

            data.remove(COCAINE_KEY);
            ItemStack item = new ItemStack(Material.AIR);
            if (block.getType() == Material.OAK_LEAVES) {
                item = new CocaLeaves(potency).getItem();
            } else if (block.getType() == Material.CALCITE) {
                item = new BlockOfCocaine(potency).getItem();
            }
            block.getWorld().dropItemNaturally(block.getLocation(), item);
        }
    }

    public static void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block;
            for (int i = 0; i < 80; i++) {
                Location loc = player.getLocation().add(
                        RandomUtil.getRandom().nextInt(-64, 64),
                        RandomUtil.getRandom().nextInt(-64, 64),
                        RandomUtil.getRandom().nextInt(-64, 64));
                block = loc.getBlock();
                if (block.getLightLevel() < 5) continue;
                if (block.getType() == Material.OAK_LEAVES && block.getRelative(0, 1, 0).getType() == Material.AIR) {
                    PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

                    if (data.has(COCAINE_KEY, PersistentDataType.INTEGER)) {
                        int potency = data.get(COCAINE_KEY, PersistentDataType.INTEGER);
                        int change = switch (Season.getSeason()) {
                            default -> 3;
                            case AUTUMN -> 2;
                            case WINTER -> 1;
                        };

                        int length = getCocaLength(block);
                        if (length >= 5) continue;

                        int fertilizerTier = 0;
                        if (data.has(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER)) {
                            fertilizerTier = data.get(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER);
                            data.remove(HarvestModifier.BONEMEAL_KEY);
                        }

                        change += fertilizerTier;

                        Biome biome = ((CraftWorld) block.getWorld()).getHandle().getBiome(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).value();
                        if (biome.climateSettings.temperature() >= 0.8f) change++;
                        if (biome.climateSettings.downfall() >= 0.9f) change++;

                        potency += RandomUtil.getRandom().nextInt(change-6, change);
                        potency = Math.min(64+change*4, Math.max(0, potency));

                        setCocaPlant(block, potency, data);

                        if (length >= 3) {
                            potency /= 3;
                            if (length == 3) potency *= 2;
                        }

                        block = block.getRelative(0, 1, 0);
                        data = new CustomBlockData(block, BetterSurvival.getInstance());
                        data.set(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER, fertilizerTier);
                        setCocaPlant(block, potency, data);
                    }
                }
            }
        }
    }
    private static void setCocaPlant(Block block, int potency, PersistentDataContainer data) {
        block.setType(Material.OAK_LEAVES);
        Leaves leaves = (Leaves) block.getBlockData();
        leaves.setPersistent(true);
        block.setBlockData(leaves);

        data.set(COCAINE_KEY, PersistentDataType.INTEGER, potency);
    }

    public static int getCocaLength(Block block) {
        int length = 1;
        for (int j = 1; j < 5; j++) {
            if (block.getRelative(0, -j, 0).getType() == Material.OAK_LEAVES
                    && (new CustomBlockData(block, BetterSurvival.getInstance())).has(COCAINE_KEY, PersistentDataType.INTEGER)) length++;
            else break;
        }
        return length;
    }
}