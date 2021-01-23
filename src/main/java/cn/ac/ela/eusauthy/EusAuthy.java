package cn.ac.ela.eusauthy;

import cn.ac.ela.eusauthy.cmd.Cmd2FA;
import cn.ac.ela.eusauthy.cmd.CmdAuthy;
import cn.ac.ela.eusauthy.lisenter.AuthyListener;
import cn.ac.ela.eusauthy.sql.DataInterface;
import cn.ac.ela.eusauthy.sql.SQLite;
import cn.ac.ela.eusauthy.sql.YAML;
import io.izzel.taboolib.loader.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ElaBosak
 */
public class EusAuthy extends Plugin {

    public static JavaPlugin plugin;
    static DataInterface dataInterface;

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
        getPlugin().getLogger().info("EusAuthy 加载成功");
    }

    @Override
    public void onDisable() {
        //
    }

    public static DataInterface getDataInterface() {
        return dataInterface;
    }

}
