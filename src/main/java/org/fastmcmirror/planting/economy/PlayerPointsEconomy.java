package org.fastmcmirror.planting.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.fastmcmirror.planting.PlantingWand;

public class PlayerPointsEconomy extends SimpleEconomy {
    private final PlayerPointsAPI api;

    private PlayerPointsEconomy(PlayerPointsAPI api) {
        this.api = api;
    }

    public static PlayerPointsEconomy setup() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
            PlantingWand.instance.getLogger().warning("PlayerPoints not found. disabled this module!");
            return null;
        }
        return new PlayerPointsEconomy(((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI());
    }

    @Override
    public boolean takeMoney(Player player, double amount) {
        if (api.look(player.getUniqueId()) < amount) {
            return false;
        }
        api.take(player.getUniqueId(), (int) amount);
        return true;
    }

    @Override
    public boolean has(Player player, double amount) {
        return api.look(player.getUniqueId()) >= amount;
    }
}
