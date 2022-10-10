package org.fastmcmirror.planting.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fastmcmirror.i18n.InternationalizationAPI;
import org.fastmcmirror.i18n.MinecraftLanguage;
import org.fastmcmirror.planting.PlantingWand;
import org.fastmcmirror.planting.Wand;
import org.fastmcmirror.planting.utils.Color;

public class PlantCommand implements CommandExecutor {
    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(Color.color("&e&lPlantingWand &aby FastMCMirror"));
        sender.sendMessage(Color.color("&7/plantingwand reload ———— " + PlantingWand.lang.command_help_reload));
        sender.sendMessage(Color.color("&7/plantingwand list ———— " + PlantingWand.lang.command_help_list));
        if (PlantingWand.instance.getConfig().getBoolean("nbtmode")) {
            sender.sendMessage(Color.color("&7/plantingwand add <Wand> ———— " + PlantingWand.lang.command_help_add));
            sender.sendMessage(Color.color("&7/plantingwand show ———— " + PlantingWand.lang.command_help_show));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("plantingwand.admin")) return false;
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                PlantingWand.instance.reloadLang();
                PlantingWand.instance.reloadWands();
                PlantingWand.instance.reloadBoosters();
                PlantingWand.instance.reloadLevelUpWands();
                Bukkit.getScheduler().runTaskAsynchronously(PlantingWand.instance, () -> {
                    PlantingWand.iapi = new InternationalizationAPI(PlantingWand.getMinecraftVersion(Bukkit.getBukkitVersion()), MinecraftLanguage.valueOf(PlantingWand.instance.getConfig().getString("i18n")), PlantingWand.instance.getDataFolder() + "/i18n/");
                });
                sender.sendMessage(Color.color(PlantingWand.lang.command_reload));
            } else if (args[0].equals("list")) {
                sender.sendMessage(Color.color(PlantingWand.lang.command_list_wandlist));
                sender.sendMessage(Color.color(PlantingWand.lang.command_list_plantingwand));
                for (String key : PlantingWand.wands.keySet()) {
                    Wand wand = PlantingWand.wands.get(key);
                    sender.sendMessage(Color.color(PlantingWand.lang.command_list_info.replace("%key%", key).replace("%range%", String.valueOf(wand.range)).replace("%permission%", wand.permission).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%message%", wand.message.replace("%amount%", "1").replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString()))).replace("%message_type%", wand.messageType.toString())));
                }
                sender.sendMessage(Color.color(PlantingWand.lang.command_list_boosterwand));
                for (String key : PlantingWand.boosters.keySet()) {
                    Wand wand = PlantingWand.boosters.get(key);
                    sender.sendMessage(Color.color(PlantingWand.lang.command_list_info.replace("%key%", key).replace("%range%", String.valueOf(wand.range)).replace("%permission%", wand.permission).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%message%", wand.message.replace("%amount%", "1").replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString()))).replace("%message_type%", wand.messageType.toString())));
                }
                sender.sendMessage(Color.color(PlantingWand.lang.command_list_levelupwand));
                for (String key : PlantingWand.levelup.keySet()) {
                    Wand wand = PlantingWand.levelup.get(key);
                    sender.sendMessage(Color.color(PlantingWand.lang.command_list_info.replace("%key%", key).replace("%range%", String.valueOf(wand.range)).replace("%permission%", wand.permission).replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString())).replace("%message%", wand.message.replace("%amount%", "1").replace("%item%", PlantingWand.iapi.getItemName(wand.plant.toString()))).replace("%message_type%", wand.messageType.toString())));
                }
            } else if (args[0].equals("show")) {
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;
                if (!PlantingWand.instance.getConfig().getBoolean("nbtmode")) {
                    sender.sendMessage(Color.color(PlantingWand.lang.command_add_unavailable));
                    return false;
                }
                sender.sendMessage(Color.color(PlantingWand.lang.command_show));
                for (String tag : PlantingWand.nbtManager.getWands(player.getItemInHand())) {
                    sender.sendMessage(Color.color("&e> " + tag));
                }
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 2) {
            String tag2 = args[1];
            if (args[0].equals("add")) {
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;
                if (!PlantingWand.instance.getConfig().getBoolean("nbtmode")) {
                    sender.sendMessage(Color.color(PlantingWand.lang.command_add_unavailable));
                    return false;
                }
                if (!PlantingWand.wands.containsKey(tag2) && !PlantingWand.boosters.containsKey(tag2) && !PlantingWand.levelup.containsKey(tag2)) {
                    sender.sendMessage(Color.color(PlantingWand.lang.command_add_unknow_wand));
                    return false;
                }
                player.setItemInHand(PlantingWand.nbtManager.addWand(player.getItemInHand(), tag2));
                sender.sendMessage(Color.color(PlantingWand.lang.command_add_success));
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
        return false;
    }
}
