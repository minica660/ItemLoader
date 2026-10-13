package MiniCash.itemLoader.command;

import MiniCash.itemLoader.ItemLoaderAPI;
import MiniCash.itemLoader.ItemRegistry;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ItemLoaderCommand implements BasicCommand {

    private final JavaPlugin plugin;
    private final ItemRegistry itemRegistry;

    public ItemLoaderCommand(JavaPlugin plugin,ItemRegistry itemRegistry) {
        this.plugin = plugin;
        this.itemRegistry = itemRegistry;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {

        CommandSender sender = commandSourceStack.getSender();

        if (args.length > 0) {

            if (args[0].equals("reload")) {

                sender.sendMessage(ItemLoaderAPI.getMessage(
                        Component.text("設定ファイルを再読み込み中...", NamedTextColor.GREEN)
                ));

                itemRegistry.reload(plugin.getDataFolder());

                sender.sendMessage(ItemLoaderAPI.getMessage(
                        Component.text("設定ファイルの再読み込み完了", NamedTextColor.AQUA)
                ));

                return;

            }

            if (args[0].equals("give")) {

                if (args.length < 3) {

                    sender.sendMessage(ItemLoaderAPI.getMessage(
                            Component.text("使用方法: /itemloader give <player> <id>", NamedTextColor.RED)
                    ));
                    return;
                }


                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {

                    sender.sendMessage(ItemLoaderAPI.getMessage(
                            Component.text("指定したターゲット: " + args[1] +" は不明です" , NamedTextColor.RED)
                    ));

                    return;
                }


                String itemKey = args[2].toLowerCase();

                ItemStack item = itemRegistry.get(itemKey);

                if (item == null) {

                    sender.sendMessage(ItemLoaderAPI.getMessage(
                            Component.text("カスタムアイテム: " + itemKey + "が見つかりませんでした", NamedTextColor.RED)
                    ));

                    return;
                }

                target.getInventory().addItem(item);

                sender.sendMessage(ItemLoaderAPI.getMessage(
                        Component.text(target.getName() + "に" + itemKey + "を付与しました！", NamedTextColor.DARK_GREEN)
                ));

                target.sendMessage(ItemLoaderAPI.getMessage(
                        Component.text("アイテム： " + itemKey + " を受け取りました" , NamedTextColor.GRAY)
                ));

                return;


            }
        }


        sender.sendMessage(ItemLoaderAPI.getMessage(Component.text("===== ItemLoaderAPI ヘルプ =====" , NamedTextColor.GREEN)));

        sender.sendMessage(ItemLoaderAPI.getMessage(
                Component.text("設定ファイルの再読み込み： /itemloader reload" , NamedTextColor.AQUA)
        ));

        sender.sendMessage(ItemLoaderAPI.getMessage(
                Component.text("アイテム取得： /itemloader give <player> <id>" , NamedTextColor.AQUA)
        ));


        sender.sendMessage(ItemLoaderAPI.getMessage(Component.text("====================" , NamedTextColor.GREEN)));

        return;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {

        if (args.length <= 1) {

            List<String> suggest = List.of("reload", "give");

            if (args.length == 1) {
                String currentArg = args[0].toLowerCase();
                return suggest.stream()
                        .filter(sub -> sub.startsWith(currentArg))
                        .toList();
            }

            return suggest;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("give")) {

            if (args.length == 2) {

                String currentArg = args[1].toLowerCase();

                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(currentArg))
                        .toList();

            }

            if (args.length == 3) {

                String currentArg = args[2].toLowerCase();

                return itemRegistry.getAllItemKeys().stream()
                        .filter(key -> key.toLowerCase().startsWith(currentArg))
                        .toList();

            }
        }

        return Collections.emptyList();

    }

    @Override
    public @Nullable String permission() {
        return "itemloader.commands.itemloader";
    }
}
