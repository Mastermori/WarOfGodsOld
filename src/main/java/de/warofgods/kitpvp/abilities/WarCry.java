package de.warofgods.kitpvp.abilities;

import de.warofgods.kitpvp.God;
import de.warofgods.kitpvp.Kitpvp;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class WarCry extends Ability {

    public WarCry(Ability old, God owner){
        super(old, owner);
    }

    public WarCry(int slot) {
        super("war_cry", "War Cry", ActivateType.Slot, 10);
        setSlot(slot);
    }

    @Override
    public void activate(PlayerEvent e) {
        List<Entity> nearby = owner.getPlayer().getNearbyEntities(2.5, 2.5, 2.5);
        owner.getPlayer().sendMessage(getStylizedName() + " " + nearby.size() + " entities nearby");
        int tauntCount = 0;
        for(Entity entity : nearby){
            if(entity instanceof Player){
                God god = Kitpvp.getGod((Player) entity);
                if(!god.isInTeam(owner.getTeam())) {
                    god.taunt(5, owner.getPlayer());
                    tauntCount++;
                }
            }
        }
        if(tauntCount > 0)
            owner.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7*20, tauntCount/2, false, false, true));
    }

    @Override
    public boolean isAvailable() {
        return available && !owner.isStunned();
    }

    @Override
    public Ability clone(God owner) {
        return new WarCry(this, owner);
    }
}
