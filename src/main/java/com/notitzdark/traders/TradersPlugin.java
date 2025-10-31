package com.notitzdark.traders;

import com.notitzdark.traders.command.SpawnTraderCommand;
import com.notitzdark.traders.manager.TraderManager;
import com.notitzdark.traders.manager.TradeManager;
import com.notitzdark.traders.manager.SpawnManager;
import com.notitzdark.traders.listener.TraderListener;
import org.bukkit.plugin.java.JavaPlugin;

public class TradersPlugin extends JavaPlugin {

    private TraderManager traderManager;
    private TradeManager tradeManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.tradeManager = new TradeManager(this);
        this.traderManager = new TraderManager(this);
        this.spawnManager = new SpawnManager(this);
        
        getServer().getPluginManager().registerEvents(new TraderListener(this), this);
        
        SpawnTraderCommand spawnTraderCommand = new SpawnTraderCommand(traderManager);
        getCommand("spawntrader").setExecutor(spawnTraderCommand);
        getCommand("spawntrader").setTabCompleter(spawnTraderCommand);
        
        spawnManager.startSpawning();
    }

    @Override
    public void onDisable() {
        if (spawnManager != null) {
            spawnManager.stopSpawning();
        }
        if (traderManager != null) {
            traderManager.removeAllTraders();
        }
    }

    public TraderManager getTraderManager() {
        return traderManager;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
}
