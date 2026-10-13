package MiniCash.itemLoader;

import MiniCash.itemLoader.command.ItemLoaderCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemLoaderAPI extends JavaPlugin {

    private static ItemLoaderAPI itemLoaderAPI;
    private final ItemRegistry itemRegistry = new ItemRegistry(this);

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.itemLoaderAPI = this;

        itemRegistry.reload(getDataFolder());


        registerCommand("itemloader" , "ItemLoaderプラグインのメインコマンド" , new ItemLoaderCommand(this, itemRegistry));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static ItemStack getItem(String itemID){

        if (itemID == null || itemID.isBlank()){
            return null;
        }

        return itemLoaderAPI.itemRegistry.get(itemID);

    }


    public static Component getMessage(Component message){
        return Component.text("[").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("ItemLoader").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
                .append(message);

    }



}
