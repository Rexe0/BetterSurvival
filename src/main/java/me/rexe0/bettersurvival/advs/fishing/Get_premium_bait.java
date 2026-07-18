package me.rexe0.bettersurvival.advs.fishing;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;

public class Get_premium_bait extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "get_premium_bait");


  public Get_premium_bait(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.MELON_SEEDS, "$ Premium $", AdvancementFrameType.TASK, true, true, x, y , "Craft Premium Bait using a Golden Carrot and a Baked Potato"), parent, 1);

      registerEvent(CraftItemEvent.class, e -> {
          if (ItemDataUtil.isItem(e.getRecipe().getResult(), ItemType.PREMIUM_BAIT.getItem().getID())) incrementProgression((Player) e.getWhoClicked());
      });
  }
}