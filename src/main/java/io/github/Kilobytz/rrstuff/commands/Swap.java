package io.github.Kilobytz.rrstuff.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.Kilobytz.rrstuff.Main;
import io.netty.channel.local.LocalAddress;

public class Swap implements CommandExecutor {

    Main main;

    public Swap(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (command.getName().equalsIgnoreCase("swap")) {
            if(args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Error. No players specified.");
                return true;
            }
            if(args.length == 2) {
                Player player1 = Bukkit.getPlayer(args[0]);
                Player player2 = Bukkit.getPlayer(args[1]);
                if(player1 == null || player2 == null) {
                    sender.sendMessage(ChatColor.RED + "Error. A player name was invalid.");
                    return true;
                }
                Location p1L = player1.getLocation();
                Location p2L = player2.getLocation();
                player1.teleport(p1L);
                player2.teleport(p2L);
                sender.sendMessage("Players were successfully swapped");
                return true;
            }
            else{
                sender.sendMessage(ChatColor.RED + "Error. Must specify two players.");
                return true;
            }
        }
        return false;
    }
}
