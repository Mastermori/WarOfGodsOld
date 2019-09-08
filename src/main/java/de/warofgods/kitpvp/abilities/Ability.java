package de.warofgods.kitpvp.abilities;

import de.warofgods.kitpvp.God;
import de.warofgods.kitpvp.TimeoutVar;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public abstract class Ability {

    private static int idCounter = 0;
    protected int id;
    protected God owner;
    protected String name;
    protected String dispName;
    protected String stylizedName;
    protected int slot;
    protected ActivateType type;
    protected boolean available;
    protected float cooldown;
    protected TimeoutVar cooldownTask;

    public Ability(Ability clone, God owner){
        this.owner = owner;
        id = clone.id;
        name = clone.name;
        dispName = clone.dispName;
        stylizedName = clone.stylizedName;
        slot = clone.slot;
        type = clone.type;
        cooldown = clone.cooldown;
        available = true;
        cooldownTask = new TimeoutVar(owner, "WarCry", BarColor.WHITE) {
            @Override
            protected void onStart(int time) {
                available = false;
            }

            @Override
            protected void onTimeout() {
                available = true;
            }
        };
    }

    public Ability(String name, String dispName, ActivateType type, float cooldown) {
        id = ++idCounter;
        this.name = name;
        this.dispName = dispName;
        this.stylizedName = ChatColor.GOLD + dispName + ChatColor.GRAY + " - ";
        this.type = type;
        this.cooldown = cooldown;
        available = true;
    }

    public void onPlayerUse(PlayerInteractEvent e) {
        if(!owner.is(e.getPlayer()))
            return;
        boolean run = false;
        switch(type){
            case LeftUse:
                if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
                    run = true;
                break;
            case RightUse:
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                    run = true;
                break;
            case LeftUseOnBlock:
                if(e.getAction() == Action.LEFT_CLICK_BLOCK)
                    run = true;
                break;
            case RightUseOnBlock:
                if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
                    run = true;
                break;
        }
        if(run && isAvailable()) {
            start(e);
        }
    }

    @EventHandler
    public void onPlayerSlotChange(PlayerItemHeldEvent e){
        if(!owner.is(e.getPlayer()))
            return;
        if(type == ActivateType.Slot && e.getNewSlot() == slot){
            e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
            if(isAvailable()) {
                owner.getPlayer().sendMessage(getStylizedName() + " Activated");
                start(e);
            }else{
                owner.getPlayer().sendMessage(getStylizedName() + " " +getUnavailableMessage());
            }
        }
    }

    //Can be overwritten by abilities
    public void onPlayerDamageDealt(EntityDamageByEntityEvent event) {}
    public void onPlayerDamageTaken(EntityDamageByEntityEvent event) {}

    private void start(PlayerEvent e){
        activate(e);
        cooldownTask.start(20);
    }

    public abstract void activate(PlayerEvent e);

    public abstract boolean isAvailable();

    public abstract Ability clone(God owner);

    protected String getUnavailableMessage(){
        return ChatColor.RED + "You can't use this right now";
    }

    public String getName() {
        return name;
    }

    public Ability setName(String name) {
        this.name = name;
        return this;
    }

    public String getDispName() {
        return dispName;
    }

    public Ability setDispName(String dispName) {
        this.dispName = dispName;
        return this;
    }

    public String getStylizedName() {
        return stylizedName;
    }

    public Ability setStylizedName(String stylizedName) {
        this.stylizedName = stylizedName;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Ability setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public enum ActivateType {
        Slot,
        RightUse,
        LeftUse,
        LeftUseOnBlock,
        RightUseOnBlock,
    }
}
