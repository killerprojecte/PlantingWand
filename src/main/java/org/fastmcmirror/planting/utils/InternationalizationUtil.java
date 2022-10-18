package org.fastmcmirror.planting.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.planting.PlantingWand;

public class InternationalizationUtil {
    public static String getDisplayName(ItemStack item) {
        if (item.getType().equals(Material.AIR)) return PlantingWand.iapi.getItemName(item.getType().toString());
        if (!item.hasItemMeta()) return PlantingWand.iapi.getItemName(item.getType().toString());
        if (!item.getItemMeta().hasDisplayName()) return PlantingWand.iapi.getItemName(item.getType().toString());
        return item.getItemMeta().getDisplayName();
    }
}
