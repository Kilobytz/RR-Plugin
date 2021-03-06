package io.github.Kilobytz.rrstuff.packetshit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.Kilobytz.rrstuff.Main;
import io.github.Kilobytz.rrstuff.packetshit.protocol.Reflection;
import io.github.Kilobytz.rrstuff.packetshit.protocol.TinyProtocol;
import io.netty.channel.Channel;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.PlayerInfoData;
public class VanishInit {

    Main main;
    private final List<UUID> vanishedPlayers = new ArrayList<>();
    TinyProtocol protocol;
    private Class<?> playerInfoClass = Reflection.getClass("{nms}.PacketPlayOutPlayerInfo");
    private Reflection.FieldAccessor<List> playerInfo = Reflection.getField(this.playerInfoClass, "b", List.class);
    private Reflection.FieldAccessor<?> packetEnum = Reflection.getField(this.playerInfoClass, "a", EnumPlayerInfoAction.class);

    public VanishInit(Main main) {
        this.main=main;
        
    }




    public void vanishStart() {
        this.protocol = new TinyProtocol((Plugin)this.main) {
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
              if (packet instanceof PacketPlayOutPlayerInfo) {
                  List<PlayerInfoData> packetData = (List<PlayerInfoData>) VanishInit.this.playerInfo.get(packet);
                  if(!packetEnum.get(packet).equals(EnumPlayerInfoAction.REMOVE_PLAYER)) {
                      List<PlayerInfoData> toRemove = new ArrayList<>();
                      for(PlayerInfoData pData : packetData) {
                          if(vanishedPlayers.contains(pData.a().getId()) && !vanishedPlayers.contains(receiver.getUniqueId()) 
                          && !pData.a().getId().equals(receiver.getUniqueId())) {
                              toRemove.add(pData);
                            }
                        }
                        packetData.removeAll(toRemove);
                        VanishInit.this.playerInfo.set(packet, packetData);
                        return packet;
                    }
                }   
                return super.onPacketOutAsync(receiver, channel, packet);
            }
        };
    }
    

    public void vanishPlayer(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        PacketPlayOutPlayerInfo vanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());
        ((LivingEntity)player).setCollidable(false);
        for(Player allPlayers : Bukkit.getOnlinePlayers()) {
            if(!vanishedPlayers.contains(allPlayers.getUniqueId())) {
                ((CraftPlayer) allPlayers).getHandle().playerConnection.sendPacket(vanish);
                allPlayers.hidePlayer(main,player);
            }
            else {
                vanishIntoState(player, allPlayers);
            }
        }

    }
    public void unvanishPlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        PacketPlayOutPlayerInfo unvanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle());
        ((LivingEntity)player).setCollidable(true);
        for(Player allPlayers : Bukkit.getOnlinePlayers()) {
            if(!vanishedPlayers.contains(allPlayers.getUniqueId())) {
                ((CraftPlayer) allPlayers).getHandle().playerConnection.sendPacket(unvanish);
                allPlayers.showPlayer(main,player);
            }
            else {
                vanishOutOfState(player, allPlayers);
            }
        }
    }

    public void vanishIntoState(Player playerVanishing, Player playerToShow) {
        PacketPlayOutPlayerInfo vanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,((CraftPlayer) playerToShow).getHandle());
        ((CraftPlayer) playerVanishing).getHandle().playerConnection.sendPacket(vanish);
        playerVanishing.showPlayer(main,playerToShow);
    }
    public void vanishOutOfState(Player playerUnVanishing, Player playerToHide) {
        PacketPlayOutPlayerInfo unvanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer) playerToHide).getHandle());
        ((CraftPlayer) playerUnVanishing).getHandle().playerConnection.sendPacket(unvanish);
        playerUnVanishing.hidePlayer(main,playerToHide);
    }
    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public void setVanish(Player player) {
        if(vanishedPlayers.contains(player.getUniqueId())) {
            player.sendMessage("You have been un-vanished");
            unvanishPlayer(player);
        }
        else {
            player.sendMessage("You have been vanished");
            vanishPlayer(player);
        }
    }
}
