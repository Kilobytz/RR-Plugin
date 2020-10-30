package io.github.Kilobytz.rrstuff.mole;

import com.mojang.authlib.GameProfile;
import io.github.Kilobytz.rrstuff.Main;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MolePlayer {

    UUID userID;
    boolean isMole;
    GameProfile moleProfile;
    GameProfile normalProfile;
    Main main;
    boolean diffDim = true;
    private List<UUID> inRange = new ArrayList<>();

    public MolePlayer(Player player,Main main) {
        this.userID = player.getUniqueId();
        this.main = main;
        this.normalProfile = ((CraftPlayer) player).getProfile();
    }
    public void setMole(boolean mole) {
        this.isMole = mole;
        if(mole) {
            setMoleProfile();
        }

    }
    public boolean isMole(Player player) {
            return isMole;
    }
    public Player getMolePlayer() {
        return Bukkit.getPlayer(userID);
    }
    public void setMoleProfile() {
        this.moleProfile = new GameProfile(userID, ChatColor.RED + Bukkit.getPlayer(userID).getDisplayName());
    }
    public GameProfile getMoleProfile() {
        return moleProfile;
    }
    public GameProfile getNormalProfile() {
        return normalProfile;
    }

    public boolean inRangeCheck(MolePlayer mP) {
        try {
            return inRange.contains(mP.getMolePlayer().getUniqueId());
        }catch (NullPointerException e) {
            return false;
        }
    }
    public boolean anyInRange() {
        if(inRange.size() == 0) {
            return false;
        }
        return true;
    }
    public void setRangeCheck(MolePlayer mP) {
        inRange.add(mP.getMolePlayer().getUniqueId());
    }
    public void removeRangeCheck(MolePlayer mP) {
        inRange.remove(mP.getMolePlayer().getUniqueId());
    }

    public UUID getMoleID() {
        return userID;
    }

    public void setDim(boolean status) {
        this.diffDim = status;
    }

    public void emptyRangeCheck() {
        inRange.clear();
    }
    //public void createMoleEntity()
}
