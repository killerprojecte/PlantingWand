package org.fastmcmirror.planting.economy;

import org.bukkit.entity.Player;

public class NoneEconomy extends SimpleEconomy {
    @Override
    public boolean takeMoney(Player player, double amount) {
        return true;
    }

    @Override
    public boolean has(Player player, double amount) {
        return true;
    }
}
