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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.Wand;
import org.fastmcmirror.planting.utils.Color;
import org.fastmcmirror.planting.utils.MessageType;
import org.fastmcmirror.planting.utils.ParticleUtil;

public class PlantListener implements Listener {
    private static long plantBlocks(Location location, Wand wand, Player player) {
        int range = wand.range;
        Material plant = wand.plant;
        long amount = 0L;
        if (!PlantingWand.economics.containsKey(wand.payment.type)) {
            player.sendMessage(Color.color(PlantingWand.lang.unknow_payment));
            return 0L;
        }
        all:
        for (int x = location.getBlockX() - range; x <= location.getBlockX() + range; x++) {
            for (int z = (int) (location.getBlockZ() - range); z <= location.getBlockZ() + range; z++) {
                Location under = new Location(location.getWorld(), x, location.getBlockY(), z);
                if (!location.getWorld().getBlockAt(under).getType().equals(wand.farmblock)) continue;
                Location location1 = new Location(location.getWorld(), x + wand.offset.x, location.getBlockY() + wand.offset.y, z + wand.offset.z);
                if (!location.getWorld().getBlockAt(location1).getType().equals(Material.AIR)) continue;
                if (!PlantingWand.economics.get(wand.payment.type).has(player, wand.payment.count, wand)) {
                    player.sendMessage(Color.color(PlantingWand.lang.not_enough));
                    break all;
                }
                Bukkit.getScheduler().runTask(PlantingWand.instance, () -> {
                    if (wand.model && PlantingWand.modelEngine != null) {
                        PlantingWand.modelEngine.placeModelBlock(location1, wand.modelid, player);
                    } else {
                        location.getWorld().getBlockAt(location1).setType(plant);
                    }
                    if (wand.particle) {
                        ParticleUtil.playParticle(wand.particleType, location.getWorld().getBlockAt(location1));
                    }
                });
                amount++;
            }
        }
        long finalAmount = amount;
        Bukkit.getScheduler().runTask(PlantingWand.instance, () -> PlantingWand.economics.get(wand.payment.type).takeMoney(player, wand.payment.count * finalAmount, wand));
        return amount;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        String display = null;
        ItemStack item = player.getItemInHand();
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (PlantingWand.instance.getConfig().getBoolean("nbtmode")) {
            for (String tag : PlantingWand.nbtManager.getWands(item)) {
                if (!PlantingWand.wands.containsKey(tag)) continue;
                display = tag;
            }
            if (display == null) return;
        } else {
            if (!meta.hasDisplayName()) return;
            display = meta.getDisplayName().replace("§", "&");
        }
        if (!PlantingWand.wands.containsKey(display)) return;
        Location location = event.getClickedBlock().getLocation();
        if (location.getBlockY() >= 256) return;
        Wand wand = PlantingWand.wands.get(display);
        if (!event.getClickedBlock().getType().equals(wand.farmblock)) return;
        if (!player.hasPermission(wand.permission)) return;
        if (wand.cooldown != 0L) {
            if (wand.cooldowns.containsKey(player.getUniqueId())) {
                if (wand.cooldowns.get(player.getUniqueId()) != 0L) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Color.color(
                            PlantingWand.lang.cooldown.replace("%cooldown%", String.valueOf(wand.cooldowns.get(player.getUniqueId())))
                    )));
                    return;
                }
            }
        }
        if (wand.disposable) {
            item.setAmount(item.getAmount() - 1);
        }
        Bukkit.getScheduler().runTaskAsynchronously(PlantingWand.instance, () -> {
            if (wand.cooldowns.containsKey(player.getUniqueId())) return;
            long amount = plantBlocks(location, wand, player);
            int count = (int) (amount * wand.payment.count);
            if (wand.messageType.equals(MessageType.ACTIONBAR)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Color.color(wand.message.replace("%amount%", String.valueOf(amount)).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%count%", String.valueOf(amount * wand.payment.count))
                        .replace("%int_count%", String.valueOf(count)))));
            } else {
                player.sendMessage(Color.color(wand.message.replace("%amount%", String.valueOf(amount)).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%count%", String.valueOf(amount * wand.payment.count))
                        .replace("%int_count%", String.valueOf(count))));
            }
            if (wand.cooldown != 0L) {
                wand.cooldowns.put(player.getUniqueId(), wand.cooldown);
                Bukkit.getScheduler().runTaskLaterAsynchronously(PlantingWand.instance, () -> {
                    wand.cooldowns.remove(player.getUniqueId());
                }, 20L * wand.cooldown);
            }
        });
    }
}
