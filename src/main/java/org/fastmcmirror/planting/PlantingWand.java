package org.fastmcmirror.planting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fastmcmirror.i18n.InternationalizationAPI;
import org.fastmcmirror.i18n.MinecraftLanguage;
import org.fastmcmirror.planting.command.PlantCommand;
import org.fastmcmirror.planting.listeners.BoosterListener;
import org.fastmcmirror.planting.listeners.LevelUpListener;
import org.fastmcmirror.planting.listeners.PlantListener;
import org.fastmcmirror.planting.utils.Lang;
import org.fastmcmirror.planting.utils.MessageType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class PlantingWand extends JavaPlugin {

    public static PlantingWand instance;

    public static Map<String,Wand> wands;
    public static Map<String, Wand> boosters;
    public static Map<String, Wand> levelup;
    public static InternationalizationAPI iapi;
    public static Lang lang;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getString("i18n").equals("zh_cn")) {
            saveResource("config-zh_cn.yml", false);
            saveResource("lang/zh_cn.yml", false);
        }
        saveResource("lang/en_us.yml", false);
        Bukkit.getPluginManager().registerEvents(new PlantListener(), this);
        Bukkit.getPluginManager().registerEvents(new BoosterListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        reloadLang();
        reloadWands();
        reloadBoosters();
        reloadLevelUpWands();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            iapi = new InternationalizationAPI(getMinecraftVersion(Bukkit.getBukkitVersion()), MinecraftLanguage.valueOf(getConfig().getString("i18n")), getDataFolder() + "/i18n/");
        });
        getCommand("plantingwand").setExecutor(new PlantCommand());
        printLogo("                                          \n" +
                "──────────────────────────────────────────\n" +
                "                                          \n" +
                "╔═╗┬  ┌─┐┌┐┌┌┬┐┬┌┐┌┌─┐╦ ╦┌─┐┌┐┌┌┬┐        \n" +
                "╠═╝│  ├─┤│││ │ │││││ ┬║║║├─┤│││ ││        \n" +
                "╩  ┴─┘┴ ┴┘└┘ ┴ ┴┘└┘└─┘╚╩╝┴ ┴┘└┘─┴┘        \n" +
                "                                          \n" +
                "──────────────────────────────────────────\n" +
                "                                          \n" +
                "Author: " + getDescription().getAuthors().get(0) + "\n" +
                "Version: " + getDescription().getVersion() + "\n" +
                "MC-Version: " + getMinecraftVersion(Bukkit.getBukkitVersion()) + "\n" +
                "InternationalizationAPI-Language: " + MinecraftLanguage.valueOf(getConfig().getString("i18n")).toString().toUpperCase());
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
                    getConfig().getLong("wands." + name + ".cooldown"),
                    getConfig().getBoolean("wands." + name + ".disposable"),
                    getConfig().getBoolean("wands." + name + ".particle"),
                    Particle.valueOf(getConfig().getString("wands." + name + ".particletype").toUpperCase())
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
                    getConfig().getLong("boosters." + name + ".cooldown"),
                    getConfig().getBoolean("boosters." + name + ".disposable"),
                    getConfig().getBoolean("boosters." + name + ".particle"),
                    Particle.valueOf(getConfig().getString("boosters." + name + ".particletype").toUpperCase())
            ));
        }
    }

    public void reloadLevelUpWands() {
        reloadConfig();
        levelup = new HashMap<>();
        for (String name : getConfig().getConfigurationSection("levelup").getKeys(false)) {
            levelup.put(name, new Wand(
                    getConfig().getInt("levelup." + name + ".range"),
                    Material.getMaterial(getConfig().getString("levelup." + name + ".plant").toUpperCase()),
                    getConfig().getString("levelup." + name + ".permission"),
                    getConfig().getString("levelup." + name + ".message"),
                    MessageType.valueOf(getConfig().getString("levelup." + name + ".messagetype").toUpperCase()),
                    getConfig().getLong("levelup." + name + ".cooldown"),
                    getConfig().getBoolean("levelup." + name + ".disposable"),
                    getConfig().getBoolean("levelup." + name + ".particle"),
                    Particle.valueOf(getConfig().getString("levelup." + name + ".particletype").toUpperCase())
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

    public void printLogo(String text) {
        for (String l : text.split("\n")) {
            getLogger().info(l);
        }
    }

    public void reloadLang() {
        reloadConfig();
        String i18n = getConfig().getString("i18n").toLowerCase();
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(
                getDataFolder() + "/lang/" + i18n + ".yml"
        ));
        lang = new Lang(
                configuration.getString("command.reload"),
                configuration.getString("command.list.wandlist"),
                configuration.getString("command.list.plantingwand"),
                configuration.getString("command.list.boosterwand"),
                configuration.getString("command.list.levelupwand"),
                configuration.getString("command.list.info"),
                configuration.getString("command.help.reload"),
                configuration.getString("command.help.list"),
                configuration.getString("cooldown"));
    }
}
