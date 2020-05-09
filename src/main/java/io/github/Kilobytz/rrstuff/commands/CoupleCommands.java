package io.github.Kilobytz.rrstuff.commands;

import io.github.Kilobytz.rrstuff.couple.PairConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.*;

public class CoupleCommands implements TabExecutor {

    PairConstructor pC;

    public void setCouples(PairConstructor pC) {this.pC = pC;}

    Map<String, String> coupleCommands = new LinkedHashMap<>();
    List<String> booleanTypes = new ArrayList<>();


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
                    player1Add.sendMessage("You have been coupled to " + Bukkit.getPlayer(p1UUID).getName());
                    player2Add.sendMessage("You have been coupled to " + Bukkit.getPlayer(p2UUID).getName());

                    if(pC.getAddSync()) {
                        pC.syncStats(player2Add);
                    }
                    else {
                        double maxHealth = pC.getMaxHP();
                        int maxHunger = 20;
                        float maxSaturation = 20;
                        player1Add.setSaturation(maxSaturation);
                        player1Add.setHealth(maxHealth);
                        player1Add.setFoodLevel(maxHunger);
                        player2Add.setSaturation(maxSaturation);
                        player2Add.setHealth(maxHealth);
                        player2Add.setFoodLevel(maxHunger);
                    }
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
                        int coupleNumbers = pC.getLengthOfArray();
                        for (int i = 0; i < coupleNumbers; i++) {
                            String coupleStatement = pC.getCoupleStatement(i);
                            sender.sendMessage(coupleStatement);
                        }
                        return true;
                    }catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(String.format("%sError. There are currently no couples.", ChatColor.RED));
                        return true;
                    }
                case "remove" :
                    if (args.length != 2) {
                        sender.sendMessage(String.format("%sInvalid usage. /couple remove (name/all)", ChatColor.RED));
                        return true;
                    }
                    if(pC.areCouplesEmpty()) {
                        sender.sendMessage("Error. No couples currently added.");
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("all")) {
                        int pairNumbers = pC.getLengthOfArray();
                        for (int i = 0; i < pairNumbers; i++) {
                            UUID player1UUID = pC.getCouple1FromNum(i);
                            UUID player2UUID = pC.getCoupleOppositeUUID(player1UUID);
                            String player1Name = getServer().getOfflinePlayer(player1UUID).getName();
                            String player2Name = getServer().getOfflinePlayer(player2UUID).getName();

                            if (pC.isUserOnline(player1UUID)) {
                                Player player1Obj = pC.getPlayerObject(player1UUID);
                                player1Obj.sendMessage("You have been uncoupled from " + player2Name);
                                if (pC.isUserOnline(player2UUID)) {
                                    Player player2Obj = pC.getPlayerObject(player2UUID);
                                    player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                    sender.sendMessage("Player "+ player1Name + " and " + player2Name + " couple has been dissolved.");
                                    pC.removeCouple(player1UUID);
                                    return true;
                                }
                                pC.removeCouple(player1UUID);
                                sender.sendMessage("Player "+ player1Name + "'s couple has been dissolved.");
                                return true;
                            }
                            if (pC.isUserOnline(player2UUID)) {
                                Player player2Obj = pC.getPlayerObject(player2UUID);
                                player2Obj.sendMessage("You have been uncoupled from " + player1Name);
                                sender.sendMessage("Player " + player2Name + "'s couple has been dissolved.");
                                pC.removeCouple(player1UUID);
                                return true;
                            }
                        }
                    }
                    String player1RemoveRaw = args[1];
                    Player player1Remove = getPlayerExact(player1RemoveRaw);
                    if(player1Remove == null) {
                        sender.sendMessage(String.format("%sError. Invalid player.", ChatColor.RED));
                        return true;
                    }
                    if(!pC.isPlayerCoupled(player1Remove)) {
                        sender.sendMessage(String.format("%sError. Player is not coupled.", ChatColor.RED));
                        return true;
                    }
                    UUID player1UUID = pC.getCouple1FromPlayer(player1Remove);
                    UUID player2UUID = pC.getCoupleOppositeUUID(player1UUID);
                    String player1Name = getServer().getOfflinePlayer(player1UUID).getName();
                    String player2Name = getServer().getOfflinePlayer(player2UUID).getName();
                    player1Remove.sendMessage("You have been uncoupled from " + player2Name);
                    if (pC.isUserOnline(player2UUID)) {
                            Player player2Remove = pC.getCoupleOpposite(player1Remove);
                            player2Remove.sendMessage("You have been uncoupled from " + player1Name);
                            sender.sendMessage("Player "+ player1Name + " and " + player2Name + " couple has been dissolved.");
                            pC.removeCouple(player1UUID);
                            return true;
                    }
                    sender.sendMessage("Player "+ player1Name + "'s couple has been dissolved.");
                    pC.removeCouple(player1UUID);
                    return true;

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

                case "addsync" :
                    if(args.length != 2) {
                        boolean syncAddGet = pC.getAddSync();
                        sender.sendMessage("Syncing on Add: " + syncAddGet);
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("true")) {
                        pC.setAddSync(true);
                        sender.sendMessage("Syncing on add is enabled");
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("false")) {
                        pC.setAddSync(false);
                        sender.sendMessage("Syncing on add is disabled");
                        return true;
                    }

                case "about" :
                    sender.sendMessage("This plugin was created by Kilobytes, requested and theorized by Nambo.");
                    sender.sendMessage("Big thanks to yhousegaming, Shadow, Null and Fiters for bug and beta testing.");
                    sender.sendMessage("If you discover any bugs or have any feature request, contact me at Kilobytes#8095 on Discord, or at:");
                    sender.sendMessage("https://github.com/Kilobytz/RR-Plugin");
                    return true;

                default:
                    sender.sendMessage(String.format("%sInvalid usage. Do /couple to list all Couple commands.", ChatColor.RED));
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
            if (args.length == 2) {
                ArrayList<String> coupleSubCom = new ArrayList<>();
                switch (args[0]) {
                    case "addsync" :
                        if (!args[1].equals("")) {
                            for (String entry : booleanTypes) {
                                if (entry.toLowerCase().startsWith(args[1].toLowerCase())) {
                                    coupleSubCom.add(entry);
                                }
                            }
                        } else {
                            for (String entry : booleanTypes) {
                                coupleSubCom.add(entry);
                            }
                        }
                        return coupleSubCom;
                    case "list" :
                        if(coupleSubCom.size() == 0) {
                            return coupleSubCom;
                    }
                    case "health" :
                        if(coupleSubCom.size() == 0) {
                            return coupleSubCom;
                        }
                    default :
                        return null;
                }

            }


        }
        return null;
    }


    public void populateCommandHash() {
        coupleCommands.put("add", "/couple add : Adds two players to a couple.");
        coupleCommands.put("list","/couple list : Lists all current couples.");
        coupleCommands.put("remove","/couple remove : Dissolves the couple for the specified player, or all.");
        coupleCommands.put("health","/couple health : Sets the maximum health for couples.");
        coupleCommands.put("sync","/couple sync : Syncs up the specified player to their couple's HP and Hunger.");

        booleanTypes.add("true");
        booleanTypes.add("false");
    }
    public void printCoupleCommands(CommandSender sender) {
        for (Map.Entry<String, String> entry : coupleCommands.entrySet()) {
            sender.sendMessage(entry.getValue());
        }
    }
}


