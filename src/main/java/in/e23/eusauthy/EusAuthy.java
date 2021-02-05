package in.e23.eusauthy;

import in.e23.eusauthy.cmd.Cmd2FA;
import in.e23.eusauthy.cmd.CmdAuthy;
import in.e23.eusauthy.lisenter.AuthyListener;
import in.e23.eusauthy.sql.DataInterface;
import in.e23.eusauthy.sql.SQLite;
import in.e23.eusauthy.utils.Authenticator;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.dependency.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ElaBosak
 */
@Dependency(maven = "de.taimos:totp:1.0")
@Dependency(maven = "commons-codec:commons-codec:1.10")
@Dependency(maven = "com.google.zxing:javase:3.2.1")
@Dependency(maven = "com.google.zxing:core:3.2.1")
public class EusAuthy extends Plugin {

    public static JavaPlugin plugin;
    public static DataInterface dataInterface;

    // Player Stats
    public static Map<Player, Boolean> isCreatingAuthy = new HashMap<>();
    public static Map<Player, Boolean> isDeletingAuthy = new HashMap<>();
    public static Map<Player, Boolean> is2faed = new HashMap<>();

    // Player Data RAM
    public static Map<Player, String> secretKeyRAM = new HashMap<>();
    public static Map<Player, ItemMeta> qrMeta = new HashMap<>();

    @Override
    public void onLoad() {
        plugin = this.getPlugin();
        getPlugin().getLogger().info("正在加载 EusAuthy");
    }

    @Override
    public void onEnable() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        new EAConfig(plugin);
        switch (EAConfig.dbType) {
            case YAML:
                break;
            case JSON:
                break;
            case SQLite:
            default:
                dataInterface = new SQLite();
                SQLite.Api api = new SQLite.Api();
                try {
                    Connection con = api.getConnection();
                    SQLite.createTable(con);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
        Bukkit.getPluginCommand("authy").setExecutor(new CmdAuthy());
        Bukkit.getPluginCommand("2fa").setExecutor(new Cmd2FA());
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlugin().getServer().getPluginManager().registerEvents(new AuthyListener(), getPlugin());
            }
        }.runTaskAsynchronously(plugin);
        Metrics metrics = new Metrics(this.getPlugin(), 10088);
        getPlugin().getLogger().info("EusAuthy 加载成功");
    }

    @Override
    public void onDisable() {
        getPlugin().getLogger().info("EusAuthy 卸载成功");
    }

    public static DataInterface getDataInterface() {
        return dataInterface;
    }

}
