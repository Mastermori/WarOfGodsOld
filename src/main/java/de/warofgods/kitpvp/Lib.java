package de.warofgods.kitpvp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Lib {

    public static void lookAt(Entity target, Entity source) {
        Vector direction = getVector(target).subtract(getVector(source)).normalize();
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();

        // Now change the angle
        Location changed = target.getLocation().clone();
        changed.setYaw(180 - toDegree(Math.atan2(x, z)));
        changed.setPitch(90 - toDegree(Math.acos(y)));
        target.teleport(changed);
    }

    private static float toDegree(double angle) {
        return (float) Math.toDegrees(angle);
    }

    private static Vector getVector(Entity entity) {
        if (entity instanceof Player)
            return ((Player) entity).getEyeLocation().toVector();
        else
            return entity.getLocation().toVector();
    }

    public static Entity getEntityByName(World w, String name) {
        for(Entity e : w.getEntities())
            if(name.equals(e.getCustomName()))
                return e;
        return null;
    }

    public static boolean inRange(int x, int min, int max){
        return (x >= min && x < max);
    }

    public static boolean inRange(float x, float min, float max){
        return (x >= min && x < max);
    }

}
