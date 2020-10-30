package io.github.Kilobytz.rrstuff.mole;

import io.github.Kilobytz.rrstuff.Main;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoleListener implements Listener {

    MoleHandling moleHandling;
    Main main;
    List<UUID> moleList = new ArrayList<>();

    public MoleListener(Main main) {
        this.main = main;
    }

    public void setMole() {
        this.moleHandling = new MoleHandling(main);
        moleHandling.moleStart();
        addMoleList();
    }

    public boolean isMoleOn() {
        return moleHandling != null;
    }
    public void stopMole() {
        moleHandling.shutItAllDown();
        moleHandling = null;
        delayedShutdown();
    }

    public void addMoleList() {
        moleList = moleHandling.getMolePlayers();
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        String chat = event.getMessage();
        String name = event.getPlayer().getDisplayName();
        try {
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                for (Player playersOnline : Bukkit.getOnlinePlayers()) {
                    if (!playersOnline.equals(event.getPlayer())
                            && playersOnline.getWorld().equals(event.getPlayer().getWorld())
                            && moleHandling.doesMoleContainID(playersOnline.getUniqueId())
                            && moleHandling.checkDistanceRaw(event.getPlayer().getUniqueId(), playersOnline.getUniqueId())) {
                        playersOnline.sendMessage(name + ": " + chat);
                        }
                }
                event.getPlayer().sendMessage(name + ": " + chat);
                return;
            }
        }catch (NullPointerException ignored) {}
        event.setCancelled(true);
        for(Player players : Bukkit.getOnlinePlayers()) {
            players.getPlayer().sendMessage(name +": " + chat);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
                String jM = event.getJoinMessage();
                event.setJoinMessage(null);
                event.getPlayer().sendMessage(ChatColor.YELLOW + jM);
                for (Player playersOnline : Bukkit.getOnlinePlayers()) {
                    if (playersOnline.equals(event.getPlayer()) || 
                    (moleHandling.doesMoleContainID(playersOnline.getUniqueId())
                    && !moleHandling.checkDistanceRaw(event.getPlayer().getUniqueId(), playersOnline.getUniqueId()))
                    || !playersOnline.getWorld().equals(event.getPlayer().getWorld())) {
                        continue;
                        }
                        playersOnline.sendMessage(ChatColor.YELLOW + jM);
                }
            }

        }catch (NullPointerException ignored) {
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        try {
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
                String qM = event.getQuitMessage();
                event.setQuitMessage(null);
                event.getPlayer().sendMessage(ChatColor.YELLOW + qM);
                for (Player playersOnline : Bukkit.getOnlinePlayers()) {
                    if (playersOnline.equals(event.getPlayer()) || 
                    (moleHandling.doesMoleContainID(playersOnline.getUniqueId())
                    && !moleHandling.checkDistanceRaw(event.getPlayer().getUniqueId(), playersOnline.getUniqueId()))
                    || !playersOnline.getWorld().equals(event.getPlayer().getWorld())) {
                        continue;
                        }
                        playersOnline.sendMessage(ChatColor.YELLOW + qM);
                }
            }

        }catch (NullPointerException ignored) {
        }
    }
    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        try {
            if (moleHandling.doesMoleContainID(event.getEntity().getUniqueId())) {
                String dM = event.getDeathMessage();
                event.setDeathMessage(null);
                event.getEntity().sendMessage(dM);
                for (Player playersOnline : Bukkit.getOnlinePlayers()) {
                    if (playersOnline.equals(event.getEntity()) || 
                    (moleHandling.doesMoleContainID(playersOnline.getUniqueId())
                    && !moleHandling.checkDistanceRaw(event.getEntity().getUniqueId(), playersOnline.getUniqueId()))
                    || !playersOnline.getWorld().equals(event.getEntity().getWorld())) {
                        continue;
                        }
                        playersOnline.sendMessage(dM);
                }
            }

        }catch (NullPointerException ignored) {
        }
    }

    public void delayedShutdown() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, this::cleanupMoles, 100L);
    }
    public void cleanupMoles() {
        for(UUID mole1 : moleList) {
            net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) Bukkit.getPlayer(mole1)).getHandle();
            PacketPlayOutPlayerInfo renderPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nmsPlayer);
            for(UUID mole2 : moleList) {
                if(!mole1.equals(mole2)) {
                    if(Bukkit.getPlayer(mole2).isOnline()) {
                        ((CraftPlayer) Bukkit.getPlayer(mole2)).getHandle().playerConnection.sendPacket(renderPacket);
                        Bukkit.getPlayer(mole2).showPlayer(Bukkit.getPlayer(mole1));
                    }
                }
            }

        }
        moleList.clear();
    }
    @EventHandler
    public void switchDim(PlayerChangedWorldEvent event) {
        try {
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
                MolePlayer mp = moleHandling.getInstanceFromPlayer(event.getPlayer());
                for(MolePlayer mole : moleHandling.molePlayers) {
                    if(mp != mole) {
                        if(mole.inRangeCheck(mp)) {
                            moleHandling.rangeVerify(mole);
                        }
                    }
                }
            }
        }catch (NullPointerException ignored) {
        }
    }
}
