package io.github.Kilobytz.rrstuff;

import io.github.Kilobytz.rrstuff.commands.CoupleCommands;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main  extends JavaPlugin {

    PairConstructor pC = new PairConstructor(this);
    PairEffects pE = new PairEffects();
    CoupleCommands cC = new CoupleCommands();

    @Override
    public void onEnable() {
        registerListeners();
        pE.setPairs(pC);
        pC.setMaxHP(20);
        pC.setHunger(true);
        cC.setCouples(pC);
        cC.populateCommandHash();
        this.getCommand("couple").setExecutor(this.cC);
    }

    @Override
    public void onDisable() {

    }
    public void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this.pE, this);
    }




}
