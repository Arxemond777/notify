package Notification.database;

import java.sql.*;
import java.util.*;

public class SelectHashesChrome extends ConfigureDatabase
{
    @Override
    public Set<Map<String, String>> getQuery() {

        Connection conn = null;
        Statement stmt = null;
        Set<Map<String, String>> item = new HashSet<Map<String, String>>();

        try{
            Class.forName(ConfigureDatabase.JDBC_MYSQL_DRIVER).newInstance();

            conn = DriverManager.getConnection(this.getDBURL(), getUserName(), getPassword());

            stmt = conn.createStatement();
            String sql;
            sql = "" +
                    "SELECT id, hash, groups, type_browser, id_resource " +
                    "FROM test.notify_bundle_hash_notify " +
                    "WHERE type_browser = " + this.idResource;
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", rs.getString("id"));
                map.put("hash", rs.getString("hash"));
                map.put("groups", rs.getString("groups"));
                map.put("type_browser", rs.getString("type_browser"));
                map.put("id_resource", rs.getString("id_resource"));

                item.add(map);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch(SQLException se2) {
                se2.printStackTrace();
            }

            try {
                if (conn != null)
                    conn.close();
            } catch(SQLException se) {
                se.printStackTrace();
            }

            return item;
        }
    }
}
