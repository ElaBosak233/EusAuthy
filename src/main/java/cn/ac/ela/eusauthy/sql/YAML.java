package cn.ac.ela.eusauthy.sql;

import cn.ac.ela.eusauthy.EusAuthy;
import cn.ac.ela.eusauthy.object.PlayerData;

import java.io.File;
import java.util.UUID;

public class YAML implements DataInterface {

//    public static class Api {
//        public void create() {
//            File file = new File(EusAuthy.plugin.getDataFolder()+"/data/yaml/", "ramData.yml");
//        }
//    }

    @Override
    public boolean insertPlayer(PlayerData data) {
        return false;
    }

    @Override
    public String getSecretKey(UUID uuid) {
        return null;
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        return false;
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        return false;
    }

}
