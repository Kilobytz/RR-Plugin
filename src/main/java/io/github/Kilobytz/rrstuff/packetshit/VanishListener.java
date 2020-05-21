package io.github.Kilobytz.rrstuff.packetshit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishListener implements Listener {

    VanishInit vanishInit;

    public void setVanishData(VanishInit vanishInit) {this.vanishInit = vanishInit;}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        List<UUID> playersVanished = new ArrayList<>(vanishInit.getVanishedPlayers());
        if(playersVanished.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage("You are vanished!");
            event.setJoinMessage(null);
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
