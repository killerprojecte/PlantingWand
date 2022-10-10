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

    public Lang(String command_reload,
                String command_list_wandlist,
                String command_list_plantingwand,
                String command_list_boosterwand,
                String command_list_levelupwand,
                String command_list_info,
                String command_help_reload,
                String command_help_list,
                String cooldown) {
        this.command_reload = command_reload;
        this.command_list_wandlist = command_list_wandlist;
        this.command_list_plantingwand = command_list_plantingwand;
        this.command_list_boosterwand = command_list_boosterwand;
        this.command_list_levelupwand = command_list_levelupwand;
        this.command_list_info = command_list_info;
        this.command_help_reload = command_help_reload;
        this.command_help_list = command_help_list;
        this.cooldown = cooldown;
    }
}
