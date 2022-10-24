package org.fastmcmirror.planting.economy;

import org.bukkit.entity.Player;
import org.fastmcmirror.bartereco.api.BarterEconomyAPI;
import org.fastmcmirror.planting.Wand;

public class BarterEcoEconomy extends SimpleEconomy {

    public String type;

    public BarterEcoEconomy(String type) {
        this.type = type;
    }

    @Override
    public boolean takeMoney(Player player, double amount, Wand wand) {
        return BarterEconomyAPI.take(player, type, (int) amount);
    }

    @Override
    public boolean has(Player player, double amount, Wand wand) {
        return BarterEconomyAPI.has(player, type, (int) amount);
    }
}
