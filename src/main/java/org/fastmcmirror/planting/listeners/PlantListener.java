package org.fastmcmirror.planting.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.Wand;
import org.fastmcmirror.planting.utils.Color;
import org.fastmcmirror.planting.utils.MessageType;

public class PlantListener implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(Material.FARMLAND)) return;
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;
        String display = meta.getDisplayName().replace("§","&");
        if (!PlantingWand.wands.containsKey(display)) return;
        Location location = event.getClickedBlock().getLocation();
        if (location.getBlockY()>=256) return;
        Wand wand = PlantingWand.wands.get(display);
        if (!player.hasPermission(wand.permission)) return;
        if (wand.cooldown!=0L){
            if (wand.cooldowns.containsKey(player.getUniqueId())){
                if (wand.cooldowns.get(player.getUniqueId())!=0L){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(Color.color(
                            "&c您还需要等待 &e" + wand.cooldowns.get(player.getUniqueId())
                                    + "秒 &c才能使用此魔杖!"
                    )));
                    return;
                }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(PlantingWand.instance,() -> {
            long amount = plantBlocks(location,wand);
            if (wand.messageType.equals(MessageType.ACTIONBAR)){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(Color.color(wand.message.replace("%amount%",String.valueOf(amount)).replace("%item%",PlantingWand.iapi.getItemName(wand.plant.toString())))));
            } else {
                player.sendMessage(Color.color(wand.message.replace("%amount%",String.valueOf(amount)).replace("%item%",PlantingWand.iapi.getItemName(wand.plant.toString()))));
            }
            wand.cooldowns.put(player.getUniqueId(),wand.cooldown);
            Bukkit.getScheduler().runTaskLaterAsynchronously(PlantingWand.instance,() -> {
                wand.cooldowns.remove(player.getUniqueId());
            },20L * wand.cooldown);
        });
    }

    private static long plantBlocks(Location location,Wand wand){
        int range = wand.range;
        Material plant = wand.plant;
        long amount = 0L;
        for(int x = location.getBlockX() - range; x <= location.getBlockX() + range; x++) {
            for(int z = (int) (location.getBlockZ() - range); z <= location.getBlockZ() + range; z++) {
                Location under = new Location(location.getWorld(),x,location.getBlockY(),z);
                if (!location.getWorld().getBlockAt(under).getType().equals(Material.FARMLAND)) continue;
                Location location1 = new Location(location.getWorld(),x,location.getBlockY() + 1,z);
                if (!location.getWorld().getBlockAt(location1).getType().equals(Material.AIR)) continue;
                Bukkit.getScheduler().runTask(PlantingWand.instance,() -> {
                    location.getWorld().getBlockAt(location1).setType(plant);
                });
                amount++;
            }
        }
        return amount;
    }
}
