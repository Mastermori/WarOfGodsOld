package de.warofgods.kitpvp;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TimeoutVar {

    private int tupdate;
    private int tfactor;

    private BossBar bar;
    private BukkitRunnable task;
    private int timer;
    private int timerStart;

    public TimeoutVar(){
        this(null, "", null);
    }

    public TimeoutVar(God owner, String text, BarColor color) {
        this(owner, text, color, BarStyle.SOLID);
    }

    public TimeoutVar(God owner, String text, BarColor color, BarStyle style) {
        if(owner != null) {
            bar = Bukkit.createBossBar(text, color, style);
            bar.addPlayer(owner.getPlayer());
        }
        setTupdate(2);
        setVisible(false);
    }

    public void start(float time){
        start(time, true);
    }

    public void start(float time, boolean timerVisible) {
        if (time * tfactor > timer) {
            timer = timerStart = (int) (time * tfactor);
            onStart(timer * tupdate);
            setVisible(timerVisible);
            if (task != null && !task.isCancelled())
                task.cancel();
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (timer > 0) {
                        timer--;
                        //Kitpvp.plugin.getLogger().info("Bossbar started at " + timerStart + " is currently at " + timer + " = " + timer / timerStart);
                        bar.setProgress(timer * 1f / timerStart);
                        onTimer();
                    } else {
                        onTimeout();
                        bar.setProgress(0);
                        bar.setVisible(false);
                        this.cancel();
                    }
                }
            };
            task.runTaskTimer(Kitpvp.plugin, tupdate, tupdate);
        }
    }

    protected abstract void onStart(int time);

    protected abstract void onTimeout();

    protected void onTimer(){}

    public void setVisible(boolean visible) {
        bar.setVisible(visible);
    }

    public void setTitle(String title){
        bar.setTitle(title);
    }

    public TimeoutVar setTupdate(int tupdate) {
        this.tupdate = tupdate;
        tfactor = 20 / tupdate;
        return this;
    }

    public JsonObject getAsJson(){
        JsonObject ret = new JsonObject();
        ret.addProperty("timer", timer);
        return ret;
    }

    public void updateFromJson(JsonObject jsonObject) {
        timer = jsonObject.get("timer").getAsInt();
    }

}
