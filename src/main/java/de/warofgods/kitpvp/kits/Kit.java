package de.warofgods.kitpvp.kits;

import de.warofgods.kitpvp.God;
import de.warofgods.kitpvp.abilities.Ability;
import org.bukkit.Effect;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Kit {

    private static int idCounter;
    protected int id;
    protected transient God owner;
    protected String name;
    protected transient String dispName;
    protected transient String description;
    protected transient List<Ability> abilities;
    protected transient ItemStack[] armor;
    protected transient ItemStack weapon;
    protected transient ItemStack offHand;
    protected transient List<Effect> effects;
    protected transient float movementSpeed;

    public Kit(Kit clone, God owner) {
        this.owner = owner;
        id = clone.id;
        name = clone.name;
        dispName = clone.dispName;
        description = clone.description;
        abilities = clone.abilities.stream().map((a) -> a.clone(owner)).collect(Collectors.toList());
        owner.getPlayer().sendMessage("Your new abilities size is " + abilities.size());
        //abilities = clone.abilities;
        armor = clone.armor;
        weapon = clone.weapon;
        offHand = clone.offHand;
        effects = clone.effects;
        movementSpeed = clone.movementSpeed;
    }

    public Kit(String name, String dispName, String description, List<Ability> abilities, ItemStack[] armor, ItemStack weapon) {
        id = ++idCounter;
        this.name = name;
        this.dispName = dispName;
        this.description = description;
        this.abilities = abilities;
        this.armor = armor;
        this.weapon = weapon;
    }

    public void onPlayerUse(PlayerInteractEvent e){
        for(Ability a : abilities)
            a.onPlayerUse(e);
    }

    public void onPlayerSlotChange(PlayerItemHeldEvent e) {
        for(Ability a : abilities)
            a.onPlayerSlotChange(e);
    }

    public void onPlayerDamageDealt(EntityDamageByEntityEvent event) {
        for(Ability a : abilities)
            a.onPlayerDamageDealt(event);
    }

    public void onPlayerDamageTaken(EntityDamageByEntityEvent event) {
        for(Ability a : abilities)
            a.onPlayerDamageTaken(event);
    }


    public abstract Kit clone(God owner);


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Kit setName(String name) {
        this.name = name;
        return this;
    }

    public String getDispName() {
        return dispName;
    }

    public Kit setDispName(String dispName) {
        this.dispName = dispName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Kit setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public Kit setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
        return this;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public Kit setArmor(ItemStack[] armor) {
        this.armor = armor;
        return this;
    }

    public ItemStack getWeapon() {
        return weapon;
    }

    public Kit setWeapon(ItemStack weapon) {
        this.weapon = weapon;
        return this;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public Kit setOffHand(ItemStack offHand) {
        this.offHand = offHand;
        return this;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public Kit setEffects(List<Effect> effects) {
        this.effects = effects;
        return this;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public Kit setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
        return this;
    }
}
