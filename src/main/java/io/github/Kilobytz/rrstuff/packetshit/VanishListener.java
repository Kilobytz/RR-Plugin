package io.github.Kilobytz.rrstuff.packetshit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishListener implements Listener {

    VanishInit vanishInit;
    List<UUID> playersVanished = new ArrayList<>();

    public void setVanishData(VanishInit vanishInit) {
        this.vanishInit = vanishInit;   
        vanishInit.getVanishedPlayers();}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(playersVanished.contains(event.getPlayer().getUniqueId())) {
            ((LivingEntity)event.getPlayer()).setCollidable(false);
            event.getPlayer().sendMessage("You are vanished!");
            event.setJoinMessage(null);
        }
        else {
            ((LivingEntity)event.getPlayer()).setCollidable(true);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        List<UUID> playersVanished = new ArrayList<>(vanishInit.getVanishedPlayers());
        if(playersVanished.contains(event.getPlayer().getUniqueId())) {
            event.setQuitMessage(null);
        }

    }


}
