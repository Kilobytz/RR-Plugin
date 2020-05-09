package io.github.Kilobytz.rrstuff.loader;

import io.github.Kilobytz.rrstuff.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LoaderHandling implements Listener {

    List<Location> locations = new ArrayList<>();
    List<Chunk> chunks = new ArrayList<>();
    boolean loaderCancelled = false;
    Main main;
    World world;
    Entity entity;
    Logger log = Bukkit.getLogger();

    public LoaderHandling(Main main,World world, Entity entity) {
        this.main = main;
        this.world = world;
        this.entity = entity;
    }

    public void initLoadingChunks() {
        Location entLoc = entity.getLocation();
        loadLocations(entLoc);
        for(Location loc : locations) {
            Chunk chunk = loc.getChunk();
            chunks.add(chunk);
            chunk.load();
        }
        locationCheck();
    }

    @EventHandler
    public void unloadedChunk(ChunkUnloadEvent event) {
        if(loaderCancelled) {
            return;
        }
        if(chunks != null) {
            for(Chunk chunk : chunks) {
                if(chunk == event.getChunk()) {
                    log.info("chunk loaded successfully");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if(entity == null) {
            return;
        }
        if (event.getEntity().equals(this.entity)) {
            loaderCancelled = true;
        }
    }

    public void loadLocations(Location loc) {
        double x5 = loc.getX();
        double y5 = loc.getY();
        double z5  = loc.getZ();

        Location loc1 = new Location(world,x5-16, y5, z5-16);
        Location loc2 = new Location(world,x5, y5, z5-16);
        Location loc3 = new Location(world,x5+16, y5, z5-16);
        Location loc4 = new Location(world,x5-16, y5, z5);
        Location loc5 = new Location(world,x5, y5, z5);
        Location loc6 = new Location(world,x5+16, y5, z5);
        Location loc7 = new Location(world,x5-16, y5, z5+16);
        Location loc8 = new Location(world,x5, y5, z5+16);
        Location loc9 = new Location(world,x5-16, y5, z5+16);

        locations.add(loc1);
        locations.add(loc2);
        locations.add(loc3);
        locations.add(loc4);
        locations.add(loc5);
        locations.add(loc6);
        locations.add(loc7);
        locations.add(loc8);
        locations.add(loc9);

    }
    public boolean isLoaderCancelled() {
        return loaderCancelled;
    }
    public void locationCheckTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

            @Override
            public void run() {
                locationCheck();
            }
        },0L, 5L);
    }
    public void locationCheck() {
        Chunk currentChunk = this.entity.getLocation().getChunk();
        if(currentChunk == chunks.get(4)) {
            return;
        }
        loadLocations(entity.getLocation());
    }
}
