package fr.Tit_tiT.PluginEssaie;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdMurTabComp implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if(sender instanceof Player){
            if(cmd.getName().equalsIgnoreCase("murder")){
                if(args.length==1){
                    list.add("start");
                    list.add("join");
                    list.add("leave");
                }
                if(args.length>1){
                    list.add(ChatColor.DARK_RED+"NO ARGUMENTS");
                }
                return list;
            }
        }
        return null;
    }
}
