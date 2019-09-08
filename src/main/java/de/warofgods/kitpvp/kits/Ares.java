package de.warofgods.kitpvp.kits;

import de.warofgods.kitpvp.God;
import de.warofgods.kitpvp.TimeoutVar;
import de.warofgods.kitpvp.abilities.WarCry;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Ares extends Kit {

    private TimeoutVar warBuff;

    private Ares(Kit clone, God owner) {
        super(clone, owner);
        (warBuff = new TimeoutVar(owner, "", BarColor.WHITE) {
            @Override
            protected void onStart(int time) {

            }

            @Override
            protected void onTimeout() {
                owner.surroundingDamageDealt = Math.max(owner.surroundingDamageDealt - 20, 0);
            }

            @Override
            protected void onTimer() {
                if(owner.surroundingDamageDealt > 20){
                    Player p = owner.getPlayer();
                    int strength = (int) owner.surroundingDamageDealt / 20;
                    if(!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10, strength-1, false, false, true));
                    p.spawnParticle(Particle.FLAME, p.getLocation(), 2, 0.5, 0.1, 0.5);
                    if(!p.getInventory().contains(getWarItem(strength)))
                        p.getInventory().setItem(7, new ItemStack(getWarItem(strength), 1));
                }
            }
        }).setTupdate(5);
        warBuff.setVisible(false);
    }

    public Ares() {
        super("ares", "Ares", "The god of war", Arrays.asList(new WarCry(1)), new ItemStack[]{

        }, new ItemStack(Material.IRON_SWORD));
        movementSpeed = 0.17f;
    }

    @Override
    public Kit clone(God owner) {
        return new Ares(this, owner);
    }

    public Material getWarItem(int strength) {
        if(strength < 0){
            return Material.AIR;
        }
        switch(strength){
            case 0:
                return Material.GREEN_DYE;
            case 1:
                return Material.YELLOW_DYE;
            case 2:
                return Material.RED_DYE;
            default:
                return Material.BLACK_DYE;
        }
    }
}
