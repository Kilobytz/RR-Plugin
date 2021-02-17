package io.github.Kilobytz.rrstuff.mole;

import org.apache.commons.lang.ObjectUtils.Null;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.Kilobytz.rrstuff.Main;
import net.md_5.bungee.api.ChatColor;

public class MoleListener implements Listener {

    MoleHandling moleHandling;
    Main main;

    public MoleListener(Main main) {
        this.main = main;
    }

    public void setMole() {
        this.moleHandling = new MoleHandling(main);
        moleHandling.moleStart();
    }

    public boolean isMoleOn() {
        return moleHandling != null;
    }
    public void stopMole() {
        moleHandling.shutItAllDown();
        moleHandling = null;
    }

    
    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        String chat = event.getMessage();
        String name = event.getPlayer().getDisplayName();
        event.setCancelled(true);
        try {
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
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
        Bukkit.broadcastMessage("<" + name +"> " + chat);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String jM = event.getJoinMessage();
        event.setJoinMessage(null);
        try{
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
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
        }catch(NullPointerException e) {event.setJoinMessage(ChatColor.YELLOW + jM);}
            
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        String kM = event.getLeaveMessage();
        event.setLeaveMessage(null);
        try{
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + kM);
                moleHandling.getInstanceFromPlayer(event.getPlayer()).emptyRangeCheck();
                for (Player playersOnline : Bukkit.getOnlinePlayers()) {
                    if (playersOnline.equals(event.getPlayer()) || 
                    (moleHandling.doesMoleContainID(playersOnline.getUniqueId())
                    && !moleHandling.checkDistanceRaw(event.getPlayer().getUniqueId(), playersOnline.getUniqueId()))
                    || !playersOnline.getWorld().equals(event.getPlayer().getWorld())) {
                        continue;
                        }
                        playersOnline.sendMessage(ChatColor.YELLOW + kM);
                }
            }
        }catch(NullPointerException e) {event.setLeaveMessage(ChatColor.YELLOW + kM);}
        
    }
    

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        String jM = event.getQuitMessage();
        event.setQuitMessage(null);
        try{
            if (moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + jM);
                moleHandling.getInstanceFromPlayer(event.getPlayer()).emptyRangeCheck();
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
        }catch(NullPointerException e) {event.setQuitMessage(ChatColor.YELLOW + jM);}
        
    }

    @EventHandler
    public void leaving(PlayerQuitEvent event) {
        try{
        if(moleHandling.doesMoleContainID(event.getPlayer().getUniqueId())) {
            moleHandling.getInstanceFromPlayer(event.getPlayer()).emptyRangeCheck();
            for(Player players : Bukkit.getOnlinePlayers()) {
                if(moleHandling.doesMoleContainID(players.getUniqueId())) {
                    moleHandling.getInstanceFromPlayer(event.getPlayer()).removeRangeCheck(moleHandling.getInstanceFromPlayer(event.getPlayer()));
                }
            }
        }
    }catch(NullPointerException e) {
    }
    }
    

    
    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        String jM = event.getDeathMessage();
        event.setDeathMessage(null);
        try{
            if (moleHandling.doesMoleContainID(event.getEntity().getUniqueId())) {
                event.getEntity().sendMessage(jM);
                for (Player playersOnline : Bukkit.getOnlinePlayers()) {
                    if (playersOnline.equals(event.getEntity()) || 
                    (moleHandling.doesMoleContainID(playersOnline.getUniqueId())
                    && !moleHandling.checkDistanceRaw(event.getEntity().getUniqueId(), playersOnline.getUniqueId()))
                    || !playersOnline.getWorld().equals(event.getEntity().getWorld())) {
                        continue;
                        }
                        playersOnline.sendMessage(jM);
                        
                }
                return;
            }
        }catch(NullPointerException e) {
        }
        event.setDeathMessage(jM);
    }
    

    /*@EventHandler
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
    */
}
