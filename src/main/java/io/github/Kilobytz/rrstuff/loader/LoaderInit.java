package io.github.Kilobytz.rrstuff.loader;

import io.github.Kilobytz.rrstuff.Main;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoaderInit {

    Main main;
    PluginManager pluginManager;
    List<Player> playersOnline = new ArrayList<>();
    HashMap<Entity,LoaderHandling> isLoading = new HashMap<>();
    World world;
    String loaderTag;


    public LoaderInit(Main main, PluginManager pluginManager) {
        this.main = main;
        this.pluginManager = pluginManager;
        try{
            this.loaderTag = main.getConfig().getString("ChunkLoaderTag = ");
        }catch(NullPointerException e) {
            this.loaderTag = "load";
        }
        if(loaderTag == null) {
            loaderTag = "load";
        }
    }



    public void entityCheck() {
        Server server = main.getServer();

        addOnlinePlayers();
        Player player1;
        try {
            player1 = playersOnline.get(0);
        }catch (IndexOutOfBoundsException e) {
            return;
        }
        this.world = player1.getWorld();
        for (Entity entity : world.getEntities()) {
            if(entity.getScoreboardTags() != null) {
                for(String tags : entity.getScoreboardTags()) {
                    if(tags.equalsIgnoreCase(loaderTag)) {
                        if(!isLoading.containsKey(entity)) {
                            LoaderHandling newLoader = new LoaderHandling(main, world, entity);
                            pluginManager.registerEvents(newLoader, main);
                            newLoader.initLoadingChunks();
                            isLoading.put(entity, newLoader);
                        }
                        LoaderHandling loaderInstance = isLoading.get(entity);
                        if(loaderInstance.isLoaderCancelled()) {
                            isLoading.remove(entity);
                            loaderInstance = null;
                        }
                    }
                }

            }
        }
    }

    public void changeLoaderTag(String tag) {
        String oldTag = this.loaderTag;
        for(Entity entities : world.getEntities()) {
            for(String word : entities.getScoreboardTags()) {
                if(word.equalsIgnoreCase(oldTag)) {
                    entities.addScoreboardTag(tag);
                }
            }
        }
        loaderTag = tag;
        main.getConfig().set("ChunkLoaderTag = " , loaderTag);
        main.saveConfig();
        for(Entity entities : world.getEntities()) {
            for(String word : entities.getScoreboardTags()) {
                if(word.equalsIgnoreCase(oldTag)) {
                    entities.removeScoreboardTag(oldTag);
                }
            }
        }
    }

    public String getLoaderTag() {
        return loaderTag;
    }

    public void addOnlinePlayers() {
        playersOnline.addAll(Bukkit.getOnlinePlayers());

    }

}
