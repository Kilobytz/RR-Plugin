package io.github.Kilobytz.nambostuff;

import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PairEffects implements Listener {

    PairConstructor pC;

    void setPairs(PairConstructor pC) {
        this.pC = pC;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent dmgEvent) {
        if (dmgEvent.getEntity() instanceof Player) {
            Player dmgedPlayer = (Player) dmgEvent.getEntity();
            Double dmg = dmgEvent.getFinalDamage();
            Boolean coupleCheck = pC.isPlayerCoupled(dmgedPlayer);
            if(coupleCheck) {
                Player couple = pC.getCoupleOpposite(dmgedPlayer);
                Boolean isCoupleSolo = pC.checkSoloCouple(couple);
                if (isCoupleSolo) {
                    double hpToSet = couple.getHealth() - dmg;
                    if (hpToSet < 0) {
                        couple.setHealth(0);
                        return;
                    }
                }
            }
        }

    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player playerJoining = event.getPlayer();
        Boolean coupleCheck = pC.isPlayerCoupled(playerJoining);
        if(coupleCheck) {
            double maxHP = pC.getMaxHP();
            playerJoining.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
            pC.syncStats(playerJoining);
            return;
        }
        double hp = playerJoining.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if(hp > 20) {
            playerJoining.setFoodLevel(20);
            playerJoining.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            playerJoining.setHealth(20);
            return;
        }
    }
    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        Player playerRespawning = event.getPlayer();
        Boolean coupleCheck = pC.isPlayerCoupled(playerRespawning);
        if(coupleCheck) {

            Player couple = pC.getCoupleOpposite(playerRespawning);
            pC.syncStats(playerRespawning);
        }
    }
    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player playerChangingGamemode = event.getPlayer();
        Boolean coupleCheck = pC.isPlayerCoupled(playerChangingGamemode);
        if(coupleCheck) {
            GameMode newMode = event.getNewGameMode();
            GameMode survival = GameMode.SURVIVAL;
            GameMode adventure = GameMode.ADVENTURE;
            if(newMode == survival || newMode == adventure) {
                pC.syncStats(playerChangingGamemode);
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
                Boolean isCoupleSolo = pC.checkSoloCouple(couple);
                if(isCoupleSolo){
                    double hpToSet = couple.getHealth() + heal;
                    double maxHP = pC.getMaxHP();
                    if(hpToSet > maxHP){
                        hpToSet = maxHP;
                    }
                    couple.setHealth(hpToSet);
                    return;
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
                Boolean isCoupleSolo = pC.checkSoloCouple(couple);
                if(isCoupleSolo) {
                    hungerPlayer.sendMessage("hunger event");
                    int playerFood = hungerPlayer.getFoodLevel();
                    int coupleFood = couple.getFoodLevel();
                    float playerSaturation = hungerPlayer.getSaturation();
                    float coupleSaturation = couple.getSaturation();
                    couple.setFoodLevel(foodEventFood);
                    hungerPlayer.setFoodLevel(foodEventFood);
                    hungerPlayer.setSaturation(playerSaturation);   //add reduced regen
                    couple.setSaturation(playerSaturation);
                }
            }
        }
    }

}
