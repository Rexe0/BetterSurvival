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

public class Resonant_ingot extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "resonant_ingot");


  public Resonant_ingot(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.NETHER_BRICK, "Resonance", AdvancementFrameType.TASK, true, true, x, y , "Craft a Resonant Ingot using 2 Platinum Ingots and 2 Echo Shards"), parent, 1);

      registerEvent(CraftItemEvent.class, e -> {
          if (ItemDataUtil.isItem(e.getRecipe().getResult(), ItemType.RESONANT_INGOT.getItem().getID())) incrementProgression((Player) e.getWhoClicked());
      });
  }
}