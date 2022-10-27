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
import org.fastmcmirror.planting.command.SeedBankCommand;
import org.fastmcmirror.planting.economy.*;
import org.fastmcmirror.planting.listeners.BoosterListener;
import org.fastmcmirror.planting.listeners.LevelUpListener;
import org.fastmcmirror.planting.listeners.PlantListener;
import org.fastmcmirror.planting.model.CustomCropsModel;
import org.fastmcmirror.planting.model.ItemModsModel;
import org.fastmcmirror.planting.model.ItemsAdderModel;
import org.fastmcmirror.planting.model.SimpleModel;
import org.fastmcmirror.planting.nms.*;
import org.fastmcmirror.planting.seedbank.SeedBankGUIListener;
import org.fastmcmirror.planting.utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public final class PlantingWand extends JavaPlugin {

    public static PlantingWand instance;

    public static Map<String, Wand> wands;
    public static Map<String, Wand> boosters;
    public static Map<String, Wand> levelup;
    public static InternationalizationAPI iapi;
    public static Lang lang;
    public static NbtManager nbtManager;
    public static SimpleModel modelEngine;
    public static Map<String, SimpleEconomy> economics = new HashMap<>();

    public static String getMinecraftVersion(String bukkitVersion) {
        return bukkitVersion.split("-")[0];
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getString("i18n").equals("zh_cn")) {
            saveFile("config-zh_cn.yml", true, "config.yml");
            saveFile("lang/zh_cn.yml");
        }
        saveFile("lang/en_us.yml");
        if (Bukkit.getPluginManager().getPlugin("BarterEconomy") != null) {
            saveFile("bartereco.yml");
        }
        new Metrics(this);
        Bukkit.getPluginManager().registerEvents(new PlantListener(), this);
        Bukkit.getPluginManager().registerEvents(new BoosterListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        reloadLang();
        reloadWands();
        reloadBoosters();
        reloadLevelUpWands();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> iapi = new InternationalizationAPI(getMinecraftVersion(Bukkit.getBukkitVersion()), MinecraftLanguage.valueOf(getConfig().getString("i18n")), getDataFolder() + "/i18n/"));
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
        setupNbtManager();
        setupEconomics();
        switch (getConfig().getString("model").toLowerCase()) {
            case "itemsadder": {
                modelEngine = new ItemsAdderModel();
                break;
            }
            case "itemmods": {
                modelEngine = new ItemModsModel();
                break;
            }
            case "customcrops": {
                modelEngine = new CustomCropsModel();
                break;
            }
            case "none": {
                modelEngine = null;
                break;
            }
            default: {
                getLogger().warning("Unknown Model-Engine: " + getConfig().getString("model"));
                modelEngine = null;
                break;
            }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, Version::checkUpdate, 20L * 60L * 3L);
        // Plugin startup logic

    }

    public void setupEconomics() {
        int amount = 0;
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            getLogger().info("Vault found. loading this module...");
            economics.put("vault", VaultEconomy.setup());
            amount++;
        }
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            getLogger().info("PlayerPoints found. loading this module...");
            economics.put("playerpoints", PlayerPointsEconomy.setup());
            amount++;
        }
        if (Bukkit.getPluginManager().getPlugin("GamePoints") != null) {
            getLogger().info("GamePoints found. loading this module...");
            economics.put("gamepoints", new GamePointsEconomy());
            amount++;
        }
        if (Bukkit.getPluginManager().getPlugin("BarterEconomy") != null) {
            getLogger().info("BarterEconomy found. loading this module...");
            amount += registerBarterEconomy();
            getLogger().info("[SeedBank] SeedBank has been activated");
            Bukkit.getPluginManager().registerEvents(new SeedBankGUIListener(), this);
            getCommand("seedbank").setExecutor(new SeedBankCommand());
        }
        economics.put("none", new NoneEconomy());
        getLogger().info("All economic modules loaded. Successfully loaded into " + amount + " economic expansion");
    }

    public int registerBarterEconomy() {
        int amount = 0;
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/bartereco.yml"));
        for (String registry : configuration.getConfigurationSection("currency").getKeys(false)) {
            economics.put("bartereco:" + registry.toLowerCase(), new BarterEcoEconomy(configuration.getString("currency." + registry).toUpperCase()));
            getLogger().info("[BarterEconomy-Hook] Registered custom currency: " + registry + " (" + configuration.getString("currency." + registry).toUpperCase() + ")");
            amount++;
        }
        return amount;
    }

    public void setupNbtManager() {
        String version = Bukkit.getServer().getClass().getPackage()
                .getName().replace("org.bukkit.craftbukkit.", "");
        getLogger().info("[PlantingWand] Loading NMSManager...");
        switch (version) {
            case "v1_13_R1": {
                nbtManager = new NMS_1_13_R1();
                break;
            }
            case "v1_13_R2": {
                nbtManager = new NMS_1_13_R2();
                break;
            }
            case "v1_14_R1": {
                nbtManager = new NMS_1_14_R1();
                break;
            }
            case "v1_15_R1": {
                nbtManager = new NMS_1_15_R1();
                break;
            }
            case "v1_16_R1": {
                nbtManager = new NMS_1_16_R1();
                break;
            }
            case "v1_16_R2": {
                nbtManager = new NMS_1_16_R2();
                break;
            }
            case "v1_16_R3": {
                nbtManager = new NMS_1_16_R3();
                break;
            }
            case "v1_17_R1": {
                nbtManager = new NMS_1_17_R1();
                break;
            }
            case "v1_18_R1": {
                nbtManager = new NMS_1_18_R1();
                break;
            }
            case "v1_18_R2": {
                nbtManager = new NMS_1_18_R2();
                break;
            }
            case "v1_19_R1": {
                nbtManager = new NMS_1_19_R1();
                break;
            }
            default: {
                getLogger().severe("[PlantingWand] Unsupport Version: " + version);
                setEnabled(false);
                break;
            }
        }
    }

    public void reloadWands() {
        reloadConfig();
        wands = new HashMap<>();
        for (String name : getConfig().getConfigurationSection("wands").getKeys(false)) {
            wands.put(name, new Wand(
                    getConfig().getInt("wands." + name + ".range"),
                    Material.getMaterial(getConfig().getString("wands." + name + ".plant").toUpperCase()),
                    getConfig().getString("wands." + name + ".permission"),
                    getConfig().getString("wands." + name + ".message"),
                    MessageType.valueOf(getConfig().getString("wands." + name + ".messagetype").toUpperCase()),
                    getConfig().getLong("wands." + name + ".cooldown"),
                    getConfig().getBoolean("wands." + name + ".disposable"),
                    getConfig().getBoolean("wands." + name + ".particle"),
                    Particle.valueOf(getConfig().getString("wands." + name + ".particletype").toUpperCase()),
                    getConfig().getBoolean("wands." + name + ".model"),
                    getConfig().getString("wands." + name + ".modelid"),
                    Material.getMaterial(getConfig().getString("wands." + name + ".farmblock", "FARMLAND")),
                    new Payment(getConfig().getString("wands." + name + ".payment.type").toLowerCase(),
                            getConfig().getDouble("wands." + name + ".payment.count")),
                    new LocationOffset(getConfig().getInt("wands." + name + ".offset.x", 0),
                            getConfig().getInt("wands." + name + ".offset.y", 1),
                            getConfig().getInt("wands." + name + ".offset.z", 0))
            ));
        }
    }

    public void reloadBoosters() {
        reloadConfig();
        boosters = new HashMap<>();
        for (String name : getConfig().getConfigurationSection("boosters").getKeys(false)) {
            boosters.put(name, new Wand(
                    getConfig().getInt("boosters." + name + ".range"),
                    Material.getMaterial(getConfig().getString("boosters." + name + ".plant").toUpperCase()),
                    getConfig().getString("boosters." + name + ".permission"),
                    getConfig().getString("boosters." + name + ".message"),
                    MessageType.valueOf(getConfig().getString("boosters." + name + ".messagetype").toUpperCase()),
                    getConfig().getLong("boosters." + name + ".cooldown"),
                    getConfig().getBoolean("boosters." + name + ".disposable"),
                    getConfig().getBoolean("boosters." + name + ".particle"),
                    Particle.valueOf(getConfig().getString("boosters." + name + ".particletype").toUpperCase()),
                    getConfig().getBoolean("boosters." + name + ".model", false),
                    getConfig().getString("boosters." + name + ".modelid", "unknow"),
                    Material.getMaterial(getConfig().getString("boosters." + name + ".farmblock", "FARMLAND")),
                    new Payment(getConfig().getString("boosters." + name + ".payment.type").toLowerCase(),
                            getConfig().getDouble("boosters." + name + ".payment.count")),
                    new LocationOffset(getConfig().getInt("boosters." + name + ".offset.x", 0),
                            getConfig().getInt("boosters." + name + ".offset.y", 1),
                            getConfig().getInt("boosters." + name + ".offset.z", 0))
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
                    Particle.valueOf(getConfig().getString("levelup." + name + ".particletype").toUpperCase()),
                    getConfig().getBoolean("levelup." + name + ".model", false),
                    getConfig().getString("levelup." + name + ".modelid", "unknow"),
                    Material.getMaterial(getConfig().getString("levelup." + name + ".farmblock", "FARMLAND")),
                    new Payment(getConfig().getString("levelup." + name + ".payment.type").toLowerCase(),
                            getConfig().getDouble("levelup." + name + ".payment.count")),
                    new LocationOffset(getConfig().getInt("levelup." + name + ".offset.x", 0),
                            getConfig().getInt("levelup." + name + ".offset.y", 1),
                            getConfig().getInt("levelup." + name + ".offset.z", 0))
            ));
        }
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
                configuration.getString("cooldown"),
                configuration.getString("command.help.add"),
                configuration.getString("command.add.unavailable"),
                configuration.getString("command.add.unknow-wand"),
                configuration.getString("command.add.success"),
                configuration.getString("command.show"),
                configuration.getString("command.help.show"),
                configuration.getString("unknow-payment"),
                configuration.getString("not-enough"),
                configuration.getString("seedbank.title"),
                configuration.getString("seedbank.success"),
                configuration.getString("seedbank.button"),
                configuration.getString("seedbank.left"),
                configuration.getString("seedbank.right"));
    }

    public void saveFile(String name) {
        saveFile(name, false, name);
    }

    public void saveFile(String name, boolean replace) {
        saveFile(name, replace, name);
    }

    public void saveFile(String name, boolean replace, String saveName) {
        URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            getLogger().severe(name + " Not Found in JarFile");
            return;
        }
        File file = new File(getDataFolder() + "/" + saveName);
        if (!replace) {
            if (file.exists()) return;
        }
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
        connection.setUseCaches(false);
        try {
            saveFile(connection.getInputStream(), file);
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
    }

    private void saveFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
