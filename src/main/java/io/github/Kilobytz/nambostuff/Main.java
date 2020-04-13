package io.github.Kilobytz.nambostuff;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

public class Main  extends JavaPlugin {

    PairConstructor pC = new PairConstructor(this);
    PairEffects pE = new PairEffects();

    @Override
    public void onEnable() {
        registerListeners();
        pE.setPairs(pC);
        pC.setMaxHP(10);
        pC.setHunger(true);

    }

    @Override
    public void onDisable() {

    }
    public void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this.pE, this);
    }


    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        Player playerSent = (Player) sender;

        UUID playerID = playerSent.getUniqueId();


        if (command.getName().equalsIgnoreCase("couple")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length != 2) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /couple (player) (player)", ChatColor.RED));
                    return true;
                }
                String potentialPlayer1 = args[0];
                String potentialPlayer2 = args[1];
                if ((Bukkit.getPlayerExact(potentialPlayer1) == null)) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /couple (player) (player)", ChatColor.RED));
                    return true;
                }
                if ((Bukkit.getPlayerExact(potentialPlayer2) == null)) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /couple (player) (player)", ChatColor.RED));
                    return true;
                }
                Player player1 = Bukkit.getServer().getPlayer(potentialPlayer1);
                Player player2 = Bukkit.getServer().getPlayer(potentialPlayer2);

                boolean player1PairStatus = pC.isPlayerCoupled(player1);
                boolean player2PairStatus = pC.isPlayerCoupled(player2);

                if (player1PairStatus) {
                    if(player2PairStatus) {
                        playerSent.sendMessage(String.format("%1$sError. %2$s and %3$s is already coupled.", ChatColor.RED, potentialPlayer1, potentialPlayer2));
                        return true;
                    }
                    playerSent.sendMessage(String.format("%1$sError. %2$s is already coupled.", ChatColor.RED, potentialPlayer1));
                    return true;
                }
                if(player2PairStatus) {
                    playerSent.sendMessage(String.format("%1$sError. %2$s is already coupled.", ChatColor.RED, potentialPlayer2));
                    return true;
                }

                if(player1 == player2) {
                    playerSent.sendMessage(String.format("%sError. You cannot couple a player to themself", ChatColor.RED));
                    return true;
                }
                UUID player1UUID = player1.getUniqueId();
                UUID player2UUID = player2.getUniqueId();
                pC.setCouple(player1UUID, player2UUID);
                playerSent.sendMessage(potentialPlayer1 + " and " + potentialPlayer2 + " have been coupled!");
                player1.sendMessage("You have been coupled to " + potentialPlayer2);
                player2.sendMessage("You have been coupled to " + potentialPlayer1);
                return true;
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("listcouple")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length != 1) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /listpair (pair number)", ChatColor.RED));
                    return true;
                }
                String coupleLetter = args[0];

                if(coupleLetter == null) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /listpair (pair number)", ChatColor.RED));
                    return true;
                }
                try{
                    if(coupleLetter.matches("^[0-9]*$")) {
                    int coupleNumber = Integer.parseInt(coupleLetter);
                        String coupleStatement = pC.getCoupleStatement(coupleNumber);
                        playerSent.sendMessage(coupleStatement);
                        return true;
                        }
                    }catch (IndexOutOfBoundsException e) {
                    playerSent.sendMessage(String.format("%sError. No couple for that number exists.", ChatColor.RED));
                    return true;
                }
                playerSent.sendMessage(String.format("%sInvalid format. Must be a number", ChatColor.RED));
                return true;

            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("listallcouples")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length != 0) {
                    playerSent.sendMessage(String.format("%sError.", ChatColor.RED));
                    return true;
                }
                try {
                    boolean pairNullCheck = pC.arePairsNull();
                    if (pairNullCheck) {
                        playerSent.sendMessage(String.format("%sError.", ChatColor.RED));
                        return true;
                    }
                }catch (IndexOutOfBoundsException e) {
                    playerSent.sendMessage(String.format("%sError. No couples added", ChatColor.RED));
                    return true;
                }
                int pairNumbers = pC.getPairNumbers();
                for (int i = 0; i < pairNumbers; i++) {
                    String coupleStatement = pC.getCoupleStatement(i);
                    playerSent.sendMessage(coupleStatement);
                }
                return true;
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("uncoupleall")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length != 0) {
                    playerSent.sendMessage(String.format("%sError.", ChatColor.RED));
                    return true;
                }

                try {
                    boolean pairNullCheck = pC.arePairsNull();
                    if (pairNullCheck) {
                        playerSent.sendMessage(String.format("%sError.", ChatColor.RED));
                        return true;
                    }
                }catch (IndexOutOfBoundsException e) {
                    playerSent.sendMessage(String.format("%sError. No couples added", ChatColor.RED));
                    return true;
                }

                int pairNumbers = pC.getPairNumbers();
                for(int i = 0; i < pairNumbers; i++) {
                    UUID player1 = pC.getCouple1(0);
                    UUID player2 = pC.getCouple2(0);
                    String player1Name = getServer().getOfflinePlayer(player1).getName();
                    String player2Name = getServer().getOfflinePlayer(player2).getName();
                    Boolean player1Online = pC.isCoupleOnline(player1);
                    Boolean player2Online = pC.isCoupleOnline(player2);
                    if(player1Online) {
                        Player player1Obj = pC.getPlayerObject(player1);
                        player1Obj.sendMessage("You have been uncoupled from " + player2Name);
                        if(player2Online) {
                            Player player2Obj = pC.getPlayerObject(player2);
                            player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                            pC.removeCouple(0);
                            return true;
                        }
                        pC.removeCouple(0);
                        return true;
                    }
                    if(player2Online) {
                        Player player2Obj = pC.getPlayerObject(player2);
                        player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                        pC.removeCouple(0);
                        return true;
                    }
                }
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("uncouple")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length != 1) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /uncouple (pair number)", ChatColor.RED));
                    return true;
                }
                String coupleLetter = args[0];

                if(coupleLetter == null) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /uncouple (pair number)", ChatColor.RED));
                    return true;
                }
                try{
                    if(coupleLetter.matches("^[0-9]*$")) {
                        int coupleNumber = Integer.parseInt(coupleLetter);
                        UUID player1 = pC.getCouple1(coupleNumber);
                        UUID player2 = pC.getCouple2(coupleNumber);
                        String player1Name = getServer().getOfflinePlayer(player1).getName();
                        String player2Name = getServer().getOfflinePlayer(player2).getName();
                        Boolean player1Online = pC.isCoupleOnline(player1);
                        Boolean player2Online = pC.isCoupleOnline(player2);
                        if(player1Online) {
                            Player player1Obj = pC.getPlayerObject(player1);
                            player1Obj.sendMessage("You have been uncoupled from " + player2Name);
                            if(player2Online) {
                                Player player2Obj = pC.getPlayerObject(player2);
                                player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                playerSent.sendMessage("Couple number " + coupleNumber + " of " + player1Name + " and " + player2Name + " has ended!");
                                pC.removeCouple(coupleNumber);
                                return true;
                            }
                            playerSent.sendMessage("Couple number " + coupleNumber + " of " + player1Name + " and " + player2Name + " has ended!");
                            pC.removeCouple(coupleNumber);
                            return true;
                        }
                        if(player2Online) {
                            Player player2Obj = pC.getPlayerObject(player2);
                            player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                            playerSent.sendMessage("Couple number " + coupleNumber + " of " + player1Name + " and " + player2Name + " has ended!");
                            pC.removeCouple(coupleNumber);
                            return true;
                        }
                    }
                }catch (IndexOutOfBoundsException e) {
                    playerSent.sendMessage(String.format("%sError. No couple for that number exists.", ChatColor.RED));
                    return true;
                }
                playerSent.sendMessage(String.format("%sInvalid format. Must be a number", ChatColor.RED));
                return true;
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("couplehp")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length > 1) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /couplehp", ChatColor.RED));
                    return true;
                }

                if(args[0] == null) {
                    Double hpCheck = pC.getMaxHP()/2;
                    playerSent.sendMessage("Current couple HP is " + pC.getMaxHP() + " hearts. Use /couplehp (num) to set max Hearts.");
                    return true;
                }
                String hpNumRaw = args[0];
                if(hpNumRaw.matches("^[0-9]*$")) {
                    double hpNum = Double.parseDouble(hpNumRaw);
                    pC.setMaxHP(hpNum);
                    playerSent.sendMessage("Max HP for couples has been set to " + hpNum + " hearts.");
                    return true;
                }
                playerSent.sendMessage(String.format("%sInvalid format. Usage: /couplehp", ChatColor.RED));
                return true;
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("couplehunger")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length > 1) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /couplehunger (true,false)", ChatColor.RED));
                    return true;
                }
                if (args[0] == null) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /couplehunger (true,false)", ChatColor.RED));
                    return true;
                }
                String hungerRaw = args[0].toLowerCase();
                if(hungerRaw.equals("true")) {
                    boolean hunger = Boolean.parseBoolean(hungerRaw);
                    pC.setHunger(hunger);
                    playerSent.sendMessage("Shared hunger has been enabled for couples.");
                    return true;
                }
                if(hungerRaw.equals("false")) {
                    boolean hunger = Boolean.parseBoolean(hungerRaw);
                    pC.setHunger(hunger);
                    playerSent.sendMessage("Shared hunger has been disabled for couples.");
                    return true;
                }
                playerSent.sendMessage(String.format("%sInvalid format. Usage: /couplehunger (true,false)", ChatColor.RED));
                return true;
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        if (command.getName().equalsIgnoreCase("sync")) {
            int length = args.length;
            if (playerSent.isOp()) {
                if (length != 1) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /sync (player to sync couple to)", ChatColor.RED));
                    return true;
                }
                if (args[0] == null) {
                    playerSent.sendMessage(String.format("%sInvalid format. Usage: /sync (player to sync couple to)", ChatColor.RED));
                    return true;
                }
                String potentialPlayer = args[0];
                Player player1 = Bukkit.getServer().getPlayer(potentialPlayer);
                boolean coupleCheck = pC.isPlayerCoupled(player1);
                if(coupleCheck) {
                    Player player2 = pC.getCoupleOpposite(player1);
                    pC.syncStats(player2);
                    return true;
                }
                playerSent.sendMessage(String.format("%sError. This player is not coupled.", ChatColor.RED));
                return true;
            }
            playerSent.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command", ChatColor.RED));
            return true;
        }
        return false;
    }
}
