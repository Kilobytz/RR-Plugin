package io.github.Kilobytz.rrstuff;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.Kilobytz.rrstuff.commands.CoupleCommands;
import io.github.Kilobytz.rrstuff.couple.PairConstructor;
import io.github.Kilobytz.rrstuff.couple.PairEffects;
import io.github.Kilobytz.rrstuff.loader.LoaderInit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main  extends JavaPlugin {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    PluginManager pluginManager = getServer().getPluginManager();
    PairConstructor pC = new PairConstructor(this);
    PairEffects pE = new PairEffects();
    CoupleCommands cC = new CoupleCommands();
    LoaderInit lI = new LoaderInit(this, pluginManager);


    @Override
    public void onEnable() {
        registerListeners();
        this.getCommand("couple").setExecutor(this.cC);
        setupInit();
    }

    @Override
    public void onDisable() {

    }
    public void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this.pE, this);
    }

    public void setupInit() {
        pE.setPairs(pC);
        pC.setMaxHP(20);
        pC.setHunger(true);
        cC.setCouples(pC);
        cC.populateCommandHash();
        pC.setAddSync(false);
        startLoaderTimer();
    }

    public void startLoaderTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                lI.entityCheck();
            }
        },50L, 1L);
    }


}
