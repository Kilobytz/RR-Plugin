package io.github.Kilobytz.rrstuff.misc;

import io.github.Kilobytz.rrstuff.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.v1_12_R1.projectiles.CraftBlockProjectileSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

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
    public void death(PlayerDeathEvent event) {
        if(event.getEntity().getLastDamageCause() instanceof Snowball) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler
    public void snowballHit(EntityDamageByEntityEvent event) {
        if(event.getCause() == DamageCause.PROJECTILE){
            if(event.getDamager() instanceof Snowball){
                if(event.getEntity() instanceof Player && enabled == true){
                    event.setDamage(100);
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
