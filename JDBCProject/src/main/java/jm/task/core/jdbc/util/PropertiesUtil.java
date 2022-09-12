package jm.task.core.jdbc.util;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Использую отдельный класс для соединения и подгрузки из файла application.properties необходимых параметров*/
public final class PropertiesUtil {
    private static Logger propertiesLogger = Logger.getLogger(PropertiesUtil.class.getName());
    private static final Properties properties = new Properties();

    static {// используем статик блок по причине того, что он выполняется всего 1 при загрузке
        loadProperties();
    }
    private PropertiesUtil() {  // закрываем возможность создавать экземпляры класса

    }
    public static String getByKey(String key){
        return properties.getProperty(key);
    }
    private static void  loadProperties()  {
        try ( FileReader reader = new FileReader(new File("src/main/resources/application.properties"))) {
            properties.load(reader);
            propertiesLogger.info("Load Properties from application.properties");
        }catch (IOException ex) {
            propertiesLogger.log(Level.SEVERE,ex.getMessage(),ex);
            throw new RuntimeException(ex);
        }
    }
}
