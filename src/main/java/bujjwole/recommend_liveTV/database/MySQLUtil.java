package bujjwole.recommend_liveTV.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class MySQLUtil {

    public static String getMySQLAddress() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream = MySQLUtil.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        String instance = prop.getProperty("instance");
        String port_num = prop.getProperty("port_num");
        String dbname = prop.getProperty("dbname");
        String username = prop.getProperty("user");
        String password = prop.getProperty("password");
        return String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true&serverTimezone=UTC&createDatabaseIfNotExist=true",
                instance, port_num, dbname, username, password);
    }

}

