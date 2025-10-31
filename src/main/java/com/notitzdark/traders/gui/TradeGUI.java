package com.notitzdark.traders.gui;

import com.notitzdark.traders.TradersPlugin;
import com.notitzdark.traders.model.Trader;
import com.notitzdark.traders.model.Trade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TradeGUI implements Listener {
    
    private final TradersPlugin plugin;
    private final Player player;
    private final Trader trader;
    private final Inventory inventory;

    public TradeGUI(TradersPlugin plugin, Player player, Trader trader) {
        this.plugin = plugin;
        this.player = player;
        this.trader = trader;
        this.inventory = Bukkit.createInventory(null, 27, trader.getType().getDisplayName());
        
        setupInventory();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setupInventory() {
        // Fill background
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, filler);
        }
        
        // Add trades
        List<Trade> trades = trader.getTrades();
        int[] tradeSlots = {10, 11, 12, 14, 15};
        
        for (int i = 0; i < Math.min(trades.size(), tradeSlots.length); i++) {
            Trade trade = trades.get(i);
            ItemStack display = createTradeDisplay(trade);
            inventory.setItem(tradeSlots[i], display);
        }
    }

    private ItemStack createTradeDisplay(Trade trade) {
        ItemStack display = trade.getOutput().clone();
        ItemMeta meta = display.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Required:");
        lore.add("§f  " + trade.getInput().getAmount() + "x " + 
                 formatMaterialName(trade.getInput().getType()));
        lore.add("");
        lore.add("§7You receive:");
        lore.add("§f  " + trade.getOutput().getAmount() + "x " + 
                 formatMaterialName(trade.getOutput().getType()));
        lore.add("");
        lore.add("§eClick to trade!");
        
        meta.setLore(lore);
        display.setItemMeta(meta);
        
        return display;
    }

    private String formatMaterialName(Material material) {
        String name = material.name().toLowerCase().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
        }
        
        return result.toString().trim();
    }

    public void open() {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        
        Player clicker = (Player) event.getWhoClicked();
        
        // Find the trade
        List<Trade> trades = trader.getTrades();
        int[] tradeSlots = {10, 11, 12, 14, 15};
        
        for (int i = 0; i < Math.min(trades.size(), tradeSlots.length); i++) {
            if (event.getSlot() == tradeSlots[i]) {
                processTrade(clicker, trades.get(i));
                break;
            }
        }
    }

    private void processTrade(Player player, Trade trade) {
        ItemStack required = trade.getInput();
        
        if (!player.getInventory().containsAtLeast(required, required.getAmount())) {
            player.sendMessage("§cYou don't have enough " + 
                             formatMaterialName(required.getType()) + "!");
            return;
        }
        
        // Remove items
        player.getInventory().removeItem(required);
        
        // Give output
        ItemStack output = trade.getOutput();
        player.getInventory().addItem(output);
        
        player.sendMessage("§aTrade completed!");
        player.closeInventory();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        HandlerList.unregisterAll(this);
    }
}
