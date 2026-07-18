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

public class Resonant_rod extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "resonant_rod");


  public Resonant_rod(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.FISHING_ROD, "Resonant Rod", AdvancementFrameType.TASK, true, true, x, y , "Craft a Resonant Fishing Rod using 3 Resonant Ingots and 2 String"), parent, 1);
      registerEvent(CraftItemEvent.class, e -> {
          if (ItemDataUtil.isItem(e.getRecipe().getResult(), ItemType.RESONANT_FISHING_ROD.getItem().getID())) incrementProgression((Player) e.getWhoClicked());
      });
  }
}