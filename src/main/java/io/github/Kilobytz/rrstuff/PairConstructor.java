package io.github.Kilobytz.rrstuff;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PairConstructor {

    private final Main main;

    public PairConstructor(Main main) {
        this.main = main;
    }

    private final ArrayList<UUID> paired1 = new ArrayList<>();
    private final ArrayList<UUID> paired2 = new ArrayList<>();
    private final HashMap<UUID, Integer> hungerLeader = new HashMap<>();
    private boolean coupleHunger;
    private double maxHP;
    private boolean addSync;


    public void setCouple(UUID player1UUID, UUID player2UUID) {
        paired1.add(player1UUID);
        paired2.add(player2UUID);
        Player player1 = getPlayerObject(player1UUID);
        Player player2 = getPlayerObject(player2UUID);
        player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
    }
    public Player getPlayerObject(UUID uuid) {
        return Bukkit.getPlayer(uuid);
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
        return getServer().getOfflinePlayer(uuid).getName();
    }
    public Player getCoupleOpposite(Player player1) {
        UUID player1UUID = player1.getUniqueId();
        if(paired1.contains(player1UUID)) {
            int pairI = paired1.indexOf(player1UUID);
            UUID player2UUID = paired2.get(pairI);
            return getPlayerObject(player2UUID);
        }
        if(paired2.contains(player1UUID)) {
            int pairI = paired2.indexOf(player1UUID);
            UUID player2UUID = paired1.get(pairI);
            return getPlayerObject(player2UUID);
        }
        return null;
    }
    public UUID getCoupleOppositeUUID(UUID player1UUID) {
        if(paired1.contains(player1UUID)) {
            int pairI = paired1.indexOf(player1UUID);
            return paired2.get(pairI);
        }
        if(paired2.contains(player1UUID)) {
            int pairI = paired2.indexOf(player1UUID);
            return paired1.get(pairI);
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
            overMax(player1);
            if(player2Online) {
                overMax(player2);
                return;
            }
            return;
        }
        if(player2Online) {
            overMax(player2);
        }
    }

    public void overMax(Player player) {
        if(!addSync) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            if (maxHP > 20) {
                player.setHealth(20);
            }
        }
    }
    public boolean isPlayerCoupled(Player player) {
        UUID uuid = player.getUniqueId();
        if(paired1.contains(uuid)) {
            return true;
        }
        return paired2.contains(uuid);
    }
    public String getCoupleStatement(int coupleNumber) {
        UUID player1 = paired1.get(coupleNumber);
        UUID player2 = paired2.get(coupleNumber);
        String player1Name = getDisplayNameFromUUID(player1);
        String player2Name = getDisplayNameFromUUID(player2);
        return "Couple Number: " + coupleNumber + ". Players: " + player1Name + " and " + player2Name + ".";
    }
    public UUID getCouple1FromNum(int coupleNum) {
        return paired1.get(coupleNum);
    }

    public UUID getCouple2FromNum(int coupleNum) {
        return paired2.get(coupleNum);
    }

    public UUID getCouple1FromPlayer(Player player) {
        return player.getUniqueId();
    }

    public UUID getCouple2FromPlayer(Player player) {
        return player.getUniqueId();
    }

    public int getPairNumbers() {
        return paired1.size();
    }
    public boolean areCouplesEmpty() {
        try {
            return paired1.get(0) == null;

        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    public void setMaxHP(double num) {
        int pairSize = paired1.size();
        this.maxHP = num;
        for(int i = 0; i < pairSize; i++) {
            UUID player1UUID = paired1.get(i);
            UUID player2UUID = paired2.get(i);
            Player player1 = getPlayerObject(player1UUID);
            Player player2 = getPlayerObject(player2UUID);
            boolean player1Online = isCoupleOnline(player1UUID);
            boolean player2Online = isCoupleOnline(player2UUID);
            if (player1Online) {
                double p1HP = player1.getHealth();
                player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                if(p1HP > num) {
                    player1.setHealth(num);
                }
                if (player2Online) {
                    double p2HP = player2.getHealth();
                    player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                    if(p2HP > num) {
                        player2.setHealth(num);
                        return;
                    }
                }
            }
            if (player2Online) {
                double p2HP = player2.getHealth();
                player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                if(p2HP > num) {
                    player2.setHealth(num);
                }
            }
        }

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
                    boolean hunger = getHunger();
                    if (hunger) {
                        int hungerToSet = couple.getFoodLevel();
                        player.setFoodLevel(hungerToSet);
                    }
                }
            }, 1L);

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
            return !playerGamemode.equals(spectator);
        }catch(NullPointerException e) {
            return false;
        }
    }
    public Boolean isCoupleOnline(UUID uuid) {
        Player player = getPlayerObject(uuid);
        return player != null;
    }
    public void setAddSync(boolean syncAdd) {
        this.addSync = syncAdd;
    }
    public boolean getAddSync() {
        return this.addSync;
    }
}
