package cn.ac.ela.eusauthy.sql;

import cn.ac.ela.eusauthy.object.PlayerData;

import java.io.IOException;
import java.util.UUID;

/**
 * @author ElaBosak
 */
public interface DataInterface {

    boolean insertPlayer(PlayerData data); //请求输入玩家uuid和密钥进行玩家二步验证数据初始化

    String getSecretKey(UUID uuid); //请求输入玩家uuid，生成密钥

    boolean isPlayerRegistered(UUID uuid); //请求输入玩家uuid判断玩家是否已注册

    boolean deletePlayer(UUID uuid); //请求输入玩家uuid和数据库类型进行删除

}
