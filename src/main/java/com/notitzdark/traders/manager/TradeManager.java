package com.notitzdark.traders.manager;

import com.notitzdark.traders.TradersPlugin;
import com.notitzdark.traders.model.Trade;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TradeManager {
    
    private final TradersPlugin plugin;
    private final Random random;
    private final List<TradeTemplate> tradeTemplates;

    public TradeManager(TradersPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.tradeTemplates = new ArrayList<>();
        
        initializeTradeTemplates();
    }

    private void initializeTradeTemplates() {
        // Raw materials to processed
        tradeTemplates.add(new TradeTemplate(Material.RAW_IRON, 20, 30, Material.IRON_INGOT, 20, 35));
        tradeTemplates.add(new TradeTemplate(Material.RAW_GOLD, 15, 25, Material.GOLD_INGOT, 15, 30));
        tradeTemplates.add(new TradeTemplate(Material.RAW_COPPER, 30, 50, Material.COPPER_INGOT, 30, 55));
        
        // Ores and gems
        tradeTemplates.add(new TradeTemplate(Material.DIAMOND, 5, 10, Material.DIAMOND_CHESTPLATE, 1, 1));
        tradeTemplates.add(new TradeTemplate(Material.EMERALD, 8, 15, Material.ENCHANTED_GOLDEN_APPLE, 1, 1));
        tradeTemplates.add(new TradeTemplate(Material.GOLD_INGOT, 10, 16, Material.ENDER_PEARL, 1, 1));
        tradeTemplates.add(new TradeTemplate(Material.IRON_INGOT, 15, 25, Material.DIAMOND, 1, 2));
        
        // Building blocks
        tradeTemplates.add(new TradeTemplate(Material.COBBLESTONE, 32, 64, Material.COAL, 4, 8));
        tradeTemplates.add(new TradeTemplate(Material.OAK_LOG, 20, 40, Material.IRON_INGOT, 3, 6));
        tradeTemplates.add(new TradeTemplate(Material.STONE, 32, 64, Material.REDSTONE, 8, 16));
        
        // Food trades
        tradeTemplates.add(new TradeTemplate(Material.WHEAT, 20, 32, Material.BREAD, 10, 16));
        tradeTemplates.add(new TradeTemplate(Material.CARROT, 16, 24, Material.GOLDEN_CARROT, 4, 8));
        tradeTemplates.add(new TradeTemplate(Material.POTATO, 16, 24, Material.BAKED_POTATO, 16, 24));
        
        // Nether materials
        tradeTemplates.add(new TradeTemplate(Material.NETHERRACK, 32, 64, Material.GLOWSTONE_DUST, 8, 16));
        tradeTemplates.add(new TradeTemplate(Material.SOUL_SAND, 16, 32, Material.NETHER_WART, 8, 16));
        tradeTemplates.add(new TradeTemplate(Material.QUARTZ, 20, 32, Material.EXPERIENCE_BOTTLE, 4, 8));
        
        // Tools and armor
        tradeTemplates.add(new TradeTemplate(Material.IRON_INGOT, 20, 30, Material.IRON_SWORD, 1, 1));
        tradeTemplates.add(new TradeTemplate(Material.DIAMOND, 3, 5, Material.DIAMOND_PICKAXE, 1, 1));
        tradeTemplates.add(new TradeTemplate(Material.GOLD_INGOT, 15, 25, Material.GOLDEN_APPLE, 2, 4));
        
        // Mob drops
        tradeTemplates.add(new TradeTemplate(Material.ROTTEN_FLESH, 32, 64, Material.LEATHER, 8, 16));
        tradeTemplates.add(new TradeTemplate(Material.BONE, 20, 32, Material.BONE_MEAL, 32, 64));
        tradeTemplates.add(new TradeTemplate(Material.STRING, 16, 24, Material.WHITE_WOOL, 16, 24));
        
        // Rare materials
        tradeTemplates.add(new TradeTemplate(Material.OBSIDIAN, 8, 16, Material.ENDER_PEARL, 2, 4));
        tradeTemplates.add(new TradeTemplate(Material.BLAZE_ROD, 8, 12, Material.FIRE_CHARGE, 16, 24));
        tradeTemplates.add(new TradeTemplate(Material.ENDER_PEARL, 8, 12, Material.EXPERIENCE_BOTTLE, 8, 16));
        
        // Enchanted items
        tradeTemplates.add(new TradeTemplate(Material.DIAMOND, 10, 15, Material.ENCHANTED_BOOK, 1, 1, true));
        tradeTemplates.add(new TradeTemplate(Material.EMERALD, 12, 20, Material.DIAMOND_SWORD, 1, 1, true));
        tradeTemplates.add(new TradeTemplate(Material.GOLD_INGOT, 20, 30, Material.IRON_CHESTPLATE, 1, 1, true));
        
        // Redstone and tech
        tradeTemplates.add(new TradeTemplate(Material.REDSTONE, 20, 32, Material.REPEATER, 4, 8));
        tradeTemplates.add(new TradeTemplate(Material.IRON_INGOT, 10, 16, Material.PISTON, 2, 4));
        tradeTemplates.add(new TradeTemplate(Material.SLIME_BALL, 8, 16, Material.STICKY_PISTON, 2, 4));
        
        // Ocean materials
        tradeTemplates.add(new TradeTemplate(Material.PRISMARINE_SHARD, 16, 24, Material.PRISMARINE, 8, 16));
        tradeTemplates.add(new TradeTemplate(Material.INK_SAC, 16, 24, Material.BLACK_DYE, 16, 24));
        tradeTemplates.add(new TradeTemplate(Material.COD, 16, 24, Material.COOKED_COD, 16, 24));
    }

    public List<Trade> generateTrades(int count) {
        List<Trade> trades = new ArrayList<>();
        List<TradeTemplate> available = new ArrayList<>(tradeTemplates);
        Collections.shuffle(available);
        
        for (int i = 0; i < Math.min(count, available.size()); i++) {
            trades.add(available.get(i).createTrade(random));
        }
        
        return trades;
    }

    private static class TradeTemplate {
        private final Material inputMaterial;
        private final int minInputAmount;
        private final int maxInputAmount;
        private final Material outputMaterial;
        private final int minOutputAmount;
        private final int maxOutputAmount;
        private final boolean enchant;

        public TradeTemplate(Material inputMaterial, int minInputAmount, int maxInputAmount,
                           Material outputMaterial, int minOutputAmount, int maxOutputAmount) {
            this(inputMaterial, minInputAmount, maxInputAmount, outputMaterial, minOutputAmount, maxOutputAmount, false);
        }

        public TradeTemplate(Material inputMaterial, int minInputAmount, int maxInputAmount,
                           Material outputMaterial, int minOutputAmount, int maxOutputAmount, boolean enchant) {
            this.inputMaterial = inputMaterial;
            this.minInputAmount = minInputAmount;
            this.maxInputAmount = maxInputAmount;
            this.outputMaterial = outputMaterial;
            this.minOutputAmount = minOutputAmount;
            this.maxOutputAmount = maxOutputAmount;
            this.enchant = enchant;
        }

        public Trade createTrade(Random random) {
            int inputAmount = ThreadLocalRandom.current().nextInt(minInputAmount, maxInputAmount + 1);
            int outputAmount = ThreadLocalRandom.current().nextInt(minOutputAmount, maxOutputAmount + 1);
            
            ItemStack input = new ItemStack(inputMaterial, inputAmount);
            ItemStack output = new ItemStack(outputMaterial, outputAmount);
            
            if (enchant) {
                applyRandomEnchantment(output, random);
            }
            
            return new Trade(input, output);
        }

        private void applyRandomEnchantment(ItemStack item, Random random) {
            if (item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                Enchantment enchant = getRandomEnchantment(random);
                int level = random.nextInt(enchant.getMaxLevel()) + 1;
                meta.addStoredEnchant(enchant, level, false);
                item.setItemMeta(meta);
            } else {
                Enchantment enchant = getRandomEnchantmentForItem(item, random);
                if (enchant != null) {
                    int level = random.nextInt(Math.min(enchant.getMaxLevel(), 2)) + 1;
                    item.addUnsafeEnchantment(enchant, level);
                }
            }
        }

        private Enchantment getRandomEnchantment(Random random) {
            List<Enchantment> enchants = Arrays.asList(
                Enchantment.SHARPNESS, Enchantment.PROTECTION, Enchantment.EFFICIENCY,
                Enchantment.UNBREAKING, Enchantment.FORTUNE, Enchantment.LOOTING
            );
            return enchants.get(random.nextInt(enchants.size()));
        }

        private Enchantment getRandomEnchantmentForItem(ItemStack item, Random random) {
            if (item.getType().name().contains("SWORD")) {
                return random.nextBoolean() ? Enchantment.SHARPNESS : Enchantment.UNBREAKING;
            } else if (item.getType().name().contains("CHESTPLATE") || item.getType().name().contains("HELMET")) {
                return random.nextBoolean() ? Enchantment.PROTECTION : Enchantment.UNBREAKING;
            } else if (item.getType().name().contains("PICKAXE")) {
                return random.nextBoolean() ? Enchantment.EFFICIENCY : Enchantment.UNBREAKING;
            }
            return Enchantment.UNBREAKING;
        }
    }
}
