package me.rexe0.bettersurvival.item.constructs;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.constructs.Engine;
import me.rexe0.bettersurvival.constructs.Harness;
import me.rexe0.bettersurvival.constructs.Modification;
import me.rexe0.bettersurvival.constructs.ModificationType;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ConstructWorkshop extends Item {
    public static final NamespacedKey CONSTRUCT_WORKSHOP_KEY = new NamespacedKey(BetterSurvival.getInstance(), "CONSTRUCT_WORKSHOP");

    private final List<Modification> researched;
    public ConstructWorkshop() {
        this(new ArrayList<>());
        researched.add(Harness.NONE);
        researched.add(Engine.NONE);
    }
    public ConstructWorkshop(List<Modification> researched) {
        super(Material.RESPAWN_ANCHOR, ChatColor.GREEN+"Construct Workshop", "CONSTRUCT_WORKSHOP");
        this.researched = researched;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A workshop that can be placed");
        lore.add(ChatColor.GRAY+"down to create and tinker with");
        lore.add(ChatColor.GRAY+"Ghast Constructs.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setStringValue(item, "researchData", encodeResearchData(researched)));
        return item;
    }

    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        data.set(CONSTRUCT_WORKSHOP_KEY, PersistentDataType.STRING, ItemDataUtil.getStringValue(item, "researchData"));
        return false;
    }
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(CONSTRUCT_WORKSHOP_KEY, PersistentDataType.STRING)) {
            String str = data.get(CONSTRUCT_WORKSHOP_KEY, PersistentDataType.STRING);
            data.remove(CONSTRUCT_WORKSHOP_KEY);

            e.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), new ConstructWorkshop(decodeResearchData(str)).getItem());
        }
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("%%%", "#$#", "###");
        recipe.setIngredient('%', Material.IRON_BLOCK);
        recipe.setIngredient('$', Material.DRIED_GHAST);
        recipe.setIngredient('#', Material.CRYING_OBSIDIAN);
        return recipe;
    }


    public static List<Modification> decodeResearchData(String researchData) {
        List<Modification> researched = new ArrayList<>();
        for (ModificationType type : ModificationType.values()) {
            String section = researchData.substring(researchData.indexOf(type.name()) + type.name().length() + 1, researchData.indexOf('}', researchData.indexOf(type.name())));

            for (String id : section.split("_")) {
                if (id.isEmpty()) continue;
                int modId = Integer.parseInt(id);
                researched.add(type.getModification(modId));
            }
        }
        return researched;
    }
    public static String encodeResearchData(List<Modification> researched) {
        StringBuilder builder = new StringBuilder();
        for (ModificationType type : ModificationType.values()) {
            builder.append(type.name()).append("{");
            builder.append(researched.stream()
                    .filter(m -> type.getModificationClass().isInstance(m))
                    .reduce("", (s, m) -> s +"_"+ m.getId(), String::concat));
            builder.append("}");
        }
        return builder.toString();
    }
}
