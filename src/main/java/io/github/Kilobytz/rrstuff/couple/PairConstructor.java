package io.github.Kilobytz.rrstuff.couple;

import io.github.Kilobytz.rrstuff.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PairConstructor {

    private final Main main;


    final List<Couple> coupleList = new ArrayList<>();
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
        getPlayerObject(player1UUID).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        getPlayerObject(player2UUID).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
    }

    public Couple getCoupleInstance(Player player) {
        player.sendMessage("breakpoint instance check");
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
        return (getCoupleInstance(player1)).getCoupleOfPlayer(player1);
    }
    public UUID getCoupleOppositeUUID(UUID player1UUID) {
        return (getCoupleInstance(player1UUID)).getCoupleOfUUID(player1UUID);
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
            player.sendMessage("inst is null");
            return false;
        }
        if(newInst.getCoupleOfPlayer(player) == null) {
            player.sendMessage("couple is null");
            return false;
        }
        player.sendMessage("is true");
        return true;
    }
    public String getCoupleStatement(int coupleNumber) {
        Couple coupleSet = coupleList.get(coupleNumber);
        return "Players: " + getDisplayNameFromUUID(coupleSet.getPlayer1()) + " and " + getDisplayNameFromUUID(coupleSet.getCoupleOfUUID(coupleSet.getPlayer1())) + ".";
    }

    public UUID getCouple1FromNum(int coupleNum) {
        return (coupleList.get(coupleNum)).getPlayer1();

    }

    public UUID getCouple1FromPlayer(Player player) {
        return player.getUniqueId();
    }

    public boolean isUserOnline(UUID uuid) {
        return (getCoupleInstance(uuid)).isUserOnline(uuid);
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
                player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                if(player1.getHealth() > num) {
                    player1.setHealth(num);
                }
                if (isUserOnline(player2UUID)) {
                    player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                    if(player2.getHealth() > num) {
                        player2.setHealth(num);
                        return;
                    }
                }
            }
            if (isUserOnline(player2UUID)) {
                player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
                if(player2.getHealth() > num) {
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
            Bukkit.getScheduler().runTaskLater(main, new Runnable() {

                public void run() {
                    player.setHealth(couple.getHealth());
                    if (getHunger()) {
                        player.setFoodLevel(couple.getFoodLevel());
                    }
                }
            }, 1L);

    }
    public boolean checkCoupleOnline(Player couple) {
        try {
            if (!couple.isOnline()) {
                return false;
            }
            if (couple.isDead()) {
                return false;
            }
            if (couple.getGameMode().equals(GameMode.CREATIVE)) {
                return false;
            }
            return !couple.getGameMode().equals(GameMode.SPECTATOR);
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
