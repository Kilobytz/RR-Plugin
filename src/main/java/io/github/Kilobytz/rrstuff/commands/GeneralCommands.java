package io.github.Kilobytz.rrstuff.commands;

import io.github.Kilobytz.rrstuff.Main;
import io.github.Kilobytz.rrstuff.loader.LoaderInit;
import io.github.Kilobytz.rrstuff.misc.SnowballDeath;
import io.github.Kilobytz.rrstuff.mole.MoleHandling;
import io.github.Kilobytz.rrstuff.mole.MoleListener;
import io.github.Kilobytz.rrstuff.packetshit.VanishInit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GeneralCommands implements TabExecutor {

    LoaderInit loaderInit;
    SnowballDeath snowballDeath;
    VanishInit vanishInit;
    MoleListener moleListener;
    Main main;


    public GeneralCommands(Main main) {
        this.main = main;
    }
    public void setMoleListener(MoleListener moleListener) {
        this.moleListener = moleListener;
    }

    public void setLoaderData(LoaderInit loaderInit) {
        this.loaderInit = loaderInit;
    }
    public void setSnowballInfo(SnowballDeath snowballDeath) {
        this.snowballDeath = snowballDeath;
    }
    public void setVanishData(VanishInit vanishInit) {
        this.vanishInit = vanishInit;
    }

    Map<String, String> generalCommands = new LinkedHashMap<>();
    List<String> booleanTypes = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        Player playerSent = (Player) sender;

        if (command.getName().equalsIgnoreCase("rr")) {

            if (!sender.isOp()) {
                sender.sendMessage(String.format("%sI'm sorry, but you do not have permission to perform this command. Please contact the server administrator if you believe that this is in error.", ChatColor.RED));
                return true;
            }

            if (args.length == 0) {
                printCommands(sender);
                return true;
            }

            switch (args[0]) {
                case "loader":
                    if(args.length == 1) {
                        sender.sendMessage("Current load tag is: " + loaderInit.getLoaderTag());
                        return true;
                    }
                    if(args.length > 2) {
                        sender.sendMessage(String.format("%sError. New loader tag must be one word.", ChatColor.RED));
                        return true;
                    }
                    sender.sendMessage("New chunk loader tag has been set to " + args[1]);
                    loaderInit.changeLoaderTag(args[1]);
                    return true;

                case "snowball" :
                    if(args.length == 1) {
                     sender.sendMessage("Snowball kill: " + snowballDeath.getSnowballStatus());
                     return true;
                    }
                    if(args.length > 2) {
                        sender.sendMessage(String.format("%sError. Invalid usage.", ChatColor.RED));
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("true")) {
                        snowballDeath.setSnowballStatus(true);
                        sender.sendMessage("Snowball death has been enabled.");
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("false")) {
                        snowballDeath.setSnowballStatus(false);
                        sender.sendMessage("Snowball death has been disabled.");
                        return true;
                    }
                case "vanish" :
                    if(args.length == 1) {
                        vanishInit.setVanish(playerSent);
                        return true;
                    }
                    sender.sendMessage(String.format("%sError. Invalid usage.", ChatColor.RED));
                    return true;

                case "molestart" :
                    if(!moleListener.isMoleOn()) {
                        sender.sendMessage("Starting molecore.");
                        moleListener.setMole();
                        return true;
                    }
                    sender.sendMessage("A molecore has already been started!");
                    return true;
                case "moleend" :
                    if(moleListener.isMoleOn()) {
                        sender.sendMessage("Ending Molecore.");
                        moleListener.stopMole();
                        return true;
                    }
                    sender.sendMessage("A molecore has not been started!");
                    return true;
                default:
                    sender.sendMessage(String.format("%sInvalid usage. Do /rr to get a list of all RR Commands.", ChatColor.RED));
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("rr")) {
            if (args.length == 1) {
                ArrayList<String> coupleCom = new ArrayList<>();
                if (!args[0].equals("")) {
                    for (Map.Entry<String, String> entry : generalCommands.entrySet()) {
                        String tempEntry = entry.getKey();
                        if (tempEntry.toLowerCase().startsWith(args[0].toLowerCase())) {
                            coupleCom.add(tempEntry);
                        }
                    }
                } else {
                    for (Map.Entry<String, String> entry : generalCommands.entrySet()) {
                        coupleCom.add(entry.getKey());
                    }
                }
                return coupleCom;
            }
            if (args.length == 2) {
                ArrayList<String> coupleSubCom = new ArrayList<>();
                if(args[0].equalsIgnoreCase("loader")) {
                    return coupleSubCom;
                }
            }



        }
        return null;
    }


    public void populateCommandHash() {
        generalCommands.put("loader", "/rr loader : Changes the chunk loader tag.");
        generalCommands.put("snowball", "/rr snowball : Enables and disables snowball instadeath.");

        booleanTypes.add("true");
        booleanTypes.add("false");
    }
    public void printCommands(CommandSender sender) {
        for (Map.Entry<String, String> entry : generalCommands.entrySet()) {
            sender.sendMessage(entry.getValue());
        }
    }
}
