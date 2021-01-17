package cn.ac.ela.eusauthy.lisenter;

import cn.ac.ela.eusauthy.EusAuthy;
import cn.ac.ela.eusauthy.object.PlayerData;
import cn.ac.ela.eusauthy.utils.Authenticator;
import cn.ac.ela.eusauthy.utils.AuthyUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AuthyListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        EusAuthy.isCreatingAuthy.put(event.getPlayer(), false);
        EusAuthy.isDeletingAuthy.put(event.getPlayer(), false);
        EusAuthy.is2faed.put(event.getPlayer(), false);
        EusAuthy.qrMeta.put(event.getPlayer(), null);
        FileConfiguration ramDataConfiguration = AuthyUtils.remData();
        String gameModeKey = event.getPlayer().getUniqueId().toString()+"."+"Gamemode";
        String is2FAingKey = event.getPlayer().getUniqueId().toString()+"."+"is2FAing";
        if (EusAuthy.getDataInterface().isPlayerRegistered(event.getPlayer().getUniqueId())) {
            String gamemode = event.getPlayer().getGameMode().toString();
            if (ramDataConfiguration.getString(gameModeKey) == null) {
                ramDataConfiguration.set(gameModeKey, gamemode);
            }
            String modeData = "SPECTATOR";
            event.getPlayer().setGameMode(GameMode.valueOf(modeData));
            event.getPlayer().sendTitle(ChatColor.GREEN+"请输入二步验证码", ChatColor.DARK_BLUE+"使用 /2fa <code> 进行验证",5,100,5);
            ramDataConfiguration.set(is2FAingKey, true);
            try {
                ramDataConfiguration.save(new File(EusAuthy.plugin.getDataFolder(), "ramData.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInitAuthy(AsyncPlayerChatEvent event) {
        if (EusAuthy.isCreatingAuthy.get(event.getPlayer())) {
            if (AuthyUtils.verify(event.getMessage(), EusAuthy.secretKeyRAM.get(event.getPlayer()))) {
                PlayerData data = new PlayerData();
                data.setUuid(event.getPlayer().getUniqueId());
                data.setSecretKey(EusAuthy.secretKeyRAM.get(event.getPlayer()));
                if (EusAuthy.getDataInterface().insertPlayer(data)) {
                    event.getPlayer().sendTitle(ChatColor.GREEN+"成功创建 Authy", ChatColor.DARK_GREEN+"下次登录请不要忘了二步验证哦", 5, 100, 5);
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED+"数据库错误，创建失败");
                }
                event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                EusAuthy.isCreatingAuthy.put(event.getPlayer(), false);
                EusAuthy.qrMeta.put(event.getPlayer(), null);
            } else if (event.getMessage().equalsIgnoreCase("cancel")) {
                event.getPlayer().sendTitle(ChatColor.RED+"已取消创建 Authy", ChatColor.DARK_RED+"再三考虑后再来试试吧", 5, 100, 5);
                EusAuthy.isCreatingAuthy.put(event.getPlayer(), false);
                event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                EusAuthy.qrMeta.put(event.getPlayer(), null);
            } else {
                event.getPlayer().sendMessage(ChatColor.RED+"错误的验证码！ 请重试或输入 Cancel 取消创建 EusAuthy");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeleteAuthy(AsyncPlayerChatEvent event) {
        if (EusAuthy.isDeletingAuthy.get(event.getPlayer())) {
            if (AuthyUtils.verify(event.getMessage(), EusAuthy.getDataInterface().getSecretKey(event.getPlayer().getUniqueId()))) {
                if (EusAuthy.getDataInterface().deletePlayer(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendTitle(ChatColor.GOLD+"成功删除 Authy", ChatColor.DARK_GREEN+"期待下次见面", 5, 100, 5);
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED+"数据库错误，删除失败");
                }
                EusAuthy.isDeletingAuthy.put(event.getPlayer(), false);
            } else if (event.getMessage().equalsIgnoreCase("cancel")) {
                event.getPlayer().sendTitle(ChatColor.RED+"已取消删除 Authy", ChatColor.DARK_RED+"再三考虑后再来试试吧", 5, 100, 5);
                EusAuthy.isDeletingAuthy.put(event.getPlayer(), false);
            } else {
                event.getPlayer().sendMessage(ChatColor.RED+"错误的验证码！ 请重试或输入 Cancel 取消删除 EusAuthy");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQrMapMoved(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName().equals(EusAuthy.qrMeta.get(p).getDisplayName()) && Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getLore()).equals(EusAuthy.qrMeta.get(p).getLore())) {
            p.sendMessage(ChatColor.RED+"请勿在创建 Authy 时移动二维码");
            event.setCancelled(true);
        }
    }

}
