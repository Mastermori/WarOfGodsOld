package de.warofgods.kitpvp;

import com.google.gson.JsonObject;
import de.warofgods.kitpvp.kits.Kit;
import de.warofgods.kitpvp.maps.Map;
import de.warofgods.kitpvp.teams.Team;
import net.dev.nickplugin.api.NickManager;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class God implements Listener {

    private transient final Player player;
    private boolean ingame;
    private Team team;
    private Kit kit;
    private float movementSpeed;

    private transient boolean stunned;
    private TimeoutVar stun;

    private transient boolean snared;
    private TimeoutVar snare;

    private Entity taunter;
    private TimeoutVar taunt;

    public transient float surroundingDamageDealt;
    private transient float damageTaken;
    private transient float damageDealt;

    private transient NickManager nick;

    public God(JsonObject json, Player player){
        this(player);
        this.ingame = json.get("ingame").getAsBoolean();
        if(this.ingame){
            //TODO: Reset
        }
        if(json.has("team"))
            setTeam(Kitpvp.getTeam(json.get("team").getAsJsonObject().get("name").getAsString()));
        /*else
            setTeam(Kitpvp.getTeam("Lobby"));*/
        if(json.has("kit"))
            setKit(Kitpvp.getKitByName(json.get("kit").getAsJsonObject().get("name").getAsString()));
        this.movementSpeed = json.get("movementSpeed").getAsFloat();
        "".lastIndexOf('c');
    }

    public God(Player player) {
        System.out.println();
        this.player = player;
        movementSpeed = player.getWalkSpeed();
        stun = new TimeoutVar(this, "Stunned", BarColor.BLUE) {
            @Override
            protected void onStart(int time) {
                snare.start(time / 20f, false);
                stunned = true;
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, time + 40, 255, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, 255, false, false, false));
                player.sendMessage(ChatColor.BLUE + "You were stunned for " + (time / 20) + " seconds");
            }

            @Override
            protected void onTimeout() {
                stunned = false;
                player.sendMessage(ChatColor.BLUE + "You are no longer snared");
            }
        };
        snare = new TimeoutVar(this, "Snared", BarColor.YELLOW) {
            @Override
            protected void onStart(int time) {
                snared = true;
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 255, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, time, 250, false, false, false));
                player.sendMessage(ChatColor.YELLOW + "You were snared for " + (time / 20) + " seconds");
            }

            @Override
            protected void onTimeout() {
                snared = false;
                player.sendMessage(ChatColor.YELLOW + "You are no longer snared");
            }
        };
        taunt = new TimeoutVar(this, "Taunted", BarColor.RED) {
            @Override
            protected void onStart(int time) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 0, false, false, true));
                player.chat("/execute as @s facing entity @e[nbt={UUIDLeast:" + taunter.getUniqueId().getLeastSignificantBits() + "L,UUIDMost:" + taunter.getUniqueId().getMostSignificantBits() + "L},limit=1] feet run tp ~ ~ ~");
                setTitle("Taunted by " + (taunter instanceof Player ? taunter.getName() : taunter.getCustomName()));
            }

            @Override
            protected void onTimeout() {
                taunter = null;
            }

            @Override
            protected void onTimer() {
                //Lib.lookAt(player, taunter);
            }
        };
        ingame = false;
        resetStats();
        Kitpvp.plugin.getServer().getPluginManager().registerEvents(this, Kitpvp.plugin);
        nick = new NickManager(player);
}

    public void spawn(Map map){

    }

    public void joinTeam() {
        team.join(this);
    }

    public void snare(float time) {
        snare.start(time);
        nick.nickPlayer("Snared");
    }

    public void stun(float time) {
        stun.start(time);
    }


    public void taunt(float time, Entity taunter) {
        this.taunter = taunter;
        taunt.start(time);
    }

    public void resetStats() {
        surroundingDamageDealt = 0;
        damageDealt = 0;
        damageTaken = 0;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(ingame) {
            if (event.getDamager() instanceof Player) {
                God dealer = Kitpvp.getGod((Player) event.getDamager());
                if(dealer == this) {
                    if(event.getEntity() instanceof Player){
                        God taker = Kitpvp.getGod((Player) event.getEntity());
                        if (!dealer.team.canHit(dealer, taker)) {
                            event.setCancelled(true);
                        }
                    }
                    if (dealer.stunned) {
                        event.setCancelled(true);
                    } else if (dealer.taunter != null && !dealer.taunter.equals(event.getEntity())) {
                        event.setCancelled(true);
                    }
                    damageDealt += event.getFinalDamage();
                    kit.onPlayerDamageDealt(event);
                }
            }
            if(event.getEntity() instanceof Player){
                God taker = Kitpvp.getGod((Player) event.getEntity());
                if(taker == this){
                    damageTaken += event.getFinalDamage();
                    kit.onPlayerDamageTaken(event);
                }
            }
            if (player.getLocation().subtract(event.getEntity().getLocation()).length() < 5) {
                surroundingDamageDealt += event.getFinalDamage();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if(ingame) {
            if (event.getEntity() instanceof Player) {
                Player p = (Player) event.getEntity();
                if (p.getName().equals(player.getName())) {
                    resetStats();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if(ingame) {
            Player p = event.getPlayer();
            if (kit != null && p.getName().equals(player.getName())) {
                kit.onPlayerUse(event);
            }
        }
    }

    @EventHandler
    public void onPlayerSlotChange(PlayerItemHeldEvent event){
        if(ingame) {
            Player p = event.getPlayer();
            if (kit != null && p.getName().equals(player.getName())) {
                kit.onPlayerSlotChange(event);
            }
        }
    }

    public String getStatsString() {
        return "surroundingDamageDealt = " + surroundingDamageDealt + "\ndamageDealt = " + damageDealt + "\ndamageTaken = " + damageTaken;
    }

    public JsonObject getAdditionalJson(){
        JsonObject json = new JsonObject();
        return json;
    }

    public void setMovementSpeed(float amount) {
        movementSpeed = amount;
        player.setWalkSpeed(amount);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean is(Player p){
        return p != null && p.getName().equals(player.getName());
    }

    public boolean isInTeam(Team t){
        return t.equals(team);
    }

    public Kit getKit() {
        return kit;
    }

    public God setKit(Kit kit) {
        this.kit = kit.clone(this);
        return this;
    }

    public Team getTeam() {
        return team;
    }

    public God setTeam(Team team) {
        this.team = team;
        return this;
    }

    public boolean isSnared() {
        return snared;
    }

    public boolean isStunned() {
        return stunned;
    }

    public Entity getTaunter() {
        return taunter;
    }

    public float getSurroundingDamageDealt() {
        return surroundingDamageDealt;
    }

    public float getDamageTaken() {
        return damageTaken;
    }

    public float getDamageDealt() {
        return damageDealt;
    }

    public boolean isIngame() {
        return ingame;
    }

    public God setIngame(boolean ingame) {
        this.ingame = ingame;
        return this;
    }
}
