package org.fastmcmirror.planting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.fastmcmirror.i18n.InternationalizationAPI;
import org.fastmcmirror.i18n.MinecraftLanguage;
import org.fastmcmirror.planting.command.PlantCommand;
import org.fastmcmirror.planting.listeners.BoosterListener;
import org.fastmcmirror.planting.utils.MessageType;
import org.fastmcmirror.planting.listeners.PlantListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlantingWand extends JavaPlugin {

    public static PlantingWand instance;

    public static Map<String,Wand> wands;
    public static Map<String,Wand> boosters;

    public static InternationalizationAPI iapi;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new PlantListener(),this);
        Bukkit.getPluginManager().registerEvents(new BoosterListener(),this);
        reloadWands();
        reloadBoosters();
        Bukkit.getScheduler().runTaskAsynchronously(this,() -> {
            iapi = new InternationalizationAPI(getMinecraftVersion(Bukkit.getBukkitVersion()), MinecraftLanguage.valueOf(getConfig().getString("i18n")), getDataFolder() + "/i18n/");
        });
        getCommand("plantingwand").setExecutor(new PlantCommand());
        // Plugin startup logic

    }

    public void reloadWands(){
        reloadConfig();
        wands = new HashMap<>();
        for (String name : getConfig().getConfigurationSection("wands").getKeys(false)){
            wands.put(name,new Wand(
                    getConfig().getInt("wands." + name + ".range"),
                    Material.getMaterial(getConfig().getString("wands." + name + ".plant").toUpperCase()),
                    getConfig().getString("wands." + name + ".permission"),
                    getConfig().getString("wands." + name + ".message"),
                    MessageType.valueOf(getConfig().getString("wands." + name + ".messagetype").toUpperCase()),
                    getConfig().getLong("wands." + name + ".cooldown")
            ));
        }
    }

    public void reloadBoosters(){
        reloadConfig();
        boosters = new HashMap<>();
        for (String name : getConfig().getConfigurationSection("boosters").getKeys(false)){
            boosters.put(name,new Wand(
                    getConfig().getInt("boosters." + name + ".range"),
                    Material.getMaterial(getConfig().getString("boosters." + name + ".plant").toUpperCase()),
                    getConfig().getString("boosters." + name + ".permission"),
                    getConfig().getString("boosters." + name + ".message"),
                    MessageType.valueOf(getConfig().getString("boosters." + name + ".messagetype").toUpperCase()),
                    getConfig().getLong("boosters." + name + ".cooldown")
            ));
        }
    }

    public static String getMinecraftVersion(String bukkitVersion) {
        return bukkitVersion.split("-")[0];
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
