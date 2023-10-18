package me.dave.inventoryfull;

import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.inventoryfull.command.MainCmd;
import me.dave.inventoryfull.config.ConfigManager;
import me.dave.inventoryfull.utils.Updater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class InventoryFull extends JavaPlugin {
    private static MorePaperLib morePaperLib;
    private static Updater updater;

    private static InventoryFull plugin;
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;

        morePaperLib = new MorePaperLib(this);
        updater = new Updater(this, "inventory-full", "inventoryfull update");

        configManager = new ConfigManager();
        configManager.reloadConfig();

        getCommand("inventoryfull").setExecutor(new MainCmd());

        morePaperLib.scheduling().asyncScheduler().runAtFixedRate(this::heartBeat, Duration.of(1000, ChronoUnit.MILLIS), Duration.of(configManager.getPeriod() * 50L, ChronoUnit.MILLIS));
    }

    @Override
    public void onDisable() {
        if (morePaperLib != null) {
            morePaperLib.scheduling().cancelGlobalTasks();
            morePaperLib = null;
        }

        configManager = null;
    }

    private void heartBeat() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Inventory inventory = player.getInventory();
            List<ItemStack> items = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).toList();
            int contents = items.size();

            if (contents >= 36) {
                sendMessage(player);
            }
        });
    }

    private void sendMessage(Player player) {
        switch (configManager.getMessageType()) {
            case MESSAGE -> ChatColorHandler.sendMessage(player, configManager.getMessage("inventory-full"));
            case ACTION, ACTION_BAR -> ChatColorHandler.sendActionBarMessage(player, configManager.getMessage("inventory-full"));
            case TITLE -> {}
        }
    }

    public static MorePaperLib getMorePaperLib() {
        return morePaperLib;
    }

    public static Updater getUpdater() {
        return updater;
    }

    public static InventoryFull getInstance() {
        return plugin;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
