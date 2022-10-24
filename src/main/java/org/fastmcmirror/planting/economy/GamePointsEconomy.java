package org.fastmcmirror.planting.economy;

import org.bukkit.entity.Player;
import org.fastmcmirror.planting.Wand;
import su.nightexpress.gamepoints.api.GamePointsAPI;

public class GamePointsEconomy extends SimpleEconomy {
    @Override
    public boolean takeMoney(Player player, double amount, Wand wand) {
        if (GamePointsAPI.getUserData(player).getBalance() < amount) {
            return false;
        }
        GamePointsAPI.getUserData(player).takePoints((int) amount);
        return true;
    }

    @Override
    public boolean has(Player player, double amount, Wand wand) {
        return GamePointsAPI.getUserData(player).getBalance() < amount;
    }
}
