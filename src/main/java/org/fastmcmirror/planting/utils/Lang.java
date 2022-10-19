package org.fastmcmirror.planting.utils;

public class Lang {
    public final String command_reload;
    public final String command_list_wandlist;
    public final String command_list_plantingwand;
    public final String command_list_boosterwand;
    public final String command_list_levelupwand;
    public final String command_list_info;
    public final String command_help_reload;
    public final String command_help_list;
    public final String cooldown;
    public final String command_help_add;
    public final String command_add_unavailable;
    public final String command_add_unknow_wand;
    public final String command_add_success;
    public final String command_show;
    public final String command_help_show;
    public final String unknow_payment;
    public final String not_enough;

    public Lang(String command_reload,
                String command_list_wandlist,
                String command_list_plantingwand,
                String command_list_boosterwand,
                String command_list_levelupwand,
                String command_list_info,
                String command_help_reload,
                String command_help_list,
                String cooldown,
                String command_help_add,
                String command_add_unavailable,
                String command_add_unknow_wand,
                String command_add_success,
                String command_show,
                String command_help_show,
                String unknow_payment,
                String not_enough) {
        this.command_reload = command_reload;
        this.command_list_wandlist = command_list_wandlist;
        this.command_list_plantingwand = command_list_plantingwand;
        this.command_list_boosterwand = command_list_boosterwand;
        this.command_list_levelupwand = command_list_levelupwand;
        this.command_list_info = command_list_info;
        this.command_help_reload = command_help_reload;
        this.command_help_list = command_help_list;
        this.cooldown = cooldown;
        this.command_help_add = command_help_add;
        this.command_add_unavailable = command_add_unavailable;
        this.command_add_unknow_wand = command_add_unknow_wand;
        this.command_add_success = command_add_success;
        this.command_show = command_show;
        this.command_help_show = command_help_show;
        this.unknow_payment = unknow_payment;
        this.not_enough = not_enough;
    }
}
