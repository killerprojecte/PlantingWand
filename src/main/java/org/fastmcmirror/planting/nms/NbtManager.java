package org.fastmcmirror.planting.nms;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class NbtManager {
    public abstract List<String> getWands(ItemStack item);

    public abstract ItemStack addWand(ItemStack item, String wand);

    public abstract ItemStack removeWand(ItemStack item, String wand);
}
