package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class AnAncientEvil extends Item {
    public AnAncientEvil() {
        super(Material.WRITTEN_BOOK, ChatColor.GREEN+"An Ancient Evil", "AN_ANCIENT_EVIL");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A book containing written knowledge");
        lore.add(ChatColor.GRAY+"on the Primeval Wither.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setAuthor("???");
        meta.setGeneration(BookMeta.Generation.TATTERED);
        meta.setTitle("An Ancient Evil");
        meta.setPages("Legends tell of an ancient evil hailing from the nether. A species of Wither that was so powerful, it could destroy entire villages with ease.",
                      "This Primeval Wither forged a ring with powerful nether magic that allowed it to focus its power into explosive and destructive force.",
                      "To summon this powerful being, one must create a Wither whilst under the influence of a Bad Omen. However, one must make sure they are properly equipped to deal with an enemy of such power.");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().getKey();

        if (key.equals("chests/nether_bridge")) {
            if (RandomUtil.getRandom().nextInt(3) == 0)
                e.getLoot().add(getItem());
        }
    }
}
