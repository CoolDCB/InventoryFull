package me.dave.inventoryfull.command;

import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.inventoryfull.InventoryFull;
import me.dave.inventoryfull.utils.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    if (!sender.hasPermission("inventoryfull.reload")) {
                        ChatColorHandler.sendMessage(sender, InventoryFull.getConfigManager().getMessage("no-permissions"));
                        return true;
                    }
                    InventoryFull.getConfigManager().reloadConfig();

                    ChatColorHandler.sendMessage(sender, InventoryFull.getConfigManager().getMessage("reload"));
                    return true;
                }
                case "update" -> {
                    if (!sender.hasPermission("inventoryfull.update")) {
                        ChatColorHandler.sendMessage(sender, InventoryFull.getConfigManager().getMessage("no-permissions"));
                        return true;
                    }

                    Updater updater = InventoryFull.getUpdater();

                    if (updater.isAlreadyDownloaded() || !updater.isUpdateAvailable()) {
                        ChatColorHandler.sendMessage(sender, "&#ff6969It looks like there is no new update available!");
                        return true;
                    }

                    updater.downloadUpdate().thenAccept(success -> {
                        if (success) {
                            ChatColorHandler.sendMessage(sender, "&#b7faa2Successfully updated ActivityRewarder, restart the server to apply changes!");
                        } else {
                            ChatColorHandler.sendMessage(sender, "&#ff6969Failed to update ActivityRewarder!");
                        }
                    });

                    return true;
                }
            }
        }

        ChatColorHandler.sendMessage(sender, "&#a8e1ffYou are currently running InventoryFull version &#58b1e0" + InventoryFull.getInstance().getDescription().getVersion());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabComplete = new ArrayList<>();
        List<String> wordCompletion = new ArrayList<>();
        boolean wordCompletionSuccess = false;

        if (args.length == 1) {
            if (sender.hasPermission("inventoryfull.reload")) {
                tabComplete.add("reload");
            }
            if (sender.hasPermission("inventoryfull.update")) {
                tabComplete.add("update");
            }
        }

        for (String currTab : tabComplete) {
            int currArg = args.length - 1;
            if (currTab.startsWith(args[currArg])) {
                wordCompletion.add(currTab);
                wordCompletionSuccess = true;
            }
        }

        return wordCompletionSuccess ? wordCompletion : tabComplete;
    }
}
