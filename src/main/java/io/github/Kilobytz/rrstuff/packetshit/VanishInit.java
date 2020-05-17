package io.github.Kilobytz.rrstuff.packetshit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import io.github.Kilobytz.rrstuff.Main;
import io.github.Kilobytz.rrstuff.packetshit.wrapperstuff.WrapperPlayServerPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VanishInit {

    Main main;
    private final List<UUID> vanishedPlayers = new ArrayList<>();

    public VanishInit(Main main) {
        this.main=main;
    }




    public void vanishStart() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(

                main,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerPlayerInfo wPacket = new WrapperPlayServerPlayerInfo(packet);
                    EnumWrappers.PlayerInfoAction packetAction = wPacket.getAction();
                    List<PlayerInfoData> playerIDs = new ArrayList<>(wPacket.getData());
                    if(packetAction.equals(EnumWrappers.PlayerInfoAction.ADD_PLAYER) ||packetAction.equals(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY)) {
                        String rawUUID = getUUID(playerIDs.get(0).toString());
                        UUID uuid = UUID.fromString(rawUUID);
                        if(uuid.equals(event.getPlayer().getUniqueId())) {
                            return;
                        }
                        UUID recipientID = event.getPlayer().getUniqueId();
                        if(vanishedPlayers.contains(recipientID)) {
                            return;
                        }
                        if(vanishedPlayers.contains(uuid)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }catch(FieldAccessException e) {
                    e.printStackTrace();
                    Bukkit.getServer().getConsoleSender().sendMessage("shit broke yo");
                }
            }
        });
    }

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
        net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo vanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,nmsPlayer);
        for(Player allPlayers : Bukkit.getOnlinePlayers()) {
            if(!vanishedPlayers.contains(allPlayers.getUniqueId())) {
                ((CraftPlayer) allPlayers).getHandle().playerConnection.sendPacket(vanish);
                allPlayers.hidePlayer(player);
            }
            else {
                vanishIntoState(player, allPlayers);
            }
        }

    }
    public void unvanishPlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo unvanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,nmsPlayer);
        for(Player allPlayers : Bukkit.getOnlinePlayers()) {
            if(!vanishedPlayers.contains(allPlayers.getUniqueId())) {
                ((CraftPlayer) allPlayers).getHandle().playerConnection.sendPacket(unvanish);
                allPlayers.showPlayer(player);
            }
            else {
                vanishOutOfState(player, allPlayers);
            }
        }
    }

    public void vanishIntoState(Player playerVanishing, Player playerToShow) {
        net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) playerToShow).getHandle();
        PacketPlayOutPlayerInfo vanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,nmsPlayer);
        ((CraftPlayer) playerVanishing).getHandle().playerConnection.sendPacket(vanish);
        playerVanishing.showPlayer(playerToShow);
    }
    public void vanishOutOfState(Player playerUnVanishing, Player playerToHide) {
        net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) playerToHide).getHandle();
        PacketPlayOutPlayerInfo unvanish = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,nmsPlayer);
        ((CraftPlayer) playerUnVanishing).getHandle().playerConnection.sendPacket(unvanish);
        playerUnVanishing.hidePlayer(playerToHide);
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
