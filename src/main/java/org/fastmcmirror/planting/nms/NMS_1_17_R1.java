package org.fastmcmirror.planting.nms;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NMS_1_17_R1 extends NbtManager {
    @Override
    public List<String> getWands(ItemStack item) {
        net.minecraft.world.item.ItemStack i = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = i.getOrCreateTag();
        List<String> list = new ArrayList<>();
        if (nbt.hasKey("PlantingWand")) {
            NBTTagList nlist = nbt.getList("PlantingWand", 8);
            for (NBTBase n : nlist) {
                list.add(n.asString());
            }
        }
        return list;
    }

    @Override
    public ItemStack addWand(ItemStack item, String buff) {
        net.minecraft.world.item.ItemStack i = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = i.getOrCreateTag();
        NBTTagList nlist;
        if (nbt.hasKey("PlantingWand")) {
            nlist = nbt.getList("PlantingWand", 8);
            nlist.add(NBTTagString.a(buff));
        } else {
            nlist = new NBTTagList();
            nlist.add(NBTTagString.a(buff));
        }
        nbt.set("PlantingWand", nlist);
        i.setTag(nbt);
        return CraftItemStack.asBukkitCopy(i);
    }

    @Override
    public ItemStack removeWand(ItemStack item, String buff) {
        net.minecraft.world.item.ItemStack i = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = i.getOrCreateTag();
        NBTTagList nlist;
        NBTTagList list = new NBTTagList();
        if (nbt.hasKey("PlantingWand")) {
            nlist = nbt.getList("PlantingWand", 8);
            for (int s = 0; s < nlist.size(); s++) {
                String str = nlist.getString(s);
                if (str.equals(buff)) continue;
                list.add(NBTTagString.a(str));
            }
        }
        nbt.set("PlantingWand", list);
        i.setTag(nbt);
        return CraftItemStack.asBukkitCopy(i);
    }
}
