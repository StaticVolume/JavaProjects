package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;

/**В решении задания могу навязчиво использовать паттерны проектирования.
 * Понимаю что их впихивание везде - антипаттерн, но задача учебная, необходима практика.
 * Так же прекрасно понимаю, что решение данной задачи можно было сделать
 * на порядок проще, в тупую сохранив внутри класса  Util параметры подключения к БД,
 * не использовать WrapperConnection для попытки не создавать каждый раз
 * новое подлючение к ДБ, но данный функционал был сделан осознанно
 * */
public class Main {
    public static void main(String[] args) throws SQLException {
        // реализуйте алгоритм здесь
        UserService userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.cleanUsersTable();
        userService.saveUser("Данила", "Ваткин", (byte) 10);
        userService.saveUser("Иван", "Иваныч", (byte) 15);
        userService.saveUser("Молодая", "Уф Молодая", (byte) 115);
        userService.removeUserById(1);
        userService.getAllUsers();
    }
}
