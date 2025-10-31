package com.notitzdark.traders.listener;

import com.notitzdark.traders.TradersPlugin;
import com.notitzdark.traders.model.Trader;
import com.notitzdark.traders.model.Trade;
import com.notitzdark.traders.gui.TradeGUI;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class TraderListener implements Listener {
    
    private final TradersPlugin plugin;

    public TraderListener(TradersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity)) return;
        
        Trader trader = plugin.getTraderManager().getTrader(event.getRightClicked().getUniqueId());
        if (trader == null) return;
        
        event.setCancelled(true);
        new TradeGUI(plugin, event.getPlayer(), trader).open();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        Trader trader = plugin.getTraderManager().getTrader(event.getEntity().getUniqueId());
        if (trader == null) return;
        
        event.getDrops().clear();
        event.setDroppedExp(0);
        
        int amount = ThreadLocalRandom.current().nextInt(
            trader.getType().getMinDrop(),
            trader.getType().getMaxDrop() + 1
        );
        
        event.getDrops().add(new ItemStack(trader.getType().getDropMaterial(), amount));
        plugin.getTraderManager().removeTrader(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        Trader trader = plugin.getTraderManager().getTrader(event.getEntity().getUniqueId());
        if (trader == null) return;
        
        if (event.getDamager() instanceof Player) {
            LivingEntity traderEntity = (LivingEntity) event.getEntity();
            Player attacker = (Player) event.getDamager();
            
            // Run away from attacker
            org.bukkit.util.Vector direction = traderEntity.getLocation().toVector()
                .subtract(attacker.getLocation().toVector()).normalize();
            
            traderEntity.setVelocity(direction.multiply(0.6).setY(0.3));
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() == null) return;
        
        Trader trader = plugin.getTraderManager().getTrader(event.getTarget().getUniqueId());
        if (trader != null) {
            event.setCancelled(true);
        }
        
        if (event.getEntity() instanceof Mob) {
            Trader mobTrader = plugin.getTraderManager().getTrader(event.getEntity().getUniqueId());
            if (mobTrader != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        Trader trader = plugin.getTraderManager().getTrader(event.getEntity().getUniqueId());
        if (trader != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Trader trader = plugin.getTraderManager().getTrader(event.getEntity().getUniqueId());
        if (trader != null) {
            event.setCancelled(true);
        }
    }
}
