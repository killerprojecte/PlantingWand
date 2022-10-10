package org.fastmcmirror.planting.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Crops;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.Wand;
import org.fastmcmirror.planting.utils.Color;
import org.fastmcmirror.planting.utils.MessageType;
import org.fastmcmirror.planting.utils.ParticleUtil;

public class LevelUpListener implements Listener {
    private static long levelupPlants(Location location, Wand wand) {
        int range = wand.range;
        long amount = 0L;
        for (int x = location.getBlockX() - range; x <= location.getBlockX() + range; x++) {
            for (int z = (int) (location.getBlockZ() - range); z <= location.getBlockZ() + range; z++) {
                Location location1 = new Location(location.getWorld(), x, location.getBlockY(), z);
                if (!location.getWorld().getBlockAt(location1).getType().equals(wand.plant)) continue;
                Block block = location.getWorld().getBlockAt(location1);
                BlockState state = block.getState();
                Crops crops = (Crops) state.getData();
                if (crops.getState().equals(CropState.RIPE)) continue;
                Bukkit.getScheduler().runTask(PlantingWand.instance, () -> {
                    CropState cropState = CropState.SEEDED;
                    switch (crops.getState()) {
                        case SEEDED: {
                            cropState = CropState.GERMINATED;
                            break;
                        }
                        case GERMINATED: {
                            cropState = CropState.VERY_SMALL;
                            break;
                        }
                        case VERY_SMALL: {
                            cropState = CropState.SMALL;
                            break;
                        }
                        case SMALL: {
                            cropState = CropState.MEDIUM;
                            break;
                        }
                        case MEDIUM: {
                            cropState = CropState.TALL;
                            break;
                        }
                        case TALL: {
                            cropState = CropState.VERY_TALL;
                            break;
                        }
                        case VERY_TALL: {
                            cropState = CropState.RIPE;
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    crops.setState(cropState);
                    state.setData(crops);
                    state.update();
                    if (wand.particle) {
                        ParticleUtil.playParticle(wand.particleType, block);
                    }
                });
                amount++;
            }
        }
        return amount;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;
        String display = meta.getDisplayName().replace("§", "&");
        if (!PlantingWand.levelup.containsKey(display)) return;
        Location location = event.getClickedBlock().getLocation();
        if (location.getBlockY() >= 256) return;
        Wand wand = PlantingWand.levelup.get(display);
        if (!event.getClickedBlock().getType().equals(wand.plant)) return;
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
            long amount = levelupPlants(location, wand);
            if (wand.messageType.equals(MessageType.ACTIONBAR)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Color.color(wand.message.replace("%amount%", String.valueOf(amount)).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())))));
            } else {
                player.sendMessage(Color.color(wand.message.replace("%amount%", String.valueOf(amount)).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString()))));
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
