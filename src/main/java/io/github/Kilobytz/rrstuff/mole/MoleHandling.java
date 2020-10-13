package io.github.Kilobytz.rrstuff.mole;

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
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoleHandling {

    public MoleHandling(Main main) {
        this.main = main;
    }

    List<MolePlayer> molePlayers = new ArrayList<>();

    int moleCount;
    Main main;
    int limit = 60;
    int distTimerID;

    public void moleStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                molePlayers.add(new MolePlayer(player, main));
                player.sendMessage("Setting moles, please wait.");
            }
        }
        if (molePlayers.size() <= 8) {
            moleCount = 1;
        }
        if (molePlayers.size() > 8 && molePlayers.size() <= 12) {
            moleCount = 2;
        }
        if (molePlayers.size() > 12 && molePlayers.size() <= 20) {
            moleCount = 3;
        }
        if(molePlayers.size() > 20) {
            moleCount = 4;
        }

        poof();
        setMoles();
        ominiousTimer();
        distanceTimer();
        moleListen();
    }

    public void shutItAllDown() {
        Bukkit.getServer().getScheduler().cancelTask(distTimerID);
        for(MolePlayer mP1 : molePlayers) {
            net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) mP1.getMolePlayer()).getHandle();
            PacketPlayOutPlayerInfo unrenderPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nmsPlayer);
            for(MolePlayer mP2 : molePlayers) {
                if(!mP1.equals(mP2)) {
                    ((CraftPlayer) mP2.getMolePlayer()).getHandle().playerConnection.sendPacket(unrenderPacket);
                    mP2.getMolePlayer().hidePlayer(mP1.getMolePlayer());
                }
            }
        }
        molePlayers.clear();
    }

    public List<UUID> getMolePlayers() {
        List<UUID> moleList = new ArrayList<>();
        for(MolePlayer moles : molePlayers) {
            moleList.add(moles.getMoleID());
        }
        return moleList;
    }

    public void setMoles() {
            for (int i = 0; i < moleCount; ) {
                double rand = Math.random() * (molePlayers.size() - 1);
                int randomer = (int) Math.round(rand);
                if (!molePlayers.get(randomer).isMole) {
                    molePlayers.get(randomer).setMole(true);
                    ++i;
                }
            }
    }

    public List<MolePlayer> getMoles() {
        List<MolePlayer> moles = new ArrayList<>();
        for (MolePlayer moleP : molePlayers) {
            if (moleP.isMole) {
                moles.add(moleP);
            }
        }
        return moles;
    }

    public void ominiousTimer() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, this::fixThisCrap, 100L);
    }


    public void distanceTimer() {
        this.distTimerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this::checkDistances, 100L, 5L);
    }


    public void checkDistances() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (doesMoleContainID(players.getUniqueId())) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (!player1.equals(players) && doesMoleContainID(player1.getUniqueId())) {
                        if (players.getWorld().equals(player1.getWorld())) {
                            double distance = players.getLocation().distance(player1.getLocation());
                            if (distance > limit) {
                                unrenderPlayer(players, player1);
                            } else {
                                renderPlayer(players, player1);
                            }
                            continue;
                        }
                        unrenderPlayer(players, player1);
                    }

                }
            }
        }
    }

    public boolean checkDistanceRaw(UUID uuid1, UUID uuid2) {
        Player player1 = Bukkit.getPlayer(uuid1);
        Player player2 = Bukkit.getPlayer(uuid2);

        if (player1.getWorld().equals(player2.getWorld())) {
            double distance = player1.getLocation().distance(player2.getLocation());
            return !(distance > limit);

        }
        return false;
    }

    public void poof() {
        for (MolePlayer mPlayers : molePlayers) {
            mPlayers.getMolePlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        }
    }

    public void fixThisCrap() {
        List<MolePlayer> moles = new ArrayList<>();
        for (MolePlayer mP : molePlayers) {
            if (mP.isMole) {
                mP.getMolePlayer().sendMessage("You are a mole!");
                moles.add(mP);
            }
            else {
                mP.getMolePlayer().sendMessage("You are not a mole!");
            }
            for(Player players : Bukkit.getOnlinePlayers()) {
                if(doesMoleContainID(players.getUniqueId()) && !players.equals(mP.getMolePlayer())) {
                    mP.setRangeCheck(getInstanceFromPlayer(players));
                    if (!checkDistanceRaw(mP.getMolePlayer().getUniqueId(), players.getUniqueId())) {
                        unrenderPlayer(mP.getMolePlayer(), players);
                        }

                }
            }
        }
        for(MolePlayer literalMoles : moles) {
            sendMoleStatement(literalMoles.getMolePlayer(),moles);
        }
    }

    public void sendMoleStatement(Player player, List<MolePlayer> moles) {
        if(moleCount == 1) {
            player.sendMessage("You are the only mole.");
            return;
        }
        if(moleCount == 2) {
            player.sendMessage("Current moles: " + moles.get(0).getMolePlayer().getDisplayName() + ", " + moles.get(1).getMolePlayer().getDisplayName() );
            return;
        }
        if(moleCount == 3) {
            player.sendMessage("Current moles: " + moles.get(0).getMolePlayer().getDisplayName() + ", " + moles.get(1).getMolePlayer().getDisplayName() + ", " + moles.get(2).getMolePlayer().getDisplayName());
            return;
        }
        if(moleCount == 4) {
            player.sendMessage("Current moles: " + moles.get(0).getMolePlayer().getDisplayName() + ", " + moles.get(1).getMolePlayer().getDisplayName() + ", " + moles.get(2).getMolePlayer().getDisplayName() + ", " + moles.get(3).getMolePlayer().getDisplayName());
        }
    }

    public MolePlayer getInstanceFromPlayer(Player player) {
        try {
            for (MolePlayer mP : molePlayers) {
                if (mP.getMolePlayer().equals(player)) {
                    return mP;
                }
            }
            return null;
        }catch (NullPointerException e) {
            return null;
        }
    }

    public void renderPlayer(Player player1, Player player2) {
        try {
            MolePlayer mP1 = getInstanceFromPlayer(player1);
            MolePlayer mP2 = getInstanceFromPlayer(player2);
            if (mP1.inRangeCheck(mP2)) {
                return;
            }
            net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) mP2.getMolePlayer()).getHandle();
            PacketPlayOutPlayerInfo renderPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nmsPlayer);
            ((CraftPlayer) player1).getHandle().playerConnection.sendPacket(renderPacket);
            player1.showPlayer(player2);
            mP1.setRangeCheck(mP2);
        }catch (NullPointerException ignored) {
        }
    }

    public void unrenderPlayer(Player player1, Player player2) {
        try {
            MolePlayer mP1 = getInstanceFromPlayer(player1);
            MolePlayer mP2 = getInstanceFromPlayer(player2);
            if(!mP1.inRangeCheck(mP2)) {
                return;
            }
            net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer) mP2.getMolePlayer()).getHandle();
            PacketPlayOutPlayerInfo unrenderPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nmsPlayer);
            ((CraftPlayer) player1).getHandle().playerConnection.sendPacket(unrenderPacket);
            player1.hidePlayer(player2);
            mP1.removeRangeCheck(mP2);
        }catch (NullPointerException ignored) {
        }
    }

    public void moleListen() {
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
                    if (packetAction.equals(EnumWrappers.PlayerInfoAction.ADD_PLAYER) || packetAction.equals(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY)) {
                        String rawUUID = getUUID(playerIDs.get(0).toString());
                        UUID uuid = UUID.fromString(rawUUID);
                        if (uuid.equals(event.getPlayer().getUniqueId())) {
                            return;
                        }
                        UUID recipientID = event.getPlayer().getUniqueId();
                        if (doesMoleContainID(uuid) && doesMoleContainID(recipientID)) {
                            if (checkDistanceRaw(uuid, recipientID)) {
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                } catch (FieldAccessException e) {
                    e.printStackTrace();
                    Bukkit.getServer().getConsoleSender().sendMessage("shit broke yo");
                }
            }
        });
    }

    public String getUUID(String packetData) {
        Pattern pattern = Pattern.compile("\\[id=(\\w{8}(?:-\\w{4}){3}-\\w{12})");
        Matcher matcher = pattern.matcher(packetData);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean doesMoleContainID(UUID uuid) {
        try {
            for (MolePlayer mP : molePlayers) {
                if (mP.getMolePlayer().getUniqueId().equals(uuid)) {
                    return true;
                }
            }
            return false;
        }catch (NullPointerException e) {
            return false;
        }
    }
}
