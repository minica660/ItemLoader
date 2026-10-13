package MiniCash.itemLoader;

import MiniCash.itemLoader.util.ItemParser;
import MiniCash.itemLoader.util.LoggerUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemRegistry {

    private final JavaPlugin plugin;
    private final Map<String, ItemStack> registryItems = new HashMap<>();
    private final Map<String, String> idSourceMap = new HashMap<>();

    public ItemRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload(File dataFolder) {

        registryItems.clear();

        idSourceMap.clear();

        File dataDir = new File(dataFolder, "data");
        if (!dataDir.exists()) {
            plugin.saveResource("data/sample.yml", false);
            plugin.saveResource("data/weapons/swords.yml", false);

            plugin.getLogger().info("初期サンプルファイルを自動生成しました");
        }

        scanFolder(dataDir);
        plugin.getLogger().info("合計 " + registryItems.size() + " 個のカスタムアイテムをロードしました");
    }

    private void scanFolder(File folder) {

        File[] files = folder.listFiles();

        if (files == null){
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file);
            } else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                loadItemsFromFile(file);
            }
        }
    }

    private void loadItemsFromFile(File file) {
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (InvalidConfigurationException e) {
            LoggerUtil.logSyntaxError(plugin.getLogger(), file.getPath(), e.getMessage());
            return;
        } catch (Exception e) {
            LoggerUtil.logSystemError(plugin.getLogger(), file.getPath(), "ファイルのロード中に例外が発生しました", e);
            return;
        }

        for (String itemId : config.getKeys(false)) {
            String lowerItemId = itemId.toLowerCase();

            // 識別IDのグローバル重複チェック
            if (registryItems.containsKey(lowerItemId)) {
                String originalPath = idSourceMap.get(lowerItemId);
                LoggerUtil.logConfigError(plugin.getLogger(), file.getPath(), itemId,
                        "識別IDが重複しています！このIDは既に別のファイルで登録されています\n" +
                                "[ItemLoaderAPI] 先に登録された場所: " + originalPath);
                continue;
            }

            org.bukkit.configuration.ConfigurationSection section = config.getConfigurationSection(itemId);
            if (section == null) continue;

            try {
                ItemStack item = ItemParser.parse(section, itemId, file.getPath(), plugin.getLogger());
                if (item != null) {
                    registryItems.put(lowerItemId, item);
                    idSourceMap.put(lowerItemId, file.getPath());
                }
            } catch (Exception e) {
                LoggerUtil.logSystemError(plugin.getLogger(), file.getPath(), "アイテム '" + itemId + "' の処理中に致命的な例外が発生しました。", e);
            }
        }
    }

    public ItemStack get(String itemId) {
        ItemStack cachedItem = registryItems.get(itemId.toLowerCase());
        return cachedItem != null ? cachedItem.clone() : null;
    }

    public Set<String> getAllItemKeys() {
        return registryItems.keySet();
    }


}
