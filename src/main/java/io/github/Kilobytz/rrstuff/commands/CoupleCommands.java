package io.github.Kilobytz.rrstuff.commands;

import io.github.Kilobytz.rrstuff.PairConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.getPlayerExact;
import static org.bukkit.Bukkit.getServer;

public class CoupleCommands implements TabExecutor {

    PairConstructor pC;

    public void setCouples(PairConstructor pC) {this.pC = pC;}

    Map<String, String> coupleCommands = new LinkedHashMap<>();



    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {


        if (command.getName().equalsIgnoreCase("couple")) {

            if(!sender.isOp()) {
                sender.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command. Please contact the server administrator if you believe that this is in error.", ChatColor.RED));
                return true;
            }

            if(args.length == 0) {
                printCoupleCommands(sender);
                return true;
            }

            switch (args[0]) {
                case "add":
                    if (args.length != 3) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple add (name) (name)", ChatColor.RED));
                        return true;
                    }
                    String player1AddRaw = args[1];
                    String player2AddRaw = args[2];
                    Player player1Add = Bukkit.getPlayerExact(player1AddRaw);
                    Player player2Add = Bukkit.getPlayerExact(player2AddRaw);
                    if (player1Add == null || player2Add == null) {
                        sender.sendMessage(String.format("%sError. Invalid player.", ChatColor.RED));
                        return true;
                    }
                    if (player1AddRaw.equalsIgnoreCase(player2AddRaw)) {
                        sender.sendMessage(String.format("%sError. You cannot couple a player to themself.", ChatColor.RED));
                        return true;
                    }
                    if (pC.isPlayerCoupled(player1Add)|| pC.isPlayerCoupled(player2Add)) {
                        sender.sendMessage(String.format("%sError. A player is already in a couple.", ChatColor.RED));
                        return true;
                    }

                    UUID p1UUID = player1Add.getUniqueId();
                    UUID p2UUID = player2Add.getUniqueId();
                    pC.setCouple(p1UUID, p2UUID);
                    sender.sendMessage(player1AddRaw + " and " + player2AddRaw + " have been coupled!");
                    player1Add.sendMessage("You have been coupled to " + player2AddRaw);
                    player2Add.sendMessage("You have been coupled to " + player1AddRaw);
                    double maxHealth = pC.getMaxHP();
                    int maxHunger = 20;
                    float maxSaturation = 20;
                    player1Add.setSaturation(maxSaturation);
                    player1Add.setHealth(maxHealth);
                    player1Add.setFoodLevel(maxHunger);
                    player2Add.setSaturation(maxSaturation);
                    player2Add.setHealth(maxHealth);
                    player2Add.setFoodLevel(maxHunger);
                    return true;
                case "list" :
                    if(args.length != 1) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple list", ChatColor.RED));
                        return true;

                    }
                    if(pC.areCouplesEmpty()) {
                        sender.sendMessage("Error. No couples currently added.");
                        return true;
                    }
                    try {
                        int coupleNumbers = pC.getPairNumbers();
                        for (int i = 0; i < coupleNumbers; i++) {
                            String coupleStatement = pC.getCoupleStatement(i);
                            sender.sendMessage(coupleStatement);
                            sender.sendMessage("e");
                        }
                        return true;
                    }catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(String.format("%sError. There are currently no couples.", ChatColor.RED));
                        return true;
                    }
                case "remove" :
                    if (args.length != 2) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple remove (couple-id)", ChatColor.RED));
                        return true;
                    }
                    if(pC.areCouplesEmpty()) {
                        sender.sendMessage("Error. No couples currently added.");
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("all")) {
                        int pairNumbers = pC.getPairNumbers();
                        for (int i = 0; i < pairNumbers; i++) {
                            UUID player1UUID = pC.getCouple1(0);
                            UUID player2UUID = pC.getCouple2(0);
                            String player1Name = getServer().getOfflinePlayer(player1UUID).getName();
                            String player2Name = getServer().getOfflinePlayer(player2UUID).getName();
                            boolean player1Online = pC.isCoupleOnline(player1UUID);
                            boolean player2Online = pC.isCoupleOnline(player2UUID);
                            if (player1Online) {
                                Player player1Obj = pC.getPlayerObject(player1UUID);
                                player1Obj.sendMessage("You have been uncoupled from " + player2Name);
                                if (player2Online) {
                                    Player player2Obj = pC.getPlayerObject(player2UUID);
                                    player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                    pC.removeCouple(0);
                                    return true;
                                }
                                pC.removeCouple(0);
                                return true;
                            }
                            if (player2Online) {
                                Player player2Obj = pC.getPlayerObject(player2UUID);
                                player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                pC.removeCouple(0);
                                return true;
                            }
                        }
                    }
                    try {
                        int coupleNumber = Integer.parseInt(args[1]);
                        try {
                            UUID player1UUID = pC.getCouple1(coupleNumber);
                            UUID player2UUID = pC.getCouple2(coupleNumber);
                            String player1Name = getServer().getOfflinePlayer(player1UUID).getName();
                            String player2Name = getServer().getOfflinePlayer(player2UUID).getName();
                            Boolean player1Online = pC.isCoupleOnline(player1UUID);
                            Boolean player2Online = pC.isCoupleOnline(player2UUID);
                            if (player1Online) {
                                Player player1Obj = pC.getPlayerObject(player1UUID);
                                player1Obj.sendMessage("You have been uncoupled from " + player2Name);
                                if (player2Online) {
                                    Player player2Obj = pC.getPlayerObject(player2UUID);
                                    player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                    pC.removeCouple(coupleNumber);
                                    return true;
                                }
                                pC.removeCouple(coupleNumber);
                                return true;
                            }
                            if (player2Online) {
                                Player player2Obj = pC.getPlayerObject(player2UUID);
                                player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                pC.removeCouple(coupleNumber);
                                return true;
                            }
                        }catch(IndexOutOfBoundsException e) {
                            sender.sendMessage(String.format("%sError. No couple for that number exists.", ChatColor.RED));
                            return true;
                        }
                    }catch (NumberFormatException e) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple remove (all/number).", ChatColor.RED));
                        return true;
                    }

                case "health" :

                    if (args.length == 1) {
                        double healthMax = pC.getMaxHP();
                        sender.sendMessage("Couple max Health: " + healthMax);
                        return true;
                    }
                    if(args.length > 2) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple health (number).", ChatColor.RED));
                        return true;
                    }
                    try{
                        double healthSet = Double.parseDouble(args[1]);
                        if(healthSet>1024) {
                            healthSet = 1024;
                        }
                        pC.setMaxHP(healthSet);
                        sender.sendMessage("Couple max Health has been set to: " + healthSet);
                        return true;
                    }catch(NumberFormatException | IndexOutOfBoundsException e) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple health (number).", ChatColor.RED));
                        return true;
                    }
                case "sync" :
                    if(args.length != 2) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple sync (couple-name)", ChatColor.RED));
                        return true;
                    }
                    String player1Raw = args[1];
                    Player playerSync1 = getPlayerExact(player1Raw);
                    if(playerSync1 == null) {
                        sender.sendMessage(String.format("%sError. Invalid player.", ChatColor.RED));
                        return true;
                    }
                    if(!pC.isPlayerCoupled(playerSync1)) {
                        sender.sendMessage(String.format("%sError. Specified player is not coupled.", ChatColor.RED));
                        return true;
                    }
                    Player playerSync2 = pC.getCoupleOpposite(playerSync1);
                    pC.syncStats(playerSync2);
                    sender.sendMessage(player1Raw + " has been re-synced");
                    return true;

                default:
                    sender.sendMessage(String.format("%sInvalid usage. Do /couple to list all available commands.", ChatColor.RED));
                    return true;

            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("couple")) {
            if (args.length == 1) {
                ArrayList<String> coupleCom = new ArrayList<>();
                if (!args[0].equals("")) {
                    for (Map.Entry<String, String> entry : coupleCommands.entrySet()) {
                        String tempEntry = entry.getKey();
                        if (tempEntry.toLowerCase().startsWith(args[0].toLowerCase())) {
                            coupleCom.add(tempEntry);
                        }
                    }
                } else {
                    for (Map.Entry<String, String> entry : coupleCommands.entrySet()) {
                        coupleCom.add(entry.getKey());
                    }
                }
                return coupleCom;
            }


        }
        return null;
    }

    public void populateCommandHash() {
        coupleCommands.put("add","/couple add : Adds two players to a couple.");
        coupleCommands.put("list","/couple list : Lists all current couples.");
        coupleCommands.put("remove","/couple remove : Dissolves the specified couple number.");
        coupleCommands.put("health","/couple health : Sets the maximum health for couples.");
        coupleCommands.put("sync","/couple sync : Syncs up the specified player to their couple's HP and Hunger.");
    }
    public void printCoupleCommands(CommandSender sender) {
        for (Map.Entry<String, String> entry : coupleCommands.entrySet()) {
            sender.sendMessage(entry.getValue());
        }
    }
}


