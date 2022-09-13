package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Пробую реализовать dbConnectionManager
 * с использованием данных из конфиругационного файла
 * Возможно создам класс-обертку с переопределенным методом close(), чтобы не закрывать соединение до самого конца
 * программы, так как операция не из дешевых.
 *Сделал, но не уверен, что сделал правильно*/
public final class Util {
    // реализуйте настройку соеденения с БД
    private static Connection dbConnection;
    private static final Logger dbConnectionLog = Logger.getLogger(Util.class.getName());
    private static final String DBUSERNAME = "db.username";
    private static final String DBPASSWORD = "db.password";
    private static final String DBURL = "db.url";

    private Util () { // закрываем возможность создавать экземпляры класса

    }

    private static void CreateConnection() {
        try {
            dbConnection = DriverManager.getConnection(
                    PropertiesUtil.getByKey(DBURL),
                    PropertiesUtil.getByKey(DBUSERNAME),
                    PropertiesUtil.getByKey(DBPASSWORD)
            );

            StringBuilder infoStr = new StringBuilder();
            infoStr.append("Create Database Connection with LOGIN : ")
                    .append(PropertiesUtil.getByKey(DBUSERNAME))
                    .append(" ; PASSWORD : ")
                    .append(PropertiesUtil.getByKey(DBPASSWORD))
                    .append(" ; URL : ")
                    .append( PropertiesUtil.getByKey(DBURL))
                    .append(".");
            dbConnectionLog.info(infoStr.toString());
        } catch (SQLException ex) {
            dbConnectionLog.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
    public static Connection getConnection() {
        CreateConnection();
        return  dbConnection;
    }
    public static Logger getDbConnectionLog() {
        return dbConnectionLog;
    }

    public static String getDbUsername() {
        return DBUSERNAME;
    }

    public static String getDbPassword() {
        return DBPASSWORD;
    }

    public static String getDbUrl() {
        return DBURL;
    }
}
