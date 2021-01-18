package io.github.Kilobytz.rrstuff.couple;

import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

public class PairEffects implements Listener {

    PairConstructor pC;

    public void setPairs(PairConstructor pC) {
        this.pC = pC;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent dmgEvent) {
        if (dmgEvent.getEntity() instanceof Player) {
            Player dmgedPlayer = (Player) dmgEvent.getEntity();
            double dmg = dmgEvent.getFinalDamage();
            if(pC.isPlayerCoupled(dmgedPlayer)) {
                Player couple = pC.getCoupleOpposite(dmgedPlayer);
                if (pC.checkCoupleOnline(couple)) {
                    double hpToSet = dmgedPlayer.getHealth() - dmg;
                    if (hpToSet < 0) {
                        couple.setHealth(0);
                        return;
                    }
                    couple.setHealth(hpToSet);
                }
            }
        }

    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player playerJoining = event.getPlayer();
        if(pC.isPlayerCoupled(playerJoining)) {
            playerJoining.sendMessage("first check true");
            if (pC.checkCoupleOnline(pC.getCoupleOpposite(playerJoining))) {
                playerJoining.sendMessage("second check true");
                playerJoining.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(pC.getMaxHP());
                pC.syncStats(playerJoining);
                return;
            }
        }
        else{
            double hp = playerJoining.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if(hp > 20) {
                playerJoining.setFoodLevel(20);
                playerJoining.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                playerJoining.setHealth(20);
            }
        }
    }
    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        Player playerRespawning = event.getPlayer();
        boolean coupleCheck = pC.isPlayerCoupled(playerRespawning);
        if(coupleCheck) {
            Player couple = pC.getCoupleOpposite(playerRespawning);
            if (pC.checkCoupleOnline(couple)) {
                pC.syncStats(playerRespawning);
            }
        }
    }
    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player playerChangingGamemode = event.getPlayer();
        boolean coupleCheck = pC.isPlayerCoupled(playerChangingGamemode);
        if(coupleCheck) {
            Player couple = pC.getCoupleOpposite(playerChangingGamemode);
            if(pC.checkCoupleOnline(couple)) {
                GameMode newMode = event.getNewGameMode();
                GameMode survival = GameMode.SURVIVAL;
                GameMode adventure = GameMode.ADVENTURE;
                if (newMode == survival || newMode == adventure) {
                    pC.syncStats(playerChangingGamemode);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent healEvent){
        if(healEvent.getEntity() instanceof Player) {
            Player healedPlayer = (Player) healEvent.getEntity();
            double heal = healEvent.getAmount();
            boolean coupleCheck = pC.isPlayerCoupled(healedPlayer);
            if(coupleCheck) {
                Player couple = pC.getCoupleOpposite(healedPlayer);
                if(pC.checkCoupleOnline(couple)){
                    double hpToSet = couple.getHealth() + heal;
                    double maxHP = pC.getMaxHP();
                    if(hpToSet > maxHP){
                        hpToSet = maxHP;
                    }
                    couple.setHealth(hpToSet);
                    
                }
            }

        }


    }
    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent foodLossEvent) {
        Entity rawHunger = foodLossEvent.getEntity();
        if(rawHunger instanceof Player) {
            int foodEventFood = foodLossEvent.getFoodLevel();
            Player hungerPlayer = ((Player) rawHunger).getPlayer();
            boolean coupleCheck = pC.isPlayerCoupled(hungerPlayer);
            if(coupleCheck) {
                Player couple = pC.getCoupleOpposite(hungerPlayer);
                if(pC.checkCoupleOnline(couple)) {
                    int playerFood = hungerPlayer.getFoodLevel();
                    //int coupleFood = couple.getFoodLevel();
                    float saturation = Float.intBitsToFloat(playerFood);
                    couple.setFoodLevel(foodEventFood);
                    hungerPlayer.setFoodLevel(foodEventFood);
                    hungerPlayer.setSaturation(saturation);
                    couple.setSaturation(saturation);
                }
            }
        }
    }

    public void sendDmgEffect(Player player1, Player player2) {
        net.minecraft.server.v1_12_R1.Entity entity = ((CraftPlayer) player1).getHandle();
        PacketPlayOutAnimation dmg = new PacketPlayOutAnimation(entity,1);
        ((CraftPlayer) player2).getHandle().playerConnection.sendPacket(dmg);
        player2.playSound(player1.getLocation(), Sound.ENTITY_PLAYER_HURT,1,1);
    }
}
