package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Пробую реализовать dbConnectionManager
 * с использованием данных из конфиругационного файла
 */

public final class Util {
    // реализуйте настройку соеденения с БД
    private static Connection dbConnection;
    private static SessionFactory sessionFactory;

    /**********************************************/
    private static final Logger dbConnectionLog = Logger.getLogger(Util.class.getName());
    private static final String DBUSERNAME_KEY = "hibernate.connection.username";
    private static final String DBPASSWORD_KEY = "hibernate.connection.password";
    private static final String DBURL_KEY = "hibernate.connection.url";

    /*************************************************/
    private Util () { // закрываем возможность создавать экземпляры класса

    }
    private static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration()
                    .setProperties(PropertiesUtil.getProperties())
                    .addAnnotatedClass(User.class);
            sessionFactory = configuration.buildSessionFactory();
            dbConnectionLog.info("SessionFactory Created");
        } catch (Exception ex) {
            dbConnectionLog.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
    private static void CreateConnection() {
        try {
            dbConnection = DriverManager.getConnection(
                    PropertiesUtil.getByKey(DBURL_KEY),
                    PropertiesUtil.getByKey(DBUSERNAME_KEY),
                    PropertiesUtil.getByKey(DBPASSWORD_KEY)
            );

            StringBuilder infoStr = new StringBuilder();
            infoStr.append("Create Database Connection with LOGIN : ")
                    .append(PropertiesUtil.getByKey(DBUSERNAME_KEY))
                    .append(" ; PASSWORD : ")
                    .append(PropertiesUtil.getByKey(DBPASSWORD_KEY))
                    .append(" ; URL : ")
                    .append( PropertiesUtil.getByKey(DBURL_KEY))
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
    public static SessionFactory getSessionFactory() {
        createSessionFactory();
        return sessionFactory;
    }
    public static Logger getDbConnectionLog() {
        return dbConnectionLog;
    }

    public static String getDbUsername() {
        return DBUSERNAME_KEY;
    }

    public static String getDbPassword() {
        return DBPASSWORD_KEY;
    }

    public static String getDbUrl() {
        return DBURL_KEY;
    }
}
