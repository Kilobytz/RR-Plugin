package io.github.Kilobytz.rrstuff.couple;

import io.github.Kilobytz.rrstuff.Main;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PairConstructor {

    private final Main main;


    List<Couple> coupleList = new ArrayList<>();
    private boolean coupleHunger;
    private double maxHP;
    private boolean addSync;

    public PairConstructor(Main main) {
        this.main = main;
        this.maxHP = main.getConfig().getDouble("CoupleMaxHP = ");
        if(maxHP == 0.0) {
            maxHP = 20;
        }


        try{
            this.addSync = main.getConfig().getBoolean("AddHPOnSync = ");
        }catch(NullPointerException e) {
            addSync = false;
        }
    }



    public void setCouple(UUID player1UUID, UUID player2UUID) {
        Couple coupled = new Couple();
        coupled.setCouple(player1UUID,player2UUID);
        coupleList.add(coupled);
        Player player1 = getPlayerObject(player1UUID);
        Player player2 = getPlayerObject(player2UUID);
        player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
    }

    public Couple getCoupleInstance(Player player) {
        for(Couple coupleInstance : coupleList) {
            if(coupleInstance.contains(player)) {
                return coupleInstance;
            }
        }
        return null;
    }

    public Couple getCoupleInstance(UUID uuid) {
        for(Couple coupleInstance : coupleList) {
            if(coupleInstance.contains(uuid)) {
                return coupleInstance;
            }
        }
        return null;
    }

    public Player getPlayerObject(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    public String getDisplayNameFromUUID(UUID uuid) {
        if(isUserOnline(uuid)) {
            return Bukkit.getPlayer(uuid).getName();
        }
        return getServer().getOfflinePlayer(uuid).getName();
    }
    public Player getCoupleOpposite(Player player1) {
        Couple coupleInst = getCoupleInstance(player1);
        return coupleInst.getCoupleOfPlayer(player1);
    }
    public UUID getCoupleOppositeUUID(UUID player1UUID) {
        Couple coupleInst = getCoupleInstance(player1UUID);
        return coupleInst.getCoupleOfUUID(player1UUID);
    }
    public void removeCouple(UUID player1UUID) {
        Couple coupleRemove = getCoupleInstance(player1UUID);
        UUID player2UUID = coupleRemove.getCoupleOfUUID(player1UUID);
        if(coupleRemove.isUserOnline(player1UUID)) {
            overMax(Bukkit.getPlayer(player1UUID));
            if(coupleRemove.isUserOnline(player2UUID)) {
                overMax(Bukkit.getPlayer(player2UUID));
                coupleList.remove(coupleRemove);
                return;
            }
            coupleList.remove(coupleRemove);
            return;
        }
        if(coupleRemove.isUserOnline(player2UUID)) {
            overMax(Bukkit.getPlayer(player2UUID));
            coupleList.remove(coupleRemove);
        }
    }

    public int getLengthOfArray() {
        return coupleList.size();
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

        Couple newInst = getCoupleInstance(player);
        if(newInst == null) {
            return false;
        }
        if(newInst.getCoupleOfPlayer(player) == null) {
            return false;
        }
        return true;
    }
    public String getCoupleStatement(int coupleNumber) {
        Couple coupleSet = coupleList.get(coupleNumber);
        UUID p1 = coupleSet.getPlayer1();
        UUID p2 = coupleSet.getCoupleOfUUID(p1);
        String player1Name = getDisplayNameFromUUID(p1);
        String player2Name = getDisplayNameFromUUID(p2);
        return "Players: " + player1Name + " and " + player2Name + ".";
    }

    public UUID getCouple1FromNum(int coupleNum) {
        Couple coupleSet = coupleList.get(coupleNum);
        return coupleSet.getPlayer1();

    }

    public UUID getCouple1FromPlayer(Player player) {
        return player.getUniqueId();
    }

    public boolean isUserOnline(UUID uuid) {
        Couple coupleInt = getCoupleInstance(uuid);
        return coupleInt.isUserOnline(uuid);
    }

    public boolean areCouplesEmpty() {
        try {
            if (coupleList.get(0) == null) {
                return true;
            }
            return false;
        }catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    public void setMaxHP(double num) {
        this.maxHP = num;
        main.getConfig().set("CoupleMaxHP = " , num);
        main.saveConfig();
        for(Couple couple : coupleList) {

            UUID player1UUID = couple.getPlayer1();
            UUID player2UUID = couple.getCoupleOfUUID(player1UUID);
            Player player1 = getPlayerObject(player1UUID);
            Player player2 = getPlayerObject(player2UUID);
            if (isUserOnline(player1UUID)) {
                double p1HP = player1.getHealth();
                player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                if(p1HP > num) {
                    player1.setHealth(num);
                }
                if (isUserOnline(player2UUID)) {
                    double p2HP = player2.getHealth();
                    player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                    if(p2HP > num) {
                        player2.setHealth(num);
                        return;
                    }
                }
            }
            if (isUserOnline(player2UUID)) {
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
    public void setAddSync(boolean syncAdd) {
        this.addSync = syncAdd;
        main.getConfig().set("AddHPOnSync = " , addSync);
        main.saveConfig();
    }
    public boolean getAddSync() {
        return this.addSync;
    }
}
