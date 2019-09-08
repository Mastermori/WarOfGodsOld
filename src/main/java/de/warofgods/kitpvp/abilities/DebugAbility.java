package de.warofgods.kitpvp.abilities;

import de.warofgods.kitpvp.God;
import org.bukkit.event.player.PlayerEvent;

public class DebugAbility extends Ability {

    public DebugAbility(int slot) {
        super("debug", "Debug", ActivateType.Slot, 0);
        setSlot(slot);
    }

    @Override
    public void activate(PlayerEvent e) {
        e.getPlayer().sendMessage(getStylizedName() + " You switched the slot to: " + e.getPlayer().getInventory().getHeldItemSlot());
        e.getPlayer().getInventory();
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public Ability clone(God owner) {
        return new DebugAbility(slot);
    }
}
