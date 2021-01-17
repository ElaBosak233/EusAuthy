package cn.ac.ela.eusauthy.utils;

import cn.ac.ela.eusauthy.EusAuthy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class AuthyUtils {

    public static boolean verify (String totp, String secretKey) {
        return Authenticator.getTOTPCode(secretKey).equalsIgnoreCase(totp);
    }

    public static boolean verify2fa (UUID uuid, String totp) {
        return Authenticator.getTOTPCode(EusAuthy.getDataInterface().getSecretKey(uuid)).equalsIgnoreCase(totp);
    }

    public static FileConfiguration remData() {
        File ramDataFile = new File(EusAuthy.plugin.getDataFolder(), "ramData.yml");
        return YamlConfiguration.loadConfiguration(ramDataFile);
    }

}
