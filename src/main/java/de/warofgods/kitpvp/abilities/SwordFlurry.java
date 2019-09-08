package de.warofgods.kitpvp.abilities;

import de.warofgods.kitpvp.God;
import org.bukkit.event.player.PlayerEvent;

public class SwordFlurry extends Ability{


    public SwordFlurry(Ability clone, God owner) {
        super(clone, owner);
    }

    public SwordFlurry(int slot) {
        super("sword_flurry", "Sword Flurry", ActivateType.Slot, 10);
        setSlot(slot);
    }

    @Override
    public void activate(PlayerEvent e) {

    }

    @Override
    public boolean isAvailable() {
        return available && !owner.isStunned();
    }

    @Override
    public Ability clone(God owner) {
        return new SwordFlurry(this, owner);
    }
}
