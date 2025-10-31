package com.notitzdark.traders.model;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum TraderType {
    
    OVERWORLD("§aTrader", EntityType.ZOMBIE, Material.RAW_IRON, 2, 4),
    NETHER("§6Nether Trader", EntityType.PIGLIN, Material.GOLD_INGOT, 2, 5);

    private final String displayName;
    private final EntityType entityType;
    private final Material dropMaterial;
    private final int minDrop;
    private final int maxDrop;

    TraderType(String displayName, EntityType entityType, Material dropMaterial, int minDrop, int maxDrop) {
        this.displayName = displayName;
        this.entityType = entityType;
        this.dropMaterial = dropMaterial;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Material getDropMaterial() {
        return dropMaterial;
    }

    public int getMinDrop() {
        return minDrop;
    }

    public int getMaxDrop() {
        return maxDrop;
    }
}
