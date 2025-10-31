package com.notitzdark.traders.model;

import org.bukkit.entity.LivingEntity;
import java.util.List;
import java.util.UUID;

public class Trader {
    
    private final UUID entityId;
    private final TraderType type;
    private final List<Trade> trades;
    private final long spawnTime;
    private final long despawnTime;

    public Trader(UUID entityId, TraderType type, List<Trade> trades, long spawnTime, long despawnTime) {
        this.entityId = entityId;
        this.type = type;
        this.trades = trades;
        this.spawnTime = spawnTime;
        this.despawnTime = despawnTime;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public TraderType getType() {
        return type;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public long getDespawnTime() {
        return despawnTime;
    }

    public boolean shouldDespawn() {
        return System.currentTimeMillis() >= despawnTime;
    }
}
