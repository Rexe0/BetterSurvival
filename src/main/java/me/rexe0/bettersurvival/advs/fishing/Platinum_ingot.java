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
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class Platinum_ingot extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "platinum_ingot");


  public Platinum_ingot(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.IRON_INGOT, "Platinum?", AdvancementFrameType.TASK, true, true, x, y , "Acquire your first platinum ingot"), parent, 1);
      registerEvent(FurnaceExtractEvent.class, e -> {
          if (ItemDataUtil.isItem(e.getItemStack(), ItemType.PLATINUM_INGOT.getItem().getID())) incrementProgression(e.getPlayer());
      });
  }
}