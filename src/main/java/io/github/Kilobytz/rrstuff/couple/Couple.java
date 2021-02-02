package io.github.Kilobytz.rrstuff.couple;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Couple {

    private UUID couple1;
    private UUID couple2;

    public void setCouple(UUID playerID1, UUID playerID2) {
        this.couple1 = playerID1;
        this.couple2 = playerID2;
    }
    public void setCouple(Player player1, Player player2) {
        this.couple1 = player1.getUniqueId();
        this.couple2 = player2.getUniqueId();
    }
    public boolean contains(UUID uuid) {
        if(uuid.equals(couple1) || uuid.equals(couple2)) {
            return true;
        }
        return false;
    }

    public void remove(Player player) {
        couple1 = null;
        couple2 = null;
    }

    public void remove(UUID uuid) {
        couple1 = null;
        couple2 = null;
    }

    public Player getCoupleOfPlayer(Player player) {
        if (couple1.equals(player.getUniqueId())) {
            return Bukkit.getPlayer(couple2);
        }
        if (couple2.equals(player.getUniqueId())) {
            return Bukkit.getPlayer(couple1);
        }
        return null;
    }

    public UUID getCoupleOfUUID(UUID uuid) {
        if (couple1.equals(uuid)) {
            return couple2;
        }
        if (couple2.equals(uuid)) {
            return couple1;
        }
        return null;
    }
    public boolean isUserOnline(UUID uuid) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Player getPlayer(UUID uuid) {
        if(Bukkit.getPlayer(uuid) != null) {
            return Bukkit.getPlayer(uuid);
        }
        return null;
    }
    public String getName(UUID uuid) {
        if(isUserOnline(uuid)) {
            return Bukkit.getPlayer(uuid).getName();
        }
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
    public UUID getPlayer1() {
        return couple1;
    }
    public UUID getPlayer2() {
        return couple2;
    }
}
