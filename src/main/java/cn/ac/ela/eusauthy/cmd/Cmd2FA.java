package cn.ac.ela.eusauthy.cmd;

import cn.ac.ela.eusauthy.EusAuthy;
import cn.ac.ela.eusauthy.utils.AuthyUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ElaBosak
 */
public class Cmd2FA implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED+"请输入 2FA 子命令，或使用 /authy help 查看帮助");
            return true;
        }
        if (sender instanceof Player && sender.hasPermission("2fa.general")) {
            Player p = (Player) sender;
            if (EusAuthy.getDataInterface().isPlayerRegistered(p.getUniqueId())) {
                if (!EusAuthy.is2faed.get(p)) {
                    if (AuthyUtils.verify2fa(p.getUniqueId(), args[0])) {
                        p.sendTitle(ChatColor.GREEN+"验证通过",ChatColor.DARK_GREEN+"尽情玩耍吧",5,100,5);
                        EusAuthy.is2faed.put(p, true);
                        FileConfiguration ramDataConfiguration = AuthyUtils.ramdata();
                        String gameModeKey = p.getUniqueId().toString()+"."+"Gamemode";
//                    String is2FAingKey = p.getUniqueId().toString()+"."+"is2FAing";
                        String dataKey = p.getUniqueId().toString();
                        p.setGameMode(GameMode.valueOf((String) ramDataConfiguration.get(gameModeKey)));
                        try {
                            ramDataConfiguration.set(dataKey, null);
                            ramDataConfiguration.save(new File(EusAuthy.plugin.getDataFolder(), "ramData.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        p.sendMessage(ChatColor.RED+"验证码错误，请重试");
                        return true;
                    }
                } else {
                    p.sendMessage(ChatColor.RED+"你已经验证过啦，无需再次使用 /2FA");
                    return true;
                }
            } else {
                p.sendMessage(ChatColor.RED+"你还没有设置 EusAuthy，无需使用此命令");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED+"仅有权限玩家可以使用此命令");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String string, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("<code>");
            return subCommands;
        }
        return null;
    }
}
