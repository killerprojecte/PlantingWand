package org.fastmcmirror.planting.economy;

import org.bukkit.entity.Player;
import org.fastmcmirror.planting.Wand;

public abstract class SimpleEconomy {

    public abstract boolean takeMoney(Player player, double amount, Wand wand);

    public abstract boolean has(Player player, double amount, Wand wand);
}
