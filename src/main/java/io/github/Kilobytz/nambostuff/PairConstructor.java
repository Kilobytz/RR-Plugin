package io.github.Kilobytz.nambostuff;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PairConstructor {

    private Main main;

    public PairConstructor(Main main) {
        this.main = main;
    }

    private ArrayList<UUID> paired1 = new ArrayList<UUID>();
    private ArrayList<UUID> paired2 = new ArrayList<UUID>();
    private HashMap<UUID, Integer> hungerLeader = new HashMap<UUID, Integer>();
    private boolean coupleHunger;
    private double maxHP;


    public void setCouple(UUID player1UUID, UUID player2UUID) {
        paired1.add(player1UUID);
        paired2.add(player2UUID);
        Player player1 = getPlayerObject(player1UUID);
        Player player2 = getPlayerObject(player2UUID);
        player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        player1.setHealth(maxHP);
        player2.setHealth(maxHP);
        player1.setFoodLevel(20);
        player2.setFoodLevel(20);
    }
    public Player getPlayerObject(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return player;
    }

    public Integer getCoupleNumFromPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if(paired1.contains(uuid)) {
            return paired1.indexOf(uuid);
        }
        if(paired2.contains(uuid)) {
            return paired2.indexOf(uuid);
        }
        return null;
    }
    public String getDisplayNameFromUUID(UUID uuid) {
        String playerName = getServer().getOfflinePlayer(uuid).getName();
        return playerName;
    }
    public Player getCoupleOpposite(Player player1) {
        UUID player1UUID = player1.getUniqueId();
        if(paired1.contains(player1UUID)) {
            int pairI = paired1.indexOf(player1UUID);
            UUID player2UUID = paired2.get(pairI);
            Player player2 = getPlayerObject(player2UUID);
            return player2;
        }
        if(paired2.contains(player1UUID)) {
            int pairI = paired2.indexOf(player1UUID);
            UUID player2UUID = paired1.get(pairI);
            Player player2 = getPlayerObject(player2UUID);
            return player2;
        }
        return null;
    }
    public UUID getCoupleOppositeUUID(UUID player1UUID) {
        if(paired1.contains(player1UUID)) {
            int pairI = paired1.indexOf(player1UUID);
            UUID player2UUID = paired2.get(pairI);
            return player2UUID;
        }
        if(paired2.contains(player1UUID)) {
            int pairI = paired2.indexOf(player1UUID);
            UUID player2UUID = paired1.get(pairI);
            return player2UUID;
        }
        return null;
    }
    public void removeCouple(int coupleNum) {
        UUID player1UUID = paired1.get(coupleNum);
        UUID player2UUID = paired2.get(coupleNum);
        Player player1 = getPlayerObject(player1UUID);
        Player player2 = getPlayerObject(player2UUID);
        paired1.remove(coupleNum);
        paired2.remove(coupleNum);
        Boolean player1Online = isCoupleOnline(player1UUID);
        Boolean player2Online = isCoupleOnline(player2UUID);
        if(player1Online) {
            player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            overMax(player1);
            if(player2Online) {
                player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                overMax(player2);
                return;
            }
            return;
        }
        if(player2Online) {
            player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            overMax(player2);
            return;
        }
        return;
    }

    public void overMax(Player player) {
        if(maxHP > 20) {
            player.setHealth(20);
        }
    }
    public boolean isPlayerCoupled(Player player) {
        UUID uuid = player.getUniqueId();
        if(paired1.contains(uuid)) {
            return true;
        }
        if(paired2.contains(uuid)) {
            return true;
        }
        return false;
    }
    public String getCoupleStatement(int coupleNumber) {
        UUID player1 = paired1.get(coupleNumber);
        UUID player2 = paired2.get(coupleNumber);
        String player1Name = getDisplayNameFromUUID(player1);
        String player2Name = getDisplayNameFromUUID(player2);
        String fullCoupleStatement = "Couple Number: " + coupleNumber + ". Players: " + player1Name + " and " + player2Name + ".";
        return fullCoupleStatement;
    }
    public UUID getCouple1(int coupleNum) {
        UUID player1 = paired1.get(coupleNum);
        return player1;
    }
    public UUID getCouple2(int coupleNum) {
        UUID player2 = paired2.get(coupleNum);
        return player2;
    }
    public int getPairNumbers() {
        int pairNumbers = paired1.size();
        return pairNumbers;
    }
    public boolean arePairsNull() {
        if(paired1.get(0).equals(null)) {
            return true;
        }
        return false;
    }

    public void setMaxHP(double num) {
        int pairSize = paired1.size();
        num = num*2;
        if(num>1024) {
            this.maxHP = 1024;
            num = 1024;
        }
        else {
            this.maxHP = num;
        }
        for(int i = 0; i < pairSize; i++) {
            UUID player1UUID = paired1.get(i);
            UUID player2UUID = paired2.get(i);
            Player player1 = getPlayerObject(player1UUID);
            Player player2 = getPlayerObject(player2UUID);
            Boolean player1Online = isCoupleOnline(player1UUID);
            Boolean player2Online = isCoupleOnline(player2UUID);
            if (player1Online) {
                player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                double p1HP = player1.getHealth();
                if(p1HP >num) {
                    player1.setHealth(p1HP);
                }
                if (player2Online) {
                    player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                    double p2HP = player2.getHealth();
                    if(p2HP >num) {
                        player2.setHealth(p2HP);
                    }
                    return;
                }
            }
            if (player2Online) {
                player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                double p2HP = player2.getHealth();
                if(p2HP >num) {
                    player2.setHealth(p2HP);
                }
                return;
            }
            return;
        }
        return;

    }

    public Double getMaxHP() {
        return maxHP;
    }
    public void setHunger(boolean hunger) {
        this.coupleHunger = hunger;
    }
    public boolean getHunger() {
        return coupleHunger;
    }
    public void syncStats(final Player player) {
        try {
            final Player couple = getCoupleOpposite(player);
            if (!couple.isOnline()) {
                return;
            }
            if (couple.isDead()) {
                return;
            }
            final double hpToSet = couple.getHealth();
            Bukkit.getScheduler().runTaskLater(main, new Runnable() {

                public void run() {
                    player.setHealth(hpToSet);
                    Boolean hunger = getHunger();
                    if (hunger) {
                        int hungerToSet = couple.getFoodLevel();
                        player.setFoodLevel(hungerToSet);
                        return;
                    }
                    return;
                }
            }, 10L);
        }catch(NullPointerException e) {
            return;
        }
    }
    public boolean checkSoloCouple(Player couple) {
        try {
            if (!couple.isOnline()) {
                return false;
            }
            if (couple.isDead()) {
                return false;
            }
            GameMode creative = GameMode.CREATIVE;
            GameMode spectator = GameMode.SPECTATOR;
            GameMode playerGamemode = couple.getGameMode();
            if (playerGamemode.equals(creative)) {
                return false;
            }
            if (playerGamemode.equals(spectator)) {
                return false;
            }
            return true;
        }catch(NullPointerException e) {
            return false;
        }
    }
    public Boolean isCoupleOnline(UUID uuid) {
        Player player = getPlayerObject(uuid);
        if(player == null) {
            return false;
        }
        return true;
    }

}
