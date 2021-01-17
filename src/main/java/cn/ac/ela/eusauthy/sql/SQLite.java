package cn.ac.ela.eusauthy.sql;

import cn.ac.ela.eusauthy.EusAuthy;
import cn.ac.ela.eusauthy.object.PlayerData;
import lombok.Cleanup;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.UUID;

public class SQLite implements DataInterface {

    public static class Api {
        public Connection getConnection() throws SQLException {
            SQLiteConfig config = new SQLiteConfig();
            config.setSharedCache(true);
            config.enableRecursiveTriggers(true);
            SQLiteDataSource ds = new SQLiteDataSource(config);
            String url = System.getProperty("user.dir");
            ds.setUrl("jdbc:sqlite:"+url+"/plugins/EusAuthy/"+"EusAuthy.db");
            return ds.getConnection();
        }
    }

    public static boolean createTable(Connection con) throws SQLException {
        String sql = "create TABLE IF NOT EXISTS EusAuthy(uuid String, secretKey String); ";
        Statement stat = null;
        stat = con.createStatement();
        try {
            stat.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            con.close();
        }
    }

    public static void dropTable(Connection con) throws SQLException {
        String sql = "drop table EusAuthy; ";
        Statement stat = null;
        try {
            stat = con.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
    }

    public static boolean insert(Connection con, String  uuid, String secretKey) throws SQLException {
        String sql = "insert into EusAuthy (uuid, secretKey) values(?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            int idx = 1 ;
            pstmt.setString(idx++,uuid);
            pstmt.setString(idx++,secretKey);
            pstmt.executeUpdate();
        } finally {
            con.close();
        }
        return true;
    }

    public static boolean delete(Connection con, String uuid) throws SQLException {
        try {
            String sql = "delete from EusAuthy where uuid = ?";
            PreparedStatement pst = null;
            pst = con.prepareStatement(sql);
            int idx = 1 ;
            pst.setString(idx++, uuid);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            con.close();
        }
    }

    public static String selectSecretKey(Connection con, String uuid) throws SQLException{
        String sql = "select secretKey from EusAuthy where uuid = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        String secretKey = null;
        try {
            pst = con.prepareStatement(sql);
            int idx = 1 ;
            pst.setString(idx++, uuid);
            rs = pst.executeQuery();
            if (rs.next()) {
                secretKey = rs.getString("secretKey");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return secretKey;
    }

    @Override
    public boolean insertPlayer(PlayerData data) {
        try {
            SQLite.Api api = new SQLite.Api();
            @Cleanup Connection con = api.getConnection();
            insert(con, data.getUuid().toString(), data.getSecretKey());
        } catch (SQLException e) {
            EusAuthy.plugin.getLogger().warning("EusAuthy 数据库错误");
            return false;
        }
        return true;
    }

    @Override
    public String getSecretKey(UUID uuid) {
        String secretKey;
        try {
            SQLite.Api api = new SQLite.Api();
            @Cleanup Connection con = api.getConnection();
            secretKey = selectSecretKey(con, uuid.toString());
        } catch (SQLException e) {
            EusAuthy.plugin.getLogger().warning("EusAuthy 数据库错误");
            return null;
        }
        return secretKey;
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        boolean result = false;
        try {
            SQLite.Api api = new SQLite.Api();
            @Cleanup Connection con = api.getConnection();
            if(selectSecretKey(con, uuid.toString()) != null) {
                result = true;
            }
        } catch (SQLException e) {
            EusAuthy.plugin.getLogger().warning("EusAuthy 数据库错误");
            result = false;
        }
        return result;
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        boolean result = false;
        try {
            SQLite.Api api = new SQLite.Api();
            @Cleanup Connection con = api.getConnection();
            result = delete(con, uuid.toString());
        } catch (SQLException e) {
            EusAuthy.plugin.getLogger().warning("EusAuthy 数据库错误");
        }
        return result;
    }

}
