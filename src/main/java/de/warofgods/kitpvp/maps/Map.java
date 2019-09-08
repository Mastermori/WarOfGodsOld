package de.warofgods.kitpvp.maps;

import de.warofgods.kitpvp.God;
import de.warofgods.kitpvp.TimeoutVar;
import de.warofgods.kitpvp.teams.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Map {

    int id;
    String name;
    String stylizedName;
    Location corner1;
    Location corner2;
    TimeoutVar startTimer;
    TimeoutVar roundTimer;
    transient List<God> gods;
    List<Team> teams;
    boolean started;

    public Map(String name, String dispName, Location corner1, Location corner2) {
        this.name = name;
        this.stylizedName = ChatColor.YELLOW + dispName + ChatColor.GRAY + " - ";
        this.corner1 = corner1;
        this.corner2 = corner2;
        startTimer = new TimeoutVar() {
            @Override
            protected void onStart(int time) {
                for(God god : gods)
                    god.getPlayer().sendMessage(stylizedName + ChatColor.GREEN + "The round will start in 10");
            }

            @Override
            protected void onTimeout() {
                Map.this.start();
            }
        };
        roundTimer = new TimeoutVar() {
            @Override
            protected void onStart(int time) {
                for(God god : gods){
                    god.getPlayer().sendMessage(stylizedName + ChatColor.GREEN + "The round has started!");
                }
            }

            @Override
            protected void onTimeout() {

            }
        };
        teams = new ArrayList<>();
        teams.add(new Team("blue", ChatColor.BLUE + "Blue"));
        teams.add(new Team("red", ChatColor.RED + "Red"));
    }

    public void start(){
        started = true;

    }

    public void spawnPlayer(God god) {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
