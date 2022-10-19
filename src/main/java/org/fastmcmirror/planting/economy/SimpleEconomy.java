package org.fastmcmirror.planting.economy;

import org.bukkit.entity.Player;

public abstract class SimpleEconomy {

    public abstract boolean takeMoney(Player player, double amount);

    public abstract boolean has(Player player, double amount);
}
