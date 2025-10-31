package com.notitzdark.traders.model;

import org.bukkit.inventory.ItemStack;

public class Trade {
    
    private final ItemStack input;
    private final ItemStack output;

    public Trade(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public ItemStack getInput() {
        return input.clone();
    }

    public ItemStack getOutput() {
        return output.clone();
    }
}
