package cn.ac.ela.eusauthy;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class EAConfig {

    public enum DBType {
        SQLite,
        YAML,
        JSON
    }

    public static DBType dbType;

    public EAConfig(JavaPlugin plugin) {
        try {
            dbType = DBType.valueOf(plugin.getConfig().getString("Database"));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("EusAuthy 数据库类型不被支持，请使用以下支持类型：" + Arrays.toString(DBType.values()));
        }
    }

}
