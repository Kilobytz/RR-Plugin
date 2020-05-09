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
        if(uuid == couple1 || uuid == couple2) {
            return true;
        }
        return false;
    }
    public boolean contains(Player player) {
        if(player == null) {
            return false;
        }
        if(player.getUniqueId() == couple1 || player.getUniqueId() == couple2) {
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
        if (couple1 == player.getUniqueId()) {
                Player player2 = Bukkit.getPlayer(couple2);
                return player2;
            }
            if (couple2 == player.getUniqueId()) {
                Player player1 = Bukkit.getPlayer(couple1);
                return player1;
            }
        return null;
    }

    public UUID getCoupleOfUUID(UUID uuid) {
        if (couple1 == uuid) {
            return couple2;
        }
        if (couple2 == uuid) {
            return couple1;
        }
        return null;
    }
    public boolean isUserOnline(UUID uuid) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getUniqueId() == uuid) {
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
            String name = Bukkit.getPlayer(uuid).getName();
            return name;
        }
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name;
    }
    public UUID getPlayer1() {
        return couple1;
    }
}
