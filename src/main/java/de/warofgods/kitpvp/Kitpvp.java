package de.warofgods.kitpvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.warofgods.kitpvp.kits.Ares;
import de.warofgods.kitpvp.kits.Kit;
import de.warofgods.kitpvp.maps.Map;
import de.warofgods.kitpvp.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Kitpvp extends JavaPlugin implements Listener {

    static String kpInfo = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "WarOfGods" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " ";
    static String kpError = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "WarOfGods" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + " ";

    public static Kitpvp plugin;
    public static List<Kit> kits = new ArrayList<>();
    public static List<Team> teams = new ArrayList<>();
    public static List<Map> maps = new ArrayList<>();
    public static HashMap<Player, God> gods = new HashMap<>();

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static JsonParser parser = new JsonParser();
    ;
    public static JsonObject godsJson;
    private static final String godDataPath = "plugins\\WarOfGods\\gods.json";
    private static final String mapDataPath = "plugins\\WarOfGods\\maps.json";

    public Kitpvp() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        getLogger().info(kpInfo + "Loaded plugin!");
        getServer().getPluginManager().registerEvents(this, this);
        kits.add(new Ares());
        try {
            if (!new File(godDataPath).exists()) {
                new File(godDataPath).getParentFile().mkdirs();
                godsJson = new JsonObject();
                writeGodsFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(godDataPath));
            //godsJson = new JsonObject();
            godsJson = parser.parse(reader).getAsJsonObject();
            for (Player p : Bukkit.getOnlinePlayers()) {
                loadGodFromJson(p);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        writeGodsFile();
        getLogger().info("Unloaded plugin!");
    }

    public static God loadGodFromJson(Player p) {
        if (godsJson.has(p.getName())) {
            broadcast(kpInfo + "Loaded  " + p.getName() + " from gods");
            gods.put(p, new God(godsJson.get(p.getName()).getAsJsonObject(), p));
            return getGod(p);
        } else {
            broadcast(kpInfo + "Added  " + p.getName() + " to gods");
            gods.put(p, new God(p));
            saveGodToJson(getGod(p));
            return getGod(p);
        }
    }

    public static void saveGodToJson(God g) {
        godsJson.add(g.getPlayer().getName(), parser.parse(gson.toJson(g)).getAsJsonObject());
    }

    public static void writeGodsFile() {
        try {
            for (God god : Kitpvp.gods.values()) {
                saveGodToJson(god);
            }
            BufferedWriter w = Files.newBufferedWriter(Paths.get(godDataPath));
            gson.toJson(godsJson.getAsJsonObject(), w);
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wog")) {
            Player p;
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                return false;
            }
            if (args.length == 0) {
                sender.sendMessage(kpInfo + "/wog kit select [name]");
                sender.sendMessage(kpInfo + "/wog team set [id]");
                sender.sendMessage(kpInfo + "/wog test [name]");
                sender.sendMessage(kpInfo + "/wog stats [player]");
                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("kit")) {
                    sender.sendMessage(kpInfo + "/wog kit select [name]");
                    return true;
                } else if (args[0].equalsIgnoreCase("test")) {
                    sender.sendMessage(kpInfo + "/wog test [name]");
                    return true;
                } else if (args[0].equalsIgnoreCase("stats")) {
                    sender.sendMessage(kpInfo + "Stats:\n" + ChatColor.GRAY +  getGod(p).getStatsString());
                    return true;
                } else if (args[0].equalsIgnoreCase("team")) {
                    sender.sendMessage(kpInfo + "/wog team set [id]");
                    return true;
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("kit")) {
                    sender.sendMessage(kpInfo + "/wog kit select [name]");
                    return true;
                }
                if (args[0].equalsIgnoreCase("team")) {
                    sender.sendMessage(kpInfo + "/wog team set [id]");
                    return true;
                }
                if (args[0].equalsIgnoreCase("test")) {
                    switch (args[1].toLowerCase()) {
                        case "snare":
                            getGod(p).snare(5);
                            return true;
                        case "stun":
                            getGod(p).stun(5);
                            return true;
                        case "taunt":
                            getGod(p).taunt(5, Lib.getEntityByName(Bukkit.getWorld("world"), "Taunter"));
                            return true;
                        case "ingame":
                            getGod(p).setIngame(getGod(p).isIngame());
                            return true;
                    }
                }
                if (args[0].equalsIgnoreCase("stats")) {
                    Player selectedPlayer = getServer().getPlayer(args[1]);
                    if (selectedPlayer != null) {
                        sender.sendMessage(kpInfo + "Stats:\n" + ChatColor.GRAY + getGod(selectedPlayer).getStatsString());
                    } else {
                        sender.sendMessage(kpInfo + "Could not find player " + args[1]);
                    }
                    return true;

                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("select")) {
                    Kit k;
                    if ((k = getKitByName(args[2])) != null) {
                        getGod(p).setKit(k);
                        sender.sendMessage(kpInfo + "Selected " + k.getDispName() + " kit");
                        return true;
                    }
                    sender.sendMessage(kpInfo + "Couldn't find kit " + args[2]);
                    StringBuilder available = new StringBuilder();
                    available.append("{");
                    kits.forEach((kit) -> available.append(kit.getDispName() + ", "));
                    String av = available.toString().substring(0, available.length() - 2) + "}";
                    sender.sendMessage(kpInfo + "Available kits are: " + av);
                    return true;
                }
                if (args[0].equalsIgnoreCase("team") && args[1].equalsIgnoreCase("set")) {
                    getGod(p).setTeam(teams.get(Integer.parseInt(args[2])));
                    sender.sendMessage(kpInfo + ChatColor.GREEN + "You joined the " + getGod(p).getTeam().getName() + " team");
                    return true;
                }
            }
        }

        return false;
    }

    public static Kit getKitByName(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        player.sendMessage(kpInfo + "Welcome to the War of Gods " + player.getName());
        if(!gods.containsKey(player))
            loadGodFromJson(player);
    }

    public static God getGod(Player player) {
        if (!gods.containsKey(player))
            return loadGodFromJson(player);
        return gods.get(player);
    }

    public static Team getTeam(String name) {
        for (Team team : teams)
            if (team.getName().equals(name))
                return team;
        return teams.get(0);
    }

    public static Map getMap(String name) {
        for (Map map : maps)
            if (map.getName().equals(name))
                return map;
        return null;
    }

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(kpInfo + msg);
    }

    public static void error(String err) {
        Bukkit.broadcastMessage(kpError + err);
    }
}
