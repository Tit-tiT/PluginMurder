package fr.Tit_tiT.PluginEssaie;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


public class CmdMurder implements CommandExecutor {
    Main plugin;
    List<Player> liste;
    BossBar bbNbJ;

    public CmdMurder(Main plugin, List<Player> l){
        this.plugin=plugin;
        this.liste = l;
        this.bbNbJ = Bukkit.createBossBar(liste.size()+" Joueurs dans la partie", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        bbNbJ.setProgress(1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            if(args.length==1){
                if(args[0].equals("start")) start((Player)sender);
                else if(args[0].equals("leave")) leave((Player)sender);
                else if(args[0].equals("join")) join((Player)sender);
                else{
                    ((Player)sender).sendMessage(ChatColor.RED + "Argument non reconnue merci de mettre les suivants :");
                    ((Player)sender).sendMessage("start : pour commencer la partie");
                    ((Player)sender).sendMessage("join : pour rejoindre la partie");
                    ((Player)sender).sendMessage("leave : pour quitter la partie");
                }
            }
            else if(args.length>1){
                ((Player)sender).sendMessage(ChatColor.RED + "Il y a trop d'arguments merci de mettre les suivants :");
                ((Player)sender).sendMessage("start : pour commencer la partie");
                ((Player)sender).sendMessage("join : pour rejoindre la partie");
                ((Player)sender).sendMessage("leave : pour quitter la partie");
            }
            else{
                ((Player)sender).sendMessage(ChatColor.RED + "Il n'y a pas assez d'argument merci de mettre les suivants :");
                ((Player)sender).sendMessage("start : pour commencer la partie");
                ((Player)sender).sendMessage("join : pour rejoindre la partie");
                ((Player)sender).sendMessage("leave : pour quitter la partie");
            }
        }
        return true;
    }




    public void start(Player p){
        if(plugin.isStarted()) p.sendMessage(ChatColor.RED + "Le jeu est déja commencé");
        else {
            List<Player> listePlayer = liste;
            int nbrPlayer = listePlayer.size();
            if(nbrPlayer<=1) p.sendMessage(ChatColor.RED + "Il n'y a pas assez de joueurs pour jouer");
            else {
                if(listePlayer.get(1).getWorld().getName().equals("murder")){
                    for(Player p2 : listePlayer) p2.teleport(new Location(listePlayer.get(1).getWorld(),-316, 78, -1141));
                }
                bbNbJ.removeAll();
                for(Player pp : listePlayer){
                    pp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,1000000,255,true));
                    pp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,1000000,255,true));
                    pp.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,1000000,255,true));
                    pp.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,1000000,255,true));
                    pp.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,1000000,255,true));
                }
                new BukkitRunnable(){
                    double timer = 60;
                    BossBar bb = Bukkit.createBossBar("60 secondes restantes", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);

                    @Override
                    public void run() {
                        if (timer == 60) {
                            Bukkit.broadcastMessage("Vous avez 1min pour vous cacher bonne chance");
                            for(Player p : listePlayer){
                                bb.addPlayer(p);
                            }
                            bb.setProgress(1);
                            bb.setVisible(true);
                            timer--;
                        }
                        else if(timer == 0){
                            bb.removeAll();
                            Bukkit.broadcastMessage("C'est partie !!!!");
                            for(Player pp : listePlayer){
                                pp.removePotionEffect(PotionEffectType.INVISIBILITY);
                                pp.removePotionEffect(PotionEffectType.REGENERATION);
                            }
                            new Game(plugin,listePlayer);
                            timer--;
                        }
                        else if(timer<0) {
                            plugin.setStarted(true);
                            cancel();
                        }
                        else{
                            bb.setProgress(timer/60);
                            bb.setTitle((int)timer + " secondes restantes");
                            timer--;
                        }
                    }
                }.runTaskTimer(plugin,0,20);
            }
        }
    }

    public void leave(Player p){
        if(plugin.isStarted()) p.sendMessage(ChatColor.RED +"Le jeu est déja commencé");
        else if(!liste.contains(p)) p.sendMessage(ChatColor.RED +"Vous n'etes pas dans la partie");
        else{
            liste.remove(p);
            p.setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage(ChatColor.BLUE +p.getName()+" ne joue plus au Murder :'(");
        }

    }

    public void join(Player p){
        if(plugin.isStarted()) p.sendMessage(ChatColor.RED +"Le jeu est déja commencé");
        else if(liste.contains(p)) p.sendMessage(ChatColor.RED +"Vous etes déja dans la partie");
        else {
            liste.add(p);
            bbNbJ.addPlayer(p);
            bbNbJ.setTitle(liste.size()+" Joueurs dans la partie");
            p.setGameMode(GameMode.SURVIVAL);
            Bukkit.broadcastMessage(ChatColor.GREEN +p.getName()+" joue au Murder !");
        }
    }


}
