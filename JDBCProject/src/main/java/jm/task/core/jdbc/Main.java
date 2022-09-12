package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.PropertiesUtil;
import jm.task.core.jdbc.util.Util;
import jm.task.core.jdbc.util.WrapperConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**В решении задания могу навязчиво использовать паттерны проектирования.
 * Понимаю что их впихивание везде - антипаттерн, но задача учебная, необходима практика.*/
public class Main {
    public static void main(String[] args) throws SQLException {
        // реализуйте алгоритм здесь
        UserServiceImpl userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.cleanUsersTable();
        userService.saveUser("Данила", "Ваткин", (byte) 10);
        userService.saveUser("Иван", "Иваныч", (byte) 15);
        userService.saveUser("Молодая", "Уф Молодая", (byte) 115);
        userService.removeUserById(1);
        userService.getAllUsers();
        WrapperConnection.finallyClose();
    }
}
