package org.fastmcmirror.planting.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.fastmcmirror.i18n.InternationalizationAPI;
import org.fastmcmirror.i18n.MinecraftLanguage;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.Wand;
import org.fastmcmirror.planting.utils.Color;

public class PlantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("plantingwand.admin")) return false;
        if (args.length==1){
            if (args[0].equals("reload")){
                PlantingWand.instance.reloadWands();
                PlantingWand.instance.reloadBoosters();
                Bukkit.getScheduler().runTaskAsynchronously(PlantingWand.instance,() -> {
                    PlantingWand.iapi = new InternationalizationAPI(PlantingWand.getMinecraftVersion(Bukkit.getBukkitVersion()), MinecraftLanguage.valueOf(PlantingWand.instance.getConfig().getString("i18n")), PlantingWand.instance.getDataFolder() + "/i18n/");
                });
                sender.sendMessage(Color.color("&a配置已重载!"));
            } else if (args[0].equals("list")){
                sender.sendMessage(Color.color("&c&l魔杖列表: "));
                sender.sendMessage(Color.color("&a&l种植魔杖:"));
                for (String key : PlantingWand.wands.keySet()){
                    Wand wand = PlantingWand.wands.get(key);
                    sender.sendMessage(Color.color("&e物品名称: &r" + key + " &e范围: &f" + wand.range + " &e权限: &f" + wand.permission + " &e种植物: &f" + PlantingWand.iapi.getItemName(wand.plant.toString())
                    + " &e替换信息: &r" + wand.message + " &e消息类型: " + wand.messageType.toString()));
                }
                sender.sendMessage(Color.color("&e&l生长魔杖:"));
                for (String key : PlantingWand.boosters.keySet()){
                    Wand wand = PlantingWand.boosters.get(key);
                    sender.sendMessage(Color.color("&e物品名称: &r" + key + " &e范围: &f" + wand.range + " &e权限: &f" + wand.permission + " &e种植物: &f" + PlantingWand.iapi.getItemName(wand.plant.toString())
                            + " &e替换信息: &r" + wand.message + " &e消息类型: " + wand.messageType.toString()));
                }
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
        return false;
    }

    private static void sendHelp(CommandSender sender){
        sender.sendMessage(Color.color("&e&lPlantingWand &aby FastMCMirror"));
        sender.sendMessage(Color.color("&7/plantingwand reload ———— 重载配置"));
        sender.sendMessage(Color.color("&7/plantingwand list ———— 查看所有魔杖"));
    }
}
