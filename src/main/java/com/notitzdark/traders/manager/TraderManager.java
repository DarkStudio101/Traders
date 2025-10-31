package com.notitzdark.traders.manager;

import com.notitzdark.traders.TradersPlugin;
import com.notitzdark.traders.model.Trader;
import com.notitzdark.traders.model.TraderType;
import com.notitzdark.traders.model.Trade;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TraderManager {
    
    private final TradersPlugin plugin;
    private final Map<UUID, Trader> traders;
    private final Random random;

    public TraderManager(TradersPlugin plugin) {
        this.plugin = plugin;
        this.traders = new HashMap<>();
        this.random = new Random();
        
        startDespawnTask();
    }

    public Trader spawnTrader(Location location, TraderType type) {
        LivingEntity entity;
        
        if (type == TraderType.OVERWORLD) {
            Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.setBaby(true);
            zombie.setShouldBurnInDay(false);
            entity = zombie;
        } else {
            Piglin piglin = (Piglin) location.getWorld().spawnEntity(location, EntityType.PIGLIN);
            piglin.setBaby(true);
            piglin.setImmuneToZombification(true);
            entity = piglin;
        }
        
        setupTraderEntity(entity, type);
        
        List<Trade> trades = plugin.getTradeManager().generateTrades(
            plugin.getConfig().getInt("trades.count", 5)
        );
        
        long spawnTime = System.currentTimeMillis();
        int minDespawn = plugin.getConfig().getInt("despawn.min-time", 600) * 1000;
        int maxDespawn = plugin.getConfig().getInt("despawn.max-time", 900) * 1000;
        long despawnTime = spawnTime + ThreadLocalRandom.current().nextInt(minDespawn, maxDespawn + 1);
        
        Trader trader = new Trader(entity.getUniqueId(), type, trades, spawnTime, despawnTime);
        traders.put(entity.getUniqueId(), trader);
        
        return trader;
    }

    private void setupTraderEntity(LivingEntity entity, TraderType type) {
        entity.setCustomName(type.getDisplayName());
        entity.setCustomNameVisible(true);
        entity.setRemoveWhenFarAway(false);
        entity.setCanPickupItems(false);
        
        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.15);
        }
        
        entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            if (type == TraderType.OVERWORLD) {
                equipment.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                equipment.setLeggings(getRandomArmor(ArmorSlot.LEGS));
                equipment.setBoots(getRandomArmor(ArmorSlot.BOOTS));
            } else {
                equipment.setHelmet(new ItemStack(Material.GOLDEN_HELMET));
                equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                equipment.setLeggings(getRandomArmor(ArmorSlot.LEGS));
                equipment.setBoots(getRandomArmor(ArmorSlot.BOOTS));
            }
            
            equipment.setHelmetDropChance(0.0f);
            equipment.setChestplateDropChance(0.0f);
            equipment.setLeggingsDropChance(0.0f);
            equipment.setBootsDropChance(0.0f);
        }
        
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
            mob.setAware(true);
        }
    }

    private ItemStack getRandomArmor(ArmorSlot slot) {
        Material[] materials;
        
        switch (slot) {
            case LEGS:
                materials = new Material[]{
                    Material.LEATHER_LEGGINGS,
                    Material.CHAINMAIL_LEGGINGS,
                    Material.IRON_LEGGINGS,
                    Material.GOLDEN_LEGGINGS,
                    Material.DIAMOND_LEGGINGS
                };
                break;
            case BOOTS:
                materials = new Material[]{
                    Material.LEATHER_BOOTS,
                    Material.CHAINMAIL_BOOTS,
                    Material.IRON_BOOTS,
                    Material.GOLDEN_BOOTS,
                    Material.DIAMOND_BOOTS
                };
                break;
            default:
                return new ItemStack(Material.AIR);
        }
        
        return new ItemStack(materials[random.nextInt(materials.length)]);
    }

    public Trader getTrader(UUID entityId) {
        return traders.get(entityId);
    }

    public void removeTrader(UUID entityId) {
        traders.remove(entityId);
    }

    public int getTraderCount() {
        return traders.size();
    }

    public void removeAllTraders() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (traders.containsKey(entity.getUniqueId())) {
                    entity.remove();
                }
            }
        }
        traders.clear();
    }

    private void startDespawnTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<UUID> toDespawn = new ArrayList<>();
            
            for (Map.Entry<UUID, Trader> entry : traders.entrySet()) {
                if (entry.getValue().shouldDespawn()) {
                    toDespawn.add(entry.getKey());
                }
            }
            
            for (UUID id : toDespawn) {
                Entity entity = Bukkit.getEntity(id);
                if (entity != null) {
                    entity.remove();
                }
                traders.remove(id);
            }
        }, 100L, 100L);
    }

    private enum ArmorSlot {
        LEGS, BOOTS
    }
}
