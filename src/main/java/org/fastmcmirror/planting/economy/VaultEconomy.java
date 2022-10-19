package org.fastmcmirror.planting.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.fastmcmirror.planting.PlantingWand;

public class VaultEconomy extends SimpleEconomy {

    private final Economy economy;

    private VaultEconomy(Economy economy) {
        this.economy = economy;
    }

    public static VaultEconomy setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            PlantingWand.instance.getLogger().warning("Vault not found. disabled this module!");
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            PlantingWand.instance.getLogger().warning("Vault not found. disabled this module!");
            return null;
        }
        return new VaultEconomy(rsp.getProvider());
    }

    @Override
    public boolean takeMoney(Player player, double amount) {
        if (economy.has(player, amount)) {
            economy.withdrawPlayer(player, amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean has(Player player, double amount) {
        return economy.has(player, amount);
    }
}
