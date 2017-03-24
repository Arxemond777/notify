package Notification.database;

import Notification.database.DriversDB;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static Notification.Notification.ENVIRONMENT_DEV;

public abstract class ConfigureDatabase
{
    static int idResource;
    private static String
            host, databaseName, userName, password,
            pathConfigDB; //Путь до parameters_db
    private static int port; //Путь до parameters_db

    static final String JDBC_MYSQL_DRIVER = "com." + DriversDB.mysql + ".jdbc.Driver";
    private static final String DB_URL = "jdbc:" + DriversDB.mysql + "://%s:%d/%s%s";

    ConfigureDatabase() {
        this(1, "/var/www/front/app/config/parameters_db.yml");
    }

    /**
     *
     * @param idResource - по умолчанию 1 для РГ
     * @param pathConfigDB - где лежит YML-файл
     */
    ConfigureDatabase(int idResource, String pathConfigDB) {
        ConfigureDatabase.idResource = idResource;
        ConfigureDatabase.pathConfigDB = pathConfigDB;
    }

    private static void createConfigDB() {
        Map<String, Object> yamlData = new HashMap<String, Object>();
        Yaml yaml = new Yaml();

        try (InputStream ios = new FileInputStream(new File(ConfigureDatabase.pathConfigDB))) {
            Map<String, Object> result = (Map<String, Object>) yaml.load(ios);

            for (Object name: result.keySet())
                yamlData.put(name.toString(), result.get(name));

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Object> entry: yamlData.entrySet()) {
            if (!(entry.getKey()).equals("parameters")) //Нужны только parameters
                continue;

            LinkedHashMap configFromYaml = (LinkedHashMap)entry.getValue();
            ConfigureDatabase.host = (String)configFromYaml.get("database.front.host");
            ConfigureDatabase.port = Integer.parseInt((String) configFromYaml.get("database.front.port"));
            ConfigureDatabase.userName = (String)configFromYaml.get("database.front.user");
            ConfigureDatabase.databaseName = (String)configFromYaml.get("database.front.name");
            try { //Иногда бывает password в integer, так что хз, что прилетит
                ConfigureDatabase.password = (String) configFromYaml.get("database.front.password");
            } catch (ClassCastException e) {
                ConfigureDatabase.password = Integer.toString((Integer) configFromYaml.get("database.front.password"));
            }
        }
    }

    private static boolean init = false;

    private static synchronized void init() {
        if (!init) {
            ConfigureDatabase.createConfigDB();
            init = true;
        }
    }

    protected final String getDBURL() { //Возвращаем необходимую отформатированную строку для DB_URL (коннекта через JDBC)
        ConfigureDatabase.createConfigDB(); //Делаем первичную инициализацию

        return String.format(
                ConfigureDatabase.DB_URL,
                    ConfigureDatabase.host,
                    ConfigureDatabase.port,
                    ConfigureDatabase.databaseName,
                    (ENVIRONMENT_DEV ? "?useSSL=false" : "")
        );
    }

    protected final String getUserName() {
        return ConfigureDatabase.userName;
    }

    protected final String getPassword() {
        return ConfigureDatabase.password ;
    }

    abstract Set<Map<String, String>> getQuery();
}
