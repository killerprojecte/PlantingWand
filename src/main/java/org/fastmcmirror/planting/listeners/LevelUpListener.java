package org.fastmcmirror.planting.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Crops;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.Wand;
import org.fastmcmirror.planting.utils.Color;
import org.fastmcmirror.planting.utils.MessageType;
import org.fastmcmirror.planting.utils.ParticleUtil;

public class LevelUpListener implements Listener {
    private static long levelupPlants(Location location, Wand wand, Player player) {
        int range = wand.range;
        long amount = 0L;
        if (!PlantingWand.economics.containsKey(wand.payment.type)) {
            player.sendMessage(Color.color(PlantingWand.lang.unknow_payment));
            return 0L;
        }
        all:
        for (int x = location.getBlockX() - range; x <= location.getBlockX() + range; x++) {
            for (int z = (int) (location.getBlockZ() - range); z <= location.getBlockZ() + range; z++) {
                Location location1 = new Location(location.getWorld(), x, location.getBlockY(), z);
                if (!location.getWorld().getBlockAt(location1).getType().equals(wand.plant)) continue;
                Block block = location.getWorld().getBlockAt(location1);
                BlockState state = block.getState();
                if (!PlantingWand.economics.get(wand.payment.type).has(player, wand.payment.count)) {
                    player.sendMessage(Color.color(PlantingWand.lang.not_enough));
                    break all;
                }
                if (block.getType().equals(Material.COCOA)) {
                    CocoaPlant crops = (CocoaPlant) state.getData();
                    if (crops.getSize().equals(CocoaPlant.CocoaPlantSize.LARGE)) continue;
                    Bukkit.getScheduler().runTask(PlantingWand.instance, () -> {
                        CocoaPlant.CocoaPlantSize cropState = CocoaPlant.CocoaPlantSize.SMALL;
                        switch (crops.getSize()) {
                            case SMALL: {
                                cropState = CocoaPlant.CocoaPlantSize.MEDIUM;
                                break;
                            }
                            case MEDIUM: {
                                cropState = CocoaPlant.CocoaPlantSize.LARGE;
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                        crops.setSize(cropState);
                        state.setData(crops);
                        state.update();
                        if (wand.particle) {
                            ParticleUtil.playParticle(wand.particleType, block);
                        }
                    });
                } else {
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
                }
                amount++;
            }
        }
        long finalAmount = amount;
        Bukkit.getScheduler().runTask(PlantingWand.instance, () -> PlantingWand.economics.get(wand.payment.type).takeMoney(player, wand.payment.count * finalAmount));
        return amount;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        String display = null;
        ItemStack item = player.getItemInHand();
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (PlantingWand.instance.getConfig().getBoolean("nbtmode")) {
            for (String tag : PlantingWand.nbtManager.getWands(item)) {
                if (!PlantingWand.levelup.containsKey(tag)) continue;
                display = tag;
            }
            if (display == null) return;
        } else {
            if (!meta.hasDisplayName()) return;
            display = meta.getDisplayName().replace("§", "&");
        }
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
            long amount = levelupPlants(location, wand, player);
            if (wand.messageType.equals(MessageType.ACTIONBAR)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Color.color(wand.message.replace("%amount%", String.valueOf(amount)).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%count%", String.valueOf(amount * wand.payment.count)))));
            } else {
                player.sendMessage(Color.color(wand.message.replace("%amount%", String.valueOf(amount)).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%count%", String.valueOf(amount * wand.payment.count))));
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
