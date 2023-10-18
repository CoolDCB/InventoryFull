package me.dave.inventoryfull.config;

import me.dave.inventoryfull.InventoryFull;
import me.dave.inventoryfull.utils.MessageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private MessageType messageType;
    private int period;
    private final ConcurrentHashMap<String, String> messages = new ConcurrentHashMap<>();

    public ConfigManager() {
        InventoryFull.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        InventoryFull.getInstance().reloadConfig();
        FileConfiguration config = InventoryFull.getInstance().getConfig();

        try {
            messageType = MessageType.valueOf(config.getString("message-type", "message").toUpperCase());
        } catch (IllegalArgumentException e) {
            InventoryFull.getInstance().getLogger().severe("'message-type' has to be one of the following: " + Arrays.toString(MessageType.values()));
        }
        period = config.getInt("period", 20);

        // Clears messages map
        messages.clear();
        // Checks if messages section exists
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            // Repopulates messages map
            messagesSection.getValues(false).forEach((key, value) -> messages.put(key, (String) value));
        }
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public int getPeriod() {
        return period;
    }

    public String getMessage(String messageName) {
        String output = messages.getOrDefault(messageName, "");

        if (messages.containsKey("prefix")) {
            return output.replaceAll("%prefix%", messages.get("prefix"));
        } else {
            return output;
        }
    }
}
