package org.fastmcmirror.planting.economy;

import org.bukkit.entity.Player;
import org.fastmcmirror.planting.Wand;

public class NoneEconomy extends SimpleEconomy {
    @Override
    public boolean takeMoney(Player player, double amount, Wand wand) {
        return true;
    }

    @Override
    public boolean has(Player player, double amount, Wand wand) {
        return true;
    }
}
