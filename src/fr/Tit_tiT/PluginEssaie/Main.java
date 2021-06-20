package fr.Tit_tiT.PluginEssaie;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private boolean isStarted;
    private List<Player> liste;

    public void onEnable(){
        isStarted=false;
        liste = new ArrayList<>();
        this.getCommand("murder").setExecutor(new CmdMurder(this,liste));
        this.getCommand("murder").setTabCompleter(new CmdMurTabComp());
    }

    public void onDisable(){
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public List<Player> getListe() {
        return liste;
    }

    public void setListe(List<Player> liste) {
        this.liste = liste;
    }
}
