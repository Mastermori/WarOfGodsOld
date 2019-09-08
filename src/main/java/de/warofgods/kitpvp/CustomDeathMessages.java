package de.warofgods.kitpvp;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Random;

public class CustomDeathMessages {
    private static final Random random = new Random();
    private static final String[] bashMessages = new String[]{"%killed% was bashed to death by %killer%", "%killed% was killed by %killer% using the force of his shield", "%killer% bashed %killed% to the next dimension", "%killed%'s skull was crushed by %killer%'s shield", "A lethal dose of %killer%'s shield was applied to %killed%"};
    public static HashMap<Entity, DeathType> deadEntities = new HashMap<>();


    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            if (deadEntities.get(event.getEntity()) == DeathType.Bashed) {
                sendDeathMessage(event, bashMessages);
                deadEntities.put(event.getEntity(), DeathType.None);
            }

        }
    }

    public void sendDeathMessage(PlayerDeathEvent event, String[] possibleMessages){
        String deathMsg = possibleMessages[random.nextInt(possibleMessages.length)];
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();
        event.setDeathMessage(deathMsg.replace("%killed%", e.getEntity().getName()).replace("%killer%", e.getDamager().getName()));
    }

    public enum DeathType {
        None,
        Bashed,

    }
}
