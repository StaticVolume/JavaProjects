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
 * Возможно создам класс-обертку с переопределенным методом close(), чтобы не закрывать соединение до самого конца
 * программы, так как операция не из дешевых.
 *Сделал, но не уверен, что сделал правильно*/

public final class Util {
    // реализуйте настройку соеденения с БД
    private static Connection dbConnection;

    private static SessionFactory sessionFactory;
    /** Константа - флаг, определяющий, будет приложение работатать напрямую с использованием JDBC
     * или с использованием HIBERNATE*/
    private static Boolean USE_HIBERNATE_KEY = true;
    /**********************************************/
    /** Общие для для Hibernate и JDBC  константы */
    private static final Logger dbConnectionLog = Logger.getLogger(Util.class.getName());
    private static final String DBUSERNAME_KEY = "hibernate.connection.username";
    private static final String DBPASSWORD_KEY = "hibernate.connection.password";
    private static final String DBURL_KEY = "hibernate.connection.url";

    /*************************************************/

    /** Константы для настройки Hibernate */

    private static final String HIBERNATE_CONNECTION_CLASS_DRIVER_KEY = "hibernate.driver_class";

    private static final String HIBERNATE_DIALECT_KEY = "hibernate.dialect";

    private static final String HIBERNATE_SHOW_SQL_KEY = "hibernate.show_sql";

    private static final String HIBERNATE_CURRENT_SESSION_CONTEXT_KEY = "hibernate.current_session_context_class";

    /**********************************************************************/
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

    public static String getHibernateConnectionClassDriverKey() {
        return  HIBERNATE_CONNECTION_CLASS_DRIVER_KEY;
    }
    public static String getHibernateDialectKey() {
        return HIBERNATE_DIALECT_KEY;
    }
    public static String getHibernateCurrentSessionContextKey() {
        return HIBERNATE_CURRENT_SESSION_CONTEXT_KEY;
    }
    public static Boolean getHibernateShowSqlKey() {

        return Boolean.valueOf(PropertiesUtil.getByKey(HIBERNATE_SHOW_SQL_KEY));

    }
}
