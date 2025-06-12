package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class BookOfBrewery extends Item {
    public BookOfBrewery() {
        super(Material.WRITTEN_BOOK, ChatColor.GREEN+"Book of Brewery", "BOOK_OF_BREWERY");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A book containing written knowledge");
        lore.add(ChatColor.GRAY+"on how to brew, distill and age");
        lore.add(ChatColor.GRAY+"alcoholic drinks.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setAuthor("???");
        meta.setGeneration(BookMeta.Generation.TATTERED);
        meta.setTitle("Book of Brewery");
        meta.setPages(ChatColor.BOLD+"Reinforced Barrels\n\n"+ChatColor.RESET+"A special type of barrels crafted from 6 wood planks, 2 iron ingots and a barrel. It is needed for all processes related to brewing, distilling and aging.",
                      ChatColor.BOLD+"Fermentation\n\n"+ChatColor.RESET+"To brew alcohol, you must place water bottles, yeast and the ingredient of your choice into a Reinforced Barrel. Yeast is occasionally sold by Wandering Traders. The ingredient used must contain sugar or starch.",
                              "The fermentation process can take multiple in-game days. The longer you leave it, the stronger the alcohol will be. Once reaching about 15% ABV, the yeast will begin to die and will no longer be able to ferment any longer.",
                      ChatColor.BOLD+"Distillation\n\n"+ChatColor.RESET+"To distill alcohol, you must place a Reinforced Barrel on top of a Campfire. The barrel must contain at least 1 empty bottle (do not stack it) and up to 4 bottles of alcohol per empty bottle. ",
                                "After some time, the alcohol will be distilled into the empty bottle creating a concentrated spirit distillate with an ABV dependent on the alcohol used.",
                              "Be careful when distilling alcohol as there is a chance the distillate may contain a high concentration of methanol which can cause blindness and death if consumed.",
                      ChatColor.BOLD+"Aging\n\n"+ChatColor.RESET+"To age alcohol, you must place bottles of alcohol of the same type into a Reinforced Barrel. This process can take in-game months but will result in a more concentrated drink with a higher sell price.",
                                "The aging process can also introduce additional secondary and tertiary flavors depending on the wood of the barrel and its previously fermented products.",
                                "Aging spirit distillates alongside an ingredient will introduce a primary flavor into it based on the ingredient used. Fruits create Brandy, sugar creates Rum and grains create Whiskey.",
                      ChatColor.BOLD+"Uses\n\n"+ChatColor.RESET+"Alcoholic drinks can be consumed to suppress hunger, with the duration of this effect increasing with the alcohol concentration.",
                "They can also be sold to Nitwit Villagers for emeralds, with the price depending on the quality and flavors of the drink. Some villagers will make specific requests and will reward additional emeralds if you fulfill them.");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().getKey();
        if (key.equals("chests/shipwreck_treasure"))
            if (RandomUtil.getRandom().nextBoolean()) e.getLoot().add(getItem());
    }
}
