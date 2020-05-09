package io.github.Kilobytz.rrstuff;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.Kilobytz.rrstuff.commands.CoupleCommands;
import io.github.Kilobytz.rrstuff.commands.GeneralCommands;
import io.github.Kilobytz.rrstuff.couple.PairConstructor;
import io.github.Kilobytz.rrstuff.couple.PairEffects;
import io.github.Kilobytz.rrstuff.loader.LoaderInit;
import io.github.Kilobytz.rrstuff.misc.SnowballDeath;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main  extends JavaPlugin {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    PluginManager pluginManager = getServer().getPluginManager();
    PairConstructor pC = new PairConstructor(this);
    PairEffects pE = new PairEffects();
    CoupleCommands cC = new CoupleCommands();
    GeneralCommands gC = new GeneralCommands();
    LoaderInit lI = new LoaderInit(this, pluginManager);
    SnowballDeath sD = new SnowballDeath(this);

    @Override
    public void onEnable() {
        createConfig();
        registerListeners();
        this.getCommand("couple").setExecutor(this.cC);
        this.getCommand("rr").setExecutor(this.gC);
        setupInit();
    }

    @Override
    public void onDisable() {

    }
    public void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this.pE, this);
        pluginManager.registerEvents(this.sD, this);
    }

    public void setupInit() {
        pE.setPairs(pC);
        pC.setHunger(true);
        cC.setCouples(pC);
        cC.populateCommandHash();
        gC.populateCommandHash();
        gC.setLoaderData(lI);
        gC.setSnowballInfo(sD);
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

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
