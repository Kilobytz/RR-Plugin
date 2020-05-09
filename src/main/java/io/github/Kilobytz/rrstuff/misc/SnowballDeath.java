package io.github.Kilobytz.rrstuff.misc;

import io.github.Kilobytz.rrstuff.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SnowballDeath implements Listener {
    Main main;
    boolean enabled;

    public SnowballDeath(Main main) {
        this.main = main;
        try{
            this.enabled = main.getConfig().getBoolean("SnowballDeathEnabled = ");
        }catch(NullPointerException e) {
            enabled = false;
        }

    }

    @EventHandler
    public void snowballDeath(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Snowball) {
            if(event.getEntity() instanceof Player) {
                if(enabled) {
                    ((Player) event.getEntity()).setHealth(0);
                }
            }
        }
    }

    public boolean getSnowballStatus() {
        return enabled;
    }

    public void setSnowballStatus(Boolean status) {
        enabled = status;
        main.getConfig().set("SnowballDeathEnabled = " , status);
        main.saveConfig();
    }
}
