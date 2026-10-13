package MiniCash.itemLoader.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public class ItemParser {

    public static ItemStack parse(ConfigurationSection section, String itemId, String filePath, Logger logger) {

        String materialString = section.getString("material");

        if (materialString == null) {

            LoggerUtil.logConfigError(logger, filePath, itemId, "必須項目 Material が指定されていないため、アイテムの読み込みを中止しました");

            return null;

        }

        Material material = Material.matchMaterial(materialString.toUpperCase());

        if (material == null) {

            LoggerUtil.logConfigError(logger, filePath, itemId, "不明なマテリアル名（" + materialString + "）が指定されているため、アイテムの読み込みを中止しました");

            return null;

        }


        int amount = 1;

        if (section.contains("amount")) {

            if (!section.isInt("amount")) {

                LoggerUtil.logConfigError(logger, filePath, itemId, "amountに数値以外の値が使われていたため、1 個として設定しました");

            }else {
                amount = section.getInt("amount");

            }

        }



        ItemStack item = new ItemStack(material, amount);

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null){
            return item;
        }



        if (section.contains("name")) {
            itemMeta.displayName(translateColor(section.getString("name")));
        }

        if (section.contains("lore")) {
            itemMeta.lore(section.getStringList("lore").stream().map(ItemParser::translateColor).toList());
        }

        // エンチャントの設定
        if (section.contains("enchants")) {

            for (String enchantStr : section.getStringList("enchants")) {

                int lastColon = enchantStr.lastIndexOf(':');

                if (lastColon == -1) {
                    LoggerUtil.logConfigError(logger, filePath, itemId, "enchants の書式（" + enchantStr + "）が不正です。'名前:レベル' の形式で書いてください。");
                    continue;
                }

                String name = enchantStr.substring(0, lastColon).trim();
                String levelString = enchantStr.substring(lastColon + 1).trim();

                NamespacedKey key = NamespacedKey.fromString(name.toLowerCase());

                if (key == null || !name.contains(":")) {
                    key = NamespacedKey.minecraft(name.toLowerCase());
                }

                Enchantment enchantment = Registry.ENCHANTMENT.get(key);

                if (enchantment != null) {
                    try {
                        int level = Integer.parseInt(levelString);

                        itemMeta.addEnchant(enchantment, level, true);

                    }catch (NumberFormatException e) {
                        LoggerUtil.logConfigError(logger, filePath, itemId, "enchants 内の '" + enchantStr + "' のレベルが数字ではありません。");
                    }

                } else {
                    LoggerUtil.logConfigError(logger, filePath, itemId, "enchants 内に存在しないエンチャント名（" + name + "）が指定されています。");
                }


            }
        }

        // ========================================================
        // durability セクション
        // ========================================================
        if (section.contains("durability") && section.isConfigurationSection("durability")) {
            ConfigurationSection durabilitySection = section.getConfigurationSection("durability");

            //  不可破
            if (durabilitySection.contains("unbreakable")) {

                if (!durabilitySection.isBoolean("unbreakable")) {
                    LoggerUtil.logConfigError(logger, filePath, itemId, "durability.unbreakable に真偽値(true/false)以外の値が設定されています。");
                } else {
                    itemMeta.setUnbreakable(durabilitySection.getBoolean("unbreakable"));
                }
            }
        }

        // ========================================================
        // model(見た目)設定
        // ========================================================
        if (section.contains("model") && section.isConfigurationSection("model")) {

            ConfigurationSection modelSection = section.getConfigurationSection("model");

            if (modelSection.contains("custom-model-data")) {

                if (!modelSection.isInt("custom-model-data")) {

                    LoggerUtil.logConfigError(logger, filePath, itemId, "model.custom-model-data に整数以外の値が設定されています。");

                } else {
                    itemMeta.setCustomModelData(modelSection.getInt("custom-model-data"));
                }

            }

            if (modelSection.contains("item-model")) {

                String modelString = modelSection.getString("item-model");

                if (modelString != null && !modelString.isEmpty()) {

                    try {

                        NamespacedKey key = NamespacedKey.fromString(modelString.toLowerCase());

                        if (key != null) {
                            itemMeta.setItemModel(key);
                        }else {
                            LoggerUtil.logConfigError(logger, filePath, itemId, "model.item-model の書式（" + modelString + "）が不正です。");
                        }

                    }catch (NoSuchMethodError e) {
                        logger.fine("このサーバーバージョンは model.item-model に対応していません。");
                    }


                }


            }

        }

        item.setItemMeta(itemMeta);
        return item;

    }


    private static Component translateColor(String text) {

        if (text == null) {
            return Component.empty();
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);

    }

}


