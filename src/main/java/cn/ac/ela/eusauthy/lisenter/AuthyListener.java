package cn.ac.ela.eusauthy.lisenter;

import cn.ac.ela.eusauthy.EusAuthy;
import cn.ac.ela.eusauthy.object.PlayerData;
import cn.ac.ela.eusauthy.utils.AuthyUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

/**
 * @author ElaBosak
 */
public class AuthyListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Player Stats init
        EusAuthy.isCreatingAuthy.put(event.getPlayer(), false);
        EusAuthy.isDeletingAuthy.put(event.getPlayer(), false);
        EusAuthy.is2faed.put(event.getPlayer(), false);
        // Player Data RAM init
        EusAuthy.qrMeta.put(event.getPlayer(), null);
        EusAuthy.secretKeyRAM.put(event.getPlayer(), null);
        // Create Player Join Data RAM File
        FileConfiguration ramDataConfiguration = AuthyUtils.ramdata();
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
        if (event.getPlayer().getInventory().getItemInOffHand().hasItemMeta()) {
            String qrMetaKey = event.getPlayer().getUniqueId().toString()+"."+"qrMeta";
            ItemMeta itemMeta = ramDataConfiguration.get(qrMetaKey) instanceof ItemMeta ? (ItemMeta) ramDataConfiguration.get(qrMetaKey) : null;
            if (event.getPlayer().getInventory().getItemInOffHand().getItemMeta().equals(itemMeta)) {
                event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                ramDataConfiguration.set(qrMetaKey, null);
                try {
                    ramDataConfiguration.save(new File(EusAuthy.plugin.getDataFolder(), "ramData.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getPlayer().sendMessage(ChatColor.GOLD+"你还没创建完 EusAuthy 呢， 二维码已回收");
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        FileConfiguration ramDataConfiguration = AuthyUtils.ramdata();
        String is2FAingKey = event.getPlayer().getUniqueId().toString()+"."+"is2FAing";
        if (ramDataConfiguration.getBoolean(is2FAingKey)) {
            event.getPlayer().sendMessage(ChatColor.RED+"你还没有验证呢， 请输入二步验证码");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInitAuthy(AsyncPlayerChatEvent event) {
        if (EusAuthy.isCreatingAuthy.getOrDefault(event.getPlayer(), false)) {
            if (AuthyUtils.verify(event.getMessage(), EusAuthy.secretKeyRAM.get(event.getPlayer()))) {
                PlayerData data = new PlayerData();
                data.setUuid(event.getPlayer().getUniqueId());
                data.setSecretKey(EusAuthy.secretKeyRAM.get(event.getPlayer()));
                if (EusAuthy.getDataInterface().insertPlayer(data)) {
                    event.getPlayer().sendTitle(ChatColor.GREEN+"成功创建 Authy", ChatColor.DARK_GREEN+"下次登录请不要忘了二步验证哦", 5, 100, 5);
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED+"数据库错误， 创建失败");
                }
                event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                EusAuthy.isCreatingAuthy.put(event.getPlayer(), false);
                EusAuthy.qrMeta.put(event.getPlayer(), null);
                EusAuthy.is2faed.put(event.getPlayer(), true);
            } else if (event.getMessage().equalsIgnoreCase("cancel")) {
                event.getPlayer().sendTitle(ChatColor.RED+"已取消创建 Authy", ChatColor.DARK_RED+"再三考虑后再来试试吧", 5, 100, 5);
                EusAuthy.isCreatingAuthy.put(event.getPlayer(), false);
                event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                EusAuthy.qrMeta.put(event.getPlayer(), null);
            } else {
                event.getPlayer().sendMessage(ChatColor.RED+"错误的验证码！ 请重试或输入 Cancel 取消创建 EusAuthy");
            }

            // clear qrMeta in RAM
            FileConfiguration ramDataConfiguration = AuthyUtils.ramdata();
            String qrMetaKey = event.getPlayer().getUniqueId().toString()+"."+"qrMeta";
            ramDataConfiguration.set(qrMetaKey, null);
            try {
                ramDataConfiguration.save(new File(EusAuthy.plugin.getDataFolder(), "ramData.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeleteAuthy(AsyncPlayerChatEvent event) {
        if (EusAuthy.isDeletingAuthy.getOrDefault(event.getPlayer(), false)) {
            if (AuthyUtils.verify(event.getMessage(), EusAuthy.getDataInterface().getSecretKey(event.getPlayer().getUniqueId()))) {
                if (EusAuthy.getDataInterface().deletePlayer(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendTitle(ChatColor.GOLD+"成功删除 Authy", ChatColor.DARK_GREEN+"期待下次见面", 5, 100, 5);
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED+"数据库错误， 删除失败");
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
        if (event.getCurrentItem() != null) {
            if (event.getCurrentItem().hasItemMeta()) {
                if (event.getCurrentItem().getItemMeta().equals(EusAuthy.qrMeta.get(p))) {
                    p.sendMessage(ChatColor.RED+"请勿在创建 Authy 时移动二维码");
                    event.setCancelled(true);
                }
            }
        }
    }

}
