package cn.ac.ela.eusauthy.cmd;

import cn.ac.ela.eusauthy.EusAuthy;
import cn.ac.ela.eusauthy.utils.Authenticator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CmdAuthy implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED+"请输入 EusAuthy 子命令，或使用 /authy help 查看帮助");
            return true;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (sender.hasPermission("authy.op") || sender instanceof ConsoleCommandSender) {
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    assert target != null;
                    if (EusAuthy.getDataInterface().deletePlayer(target.getUniqueId())) {
                        sender.sendMessage(ChatColor.GREEN+"玩家数据删除成功");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"请提供玩家名称");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED+"你没有执行此命令的权限");
                return true;
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (sender instanceof Player && sender.hasPermission("authy.general")) {
                Player p = (Player) sender;
                if (!EusAuthy.getDataInterface().isPlayerRegistered(p.getUniqueId())) {
                    p.sendTitle(ChatColor.GREEN+"开始创建 EusAuthy", ChatColor.DARK_GREEN+"请按照指示完成所有步骤", 5, 100, 5);
                    String secretKey = Authenticator.generateSecretKey();
                    String url = Authenticator.getGoogleAuthenticatorQRCode(secretKey, EusAuthy.plugin.getServer().getName(), p.getName());
                    try {
                        Authenticator.createQRCode(url, EusAuthy.plugin.getDataFolder()+"/qrcode/", p.getUniqueId().toString()+".png");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }
                    if (p.getInventory().getItemInOffHand().getType() == Material.AIR) {
                        p.sendMessage(ChatColor.GREEN+
                                "------------------ EusAuthy START ------------------\n"+
                                ChatColor.GRAY+
                                "1. 使用二步验证 APP 扫描副手上的二维码或输入以下 Secret Key \n"+
                                "  "+secretKey+"\n"+
                                "2. 在聊天框中输入即时验证码，完成创建\n"+
                                ChatColor.GREEN+
                                "------------------- EusAuthy END -------------------\n"
                        );
                        EusAuthy.secretKeyRAM.put(p, secretKey);
                        ItemStack map = new ItemStack(Material.FILLED_MAP);
                        MapView view = EusAuthy.plugin.getServer().createMap(EusAuthy.plugin.getServer().getWorlds().get(0));
                        for(MapRenderer renderer : view.getRenderers()) {
                            view.removeRenderer(renderer);
                        }
                        view.addRenderer(new MapRenderer() {
                            @Override
                            public void render(MapView map, MapCanvas canvas, Player player) {
                                BufferedImage img;
                                try{
                                    img = ImageIO.read(new File(EusAuthy.plugin.getDataFolder()+"/qrcode/"+player.getUniqueId().toString()+".png"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                }
                                map.setScale(MapView.Scale.NORMAL);
                                canvas.drawImage(0,0,img);
                            }
                        });
                        MapMeta mapMeta = ((MapMeta)map.getItemMeta());
                        assert mapMeta != null;
                        mapMeta.setDisplayName("§6§lEusAuthy 二维码");
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(p.getUniqueId().toString());
                        lore.add(secretKey);
                        mapMeta.setLore(lore);
                        mapMeta.setMapView(view);
                        map.setItemMeta(mapMeta);
                        EusAuthy.qrMeta.put(p, map.getItemMeta());
                        p.getInventory().setItemInOffHand(map);
                        EusAuthy.isCreatingAuthy.put(p, true);
                    } else {
                        p.sendMessage(ChatColor.RED+"请清空你副手上的物品以便设置 EusAuthy");
                        return true;
                    }
                    return true;
                } else {
                    p.sendMessage(ChatColor.RED+"你已经设置 EusAuthy 了，请删除后方可重置 EusAuthy");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED+"只有玩家可以使用此命令");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (sender instanceof Player && sender.hasPermission("authy.general")) {
                Player p = (Player) sender;
                if (EusAuthy.getDataInterface().isPlayerRegistered(p.getUniqueId())) {
                    p.sendTitle(ChatColor.GOLD+"开始删除 EusAuthy", ChatColor.GREEN+"请按照指示完成所有步骤", 5, 100, 5);
                    p.sendMessage(ChatColor.GOLD+
                            "------------------ EusAuthy START ------------------\n"+
                            ChatColor.GRAY+
                            "1. 在聊天框中输入二步验证 APP 的验证码，完成删除\n"+
                            ChatColor.GOLD+
                            "------------------- EusAuthy END -------------------\n"
                    );
                    EusAuthy.isDeletingAuthy.put(p, true);
                    return true;
                } else {
                    p.sendMessage(ChatColor.RED+"你尚未创建 EusAuthy");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED+"只有玩家可以使用此命令");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GREEN+
                    "------------------ EusAuthy HELP ------------------\n"+
                    ChatColor.GRAY+
                    "/authy create —— 创建 EusAuthy 二步验证\n"+
                    "/authy delete —— 删除 EusAuthy 二步验证\n"+
                    "/authy help —— 获取 EusAuthy 帮助\n"+
                    ChatColor.BLUE+
                    "/authy remove <玩家名> —— 移除指定玩家 EusAuthy 二步验证\n"+
                    ChatColor.GRAY+
                    "/2fa <code> —— 登录时使用，提供验证码以交互服务器\n"+
                    ChatColor.GREEN+
                    "------------------ EusAuthy HELP -------------------\n"
            );
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("create");
            subCommands.add("delete");
            subCommands.add("help");
            if (sender.hasPermission("authy.op") || sender instanceof ConsoleCommandSender) {
                subCommands.add("remove");
            }
            return subCommands;
        }
        if (args.length == 2) {
            List<String> subsubCommands = new ArrayList<>();
            if ((sender.hasPermission("authy.op") || sender instanceof ConsoleCommandSender) && args[0].equalsIgnoreCase("remove")) {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    subsubCommands.add(p.getName());
                }
            }
            return subsubCommands;
        }
        return null;
    }
}
