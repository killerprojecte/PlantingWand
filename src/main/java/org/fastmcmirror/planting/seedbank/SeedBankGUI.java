package org.fastmcmirror.planting.seedbank;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.utils.Color;
import org.fastmcmirror.planting.utils.ItemUtil;

public class SeedBankGUI {
    public static Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(new SeedBankHolder(), 9, Color.color(PlantingWand.lang.seedbank_title));
        ItemStack left = ItemUtil.buildItem(Material.GRAY_STAINED_GLASS_PANE, PlantingWand.lang.seedbank_left);
        ItemStack button = ItemUtil.buildItem(Material.LIME_STAINED_GLASS_PANE, PlantingWand.lang.seedbank_button);
        ItemStack right = ItemUtil.buildItem(Material.GRAY_STAINED_GLASS_PANE, PlantingWand.lang.seedbank_right);
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i, left);
        }
        for (int i = 5; i < 8; i++) {
            inventory.setItem(i, right);
        }
        inventory.setItem(8, button);
        return inventory;
    }
}
