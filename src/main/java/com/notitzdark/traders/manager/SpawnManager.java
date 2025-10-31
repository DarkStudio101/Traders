package com.notitzdark.traders.manager;

import com.notitzdark.traders.TradersPlugin;
import com.notitzdark.traders.model.TraderType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnManager {
    
    private final TradersPlugin plugin;
    private final Random random;
    private BukkitTask spawnTask;

    public SpawnManager(TradersPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public void startSpawning() {
        int interval = plugin.getConfig().getInt("spawn.check-interval", 300) * 20;
        
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            if (onlinePlayers.isEmpty()) return;
            
            double spawnChance = plugin.getConfig().getDouble("spawn.spawn-chance", 0.3);
            if (random.nextDouble() > spawnChance) return;
            
            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
            attemptSpawn(randomPlayer);
            
        }, interval, interval);
    }

    public void stopSpawning() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }
    }

    public void attemptSpawn(Player player) {
        int maxPerPlayer = plugin.getConfig().getInt("spawn.max-per-player", 3);
        
        if (plugin.getTraderManager().getTraderCount() >= Bukkit.getOnlinePlayers().size() * maxPerPlayer) {
            return;
        }
        
        World.Environment environment = player.getWorld().getEnvironment();
        TraderType type;
        
        if (environment == World.Environment.NETHER) {
            type = TraderType.NETHER;
        } else if (environment == World.Environment.NORMAL) {
            type = TraderType.OVERWORLD;
        } else {
            return;
        }
        
        Location spawnLocation = findSpawnLocation(player, type);
        if (spawnLocation == null) return;
        
        int minDistance = plugin.getConfig().getInt("spawn.min-distance", 50);
        if (isNearbyTrader(spawnLocation, minDistance)) return;
        
        plugin.getTraderManager().spawnTrader(spawnLocation, type);
    }

    private Location findSpawnLocation(Player player, TraderType type) {
        Location playerLoc = player.getLocation();
        
        for (int attempt = 0; attempt < 10; attempt++) {
            int xOffset = ThreadLocalRandom.current().nextInt(-50, 51);
            int zOffset = ThreadLocalRandom.current().nextInt(-50, 51);
            
            Location checkLoc = playerLoc.clone().add(xOffset, 0, zOffset);
            
            if (type == TraderType.OVERWORLD) {
                // Find location below Y=30
                for (int y = 30; y > -64; y--) {
                    checkLoc.setY(y);
                    if (isValidSpawnLocation(checkLoc)) {
                        return checkLoc;
                    }
                }
            } else {
                // Nether spawn - find any valid location
                checkLoc.setY(playerLoc.getY());
                for (int yOffset = -10; yOffset <= 10; yOffset++) {
                    Location testLoc = checkLoc.clone().add(0, yOffset, 0);
                    if (isValidSpawnLocation(testLoc)) {
                        return testLoc;
                    }
                }
            }
        }
        
        return null;
    }

    private boolean isValidSpawnLocation(Location location) {
        if (!location.getBlock().isEmpty()) return false;
        if (!location.clone().add(0, 1, 0).getBlock().isEmpty()) return false;
        
        Location below = location.clone().add(0, -1, 0);
        return below.getBlock().getType().isSolid();
    }

    private boolean isNearbyTrader(Location location, int minDistance) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, minDistance, minDistance, minDistance)) {
            if (plugin.getTraderManager().getTrader(entity.getUniqueId()) != null) {
                return true;
            }
        }
        return false;
    }
}
