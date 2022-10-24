package org.fastmcmirror.planting.seedbank;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.bartereco.api.BarterEconomyAPI;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.utils.Color;

import java.util.Map;

public class SeedBankGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (event.getClickedInventory().getHolder() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof SeedBankHolder)) return;
        if (event.getSlot() != 4 && event.getSlot() != 8) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == 8) {
            event.setCancelled(true);
            if (event.getClickedInventory().getItem(4) == null || event.getClickedInventory().getItem(4).getType().equals(Material.AIR) || event.getClickedInventory().getItem(4).getAmount() == 0) {
                return;
            }
            final int amount = event.getClickedInventory().getItem(4).getAmount();
            BarterEconomyAPI.give(player, event.getClickedInventory().getItem(4).getType().toString(), amount);
            player.sendMessage(Color.color(PlantingWand.lang.seedbank_success.replace("%amount%", String.valueOf(amount))
                    .replace("%item%", PlantingWand.iapi.getItemName(event.getClickedInventory().getItem(4).getType().toString()))));
            event.getClickedInventory().getItem(4).setAmount(0);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof SeedBankHolder)) return;
        if (event.getInventory().getItem(4) == null || event.getInventory().getItem(4).getType().equals(Material.AIR)
                || event.getInventory().getItem(4).getAmount() == 0) return;
        Map<Integer, ItemStack> map = event.getPlayer().getInventory().addItem(event.getInventory().getItem(4));
        for (int key : map.keySet()) {
            event.getPlayer().getLocation().getWorld().dropItemNaturally(event.getPlayer().getLocation(), map.get(key));
        }
    }
}
