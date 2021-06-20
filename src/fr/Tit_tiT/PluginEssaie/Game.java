package fr.Tit_tiT.PluginEssaie;


import net.minecraft.server.v1_16_R3.Block;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class Game {

    Main plugin;
    List<Player> listePlayer;
    World world;
    Team gentilTeam;
    Team murderTeam;
    BossBar bbMurder;
    BossBar bbDetec;
    BossBar bbAutre;

    public Game(Main plugin,List<Player> listePlayer) {

        this.plugin = plugin;
        this.listePlayer = listePlayer;
        Player player = listePlayer.get(1);
        this.world = player.getWorld();
        int nbrPlayer = listePlayer.size();
        Scoreboard scBoarad = Bukkit.getScoreboardManager().getMainScoreboard();

        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);
        world.setGameRule(GameRule.KEEP_INVENTORY,true);

        if(world.getName().equals("murder")){//mettre les fleurs

        }

        int murderNum = (int) (Math.random() * nbrPlayer);
        int detectNum = (int) (Math.random() * nbrPlayer);
        while(murderNum==detectNum) detectNum = (int) (Math.random() * nbrPlayer);

        //Pour le murder
        Player murder = listePlayer.get(murderNum);
        Inventory invMur = murder.getInventory();
        invMur.clear();
        murder.updateInventory();
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD,1);
        sword.addEnchantment(Enchantment.DURABILITY,3);
        sword.addEnchantment(Enchantment.DAMAGE_ALL,5);
        invMur.addItem(sword);

        //Pour le Detective
        Player detect = listePlayer.get(detectNum);
        Inventory invDet = detect.getInventory();
        invDet.clear();
        detect.updateInventory();
        ItemStack bow = new ItemStack(Material.BOW,1);
        bow.addEnchantment(Enchantment.DURABILITY,3);
        bow.addEnchantment(Enchantment.ARROW_INFINITE,Enchantment.ARROW_INFINITE.getMaxLevel());
        bow.addEnchantment(Enchantment.ARROW_DAMAGE,5);
        invDet.addItem(bow);
        invDet.addItem(new ItemStack(Material.ARROW,1));

        //Team ,title et bossebar :
        murderTeam = scBoarad.registerNewTeam("Murder");
        murderTeam.setColor(ChatColor.WHITE);
        murderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        murderTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        gentilTeam = scBoarad.registerNewTeam("Gentil");
        gentilTeam.setColor(ChatColor.WHITE);
        gentilTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        gentilTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        bbMurder = Bukkit.createBossBar(ChatColor.RED+"Vous etes le Murder", BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        bbDetec = Bukkit.createBossBar(ChatColor.GREEN+"Vous etes le Détective", BarColor.GREEN, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        bbAutre = Bukkit.createBossBar("Vous etes un gentil", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);

        List<Player> listGentil = new ArrayList<>();
        for(Player p : listePlayer){
            if(!(listePlayer.get(murderNum).equals(p))) {
                listGentil.add(p);
                gentilTeam.addEntry(p.getName());
                if (listePlayer.get(detectNum).equals(p)){
                    p.sendTitle(ChatColor.GREEN + "Vous etes le détective", "", 10, 70, 20);
                    bbDetec.addPlayer(p);
                }
                else {
                    p.getInventory().clear();
                    p.updateInventory();
                    p.sendTitle(ChatColor.WHITE + "Vous etes gentil", "", 10, 70, 20);
                    bbAutre.addPlayer(p);
                }
            }
            else{
                murderTeam.addEntry(murder.getName());
                murder.sendTitle(ChatColor.RED+"Vous etes le murder","",10,70,20);
                bbMurder.addPlayer(p);
            }
        }

        //Pour les morts
        new BukkitRunnable(){
            @Override
            public void run() {
                if(murder.isDead()){ // si le murder est mort
                    for(Player p : listePlayer) {
                        p.sendTitle(ChatColor.GREEN+"Les Gentil ont gagné","",10,70,20);
                        p.setGameMode(GameMode.SPECTATOR);
                    }
                    murder.setGameMode(GameMode.SPECTATOR);
                    listePlayer.remove(murder);
                    endOfGame();
                    cancel();
                }
                else{
                    if(listGentil.size()==0){ // si tout les gentil sont mort
                        for(Player p : listePlayer) {
                            p.sendTitle(ChatColor.RED+"Le Murder a gagné","",10,70,20);
                            p.setGameMode(GameMode.SPECTATOR);
                            p.teleport(murder.getLocation());
                        }
                        endOfGame();
                        cancel();
                    }
                    boolean isDead = false;
                    Player pDead = null;
                    for(Player p : listGentil){
                            if(p.isDead()){ //si un gentil est mort
                                isDead=true;
                                pDead=p;
                                p.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                    if(isDead){
                        listePlayer.remove(pDead);
                        listGentil.remove(pDead);
                    }
                }

            }
        }.runTaskTimer(plugin,0,1);
    }


    public void endOfGame(){
        plugin.setStarted(false);
        for(Player p : listePlayer){
            p.removePotionEffect(PotionEffectType.WATER_BREATHING);
            p.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            p.removePotionEffect(PotionEffectType.SATURATION);
            p.getInventory().clear();
            p.updateInventory();
        }
        listePlayer.clear();
        bbAutre.removeAll();
        bbDetec.removeAll();
        bbMurder.removeAll();
        murderTeam.unregister();
        gentilTeam.unregister();
    }



    public List<Location> objLoc(String place){
        List<Location> list = new ArrayList<>();
        if(place.equals("jardin")){
            list.add(new Location(world,-331,79,-1119));
            list.add(new Location(world,-335,78,-1115));
            list.add(new Location(world,-323,84,-1116));
            list.add(new Location(world, -331,78,-1128));
            list.add(new Location(world,-334,79,-1140));
            list.add(new Location(world,-327,79,-1135));
            list.add(new Location(world, -337,78,-1134));
            list.add(new Location(world,-316,78,-1143));
            list.add(new Location(world,-312,78,-1134));
            list.add(new Location(world,-307,79,-1125));
            list.add(new Location(world,-303,75,-1123));
            list.add(new Location(world,-309,79,-1116));
            return list;
        }
        if(place.equals("rdc")){
            list.add(new Location(world,-311,80,-1132));
            list.add(new Location(world,-314,79,-1126));
            list.add(new Location(world,-312,78,-1119));
            list.add(new Location(world,-318,79,-1122));
            list.add(new Location(world,-321,77,-1118));
            return list;
        }
        if(place.equals("1er")){
            list.add(new Location(world,-316,82,-1124));
            list.add(new Location(world,-311,83,-1118));
            list.add(new Location(world,-310,85,-1135));
            list.add(new Location(world,-321,86,-1129));
            list.add(new Location(world,-319,83,-1121));
            return list;
        }
        if(place.equals("2eme")){
            list.add(new Location(world,-310,88,-1125));
            list.add(new Location(world,-316,88,-1131));
            list.add(new Location(world,-322,88,-1127));
            list.add(new Location(world,-322,88,-1118));
            list.add(new Location(world,-317,89,-1131));
            return list;
        }
        if(place.equals("grenier")){
            list.add(new Location(world,-314,92,-1127));
            list.add(new Location(world,-318,92,-1123));
            list.add(new Location(world,-316,92,-1125));
            list.add(new Location(world,-324,91,-1125));
            list.add(new Location(world,-316,95,-1115));
            return list;
        }
        if(place.equals("sous-sol")){
            list.add(new Location(world,-317,73,-1124));
            list.add(new Location(world,-311,73,-1132));
            list.add(new Location(world,-326,74,-1131));
            list.add(new Location(world,-307,75,-1123));
            list.add(new Location(world,-321,73,-1118));
            return list;
        }
        return null;
    }
}
