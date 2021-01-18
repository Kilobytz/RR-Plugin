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




    /*public void vanishStart() {
        this.protocol = new TinyProtocol((Plugin)this.main) {
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
              if (packet instanceof PacketPlayOutPlayerInfo) {
                  if(!((EnumPlayerInfoAction)VanishInit.this.packetEnum.get(packet)).equals(EnumPlayerInfoAction.REMOVE_PLAYER)) {
                      if(((PacketPlayOutPlayerInfo.PlayerInfoData)VanishInit.this.playerInfo.get(packet)).a().getId().equals(receiver.getUniqueId()) ||
                      vanishedPlayers.contains(receiver.getUniqueId())) {
                          return super.onPacketOutAsync(receiver, channel, packet);
                        }
                        if(vanishedPlayers.contains(((PacketPlayOutPlayerInfo.PlayerInfoData)VanishInit.this.playerInfo.get(packet)).a().getId())) {
                            return null;
                        }
                    }
                }   
                return super.onPacketOutAsync(receiver, channel, packet);
            }
        };
    }
    */

    public String getUUID(String packetData) {
        Pattern pattern = Pattern.compile("\\[id=(\\w{8}(?:-\\w{4}){3}-\\w{12})");
        Matcher matcher = pattern.matcher(packetData);

        if(matcher.find()) {
            return matcher.group(1);
        }
        return null;
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
