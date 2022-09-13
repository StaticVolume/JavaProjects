package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Считается, что DAO - Data Access Object должен быть реализован
 *  с использованием паттерна проектирования Singleton
 *  Пытаюсь его так и реализовать
 *  Проблема - много методов с однотипным кодом, возможное решение :
 *  Вывести дублируюмый код в отдельный метод. Но присутствует разница в обьектах Statement и PrepareStatement
 * */
public class UserDaoJDBCImpl implements UserDao {

    private static final Logger userDaoJDBCLogger = Logger.getLogger(UserDaoJDBCImpl.class.getName());
    private static final UserDaoJDBCImpl instance = new UserDaoJDBCImpl(); // первое требование Singleton

    private final Connection jdbcConnect;

    /**Все запросы к базе данных будем создавать в виде private констант */
    private static final String SQL_CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS Users (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            firstname VARCHAR(128) NOT NULL,
            lastname VARCHAR(128) NOT NULL,
            age TINYINT NOT NULL
            );
            """;
    private static final String SQL_DROP_USERS_TABLE = """
            DROP TABLE IF EXISTS Users
            """;

    private static final String SQL_INSERT_USER = """
            INSERT INTO Users (firstname, lastname, age)
            VALUES (?, ?, ?);
            """;
    private static final String SQL_DELETE_BY_ID = """
            DELETE IGNORE FROM Users
            WHERE
            id = ?;
            """;
    private static final String SQL_GET_ALL_USERS = """
            SELECT *
            FROM Users;
            """;
    private static final String SQL_CLEAR_TABLE_USERS = """
            DELETE
            FROM Users;
            """;
    private UserDaoJDBCImpl() { // private constructor - 2 требование к Singleton
        jdbcConnect = Util.getConnection();

    }

    public static UserDaoJDBCImpl getInstance() { // static method с данным функционалом - 3 требование Singleton

        return instance != null ? instance : new UserDaoJDBCImpl();
    }

    public void createUsersTable() {
        try (Statement jdbcStatement = jdbcConnect.createStatement()) {
            jdbcConnect.setAutoCommit(false);

            jdbcStatement.execute(SQL_CREATE_USERS_TABLE);
            jdbcConnect.commit();

            userDaoJDBCLogger.info("Success Create Table Users");
        } catch (SQLException ex) {
            userDaoJDBCLogger.log(Level.SEVERE, ex.getMessage(), ex);
            if (jdbcConnect != null) {
                try {
                    jdbcConnect.rollback();
                } catch (SQLException rollback) {
                    userDaoJDBCLogger.log(Level.SEVERE,rollback.getMessage(),rollback);
                    throw new RuntimeException(rollback);
                }
            }
            throw new RuntimeException(ex);
        }
    }

    public void dropUsersTable() {
        try (Statement jdbcStatement = jdbcConnect.createStatement()) {
            jdbcConnect.setAutoCommit(false);

            jdbcStatement.execute(SQL_DROP_USERS_TABLE);
            jdbcConnect.commit();

            userDaoJDBCLogger.info("Success Delete Table Users");
        } catch (SQLException ex) {
            userDaoJDBCLogger.log(Level.SEVERE, ex.getMessage(), ex);
            if (jdbcConnect != null) {
                try {
                    jdbcConnect.rollback();
                } catch (SQLException rollback) {
                    userDaoJDBCLogger.log(Level.SEVERE,rollback.getMessage(),rollback);
                    throw new RuntimeException(rollback);
                }
            }
            throw new RuntimeException(ex);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try(PreparedStatement jdbcPrepareStatement = jdbcConnect.prepareStatement(SQL_INSERT_USER)) {
            jdbcPrepareStatement.setString(1, name);
            jdbcPrepareStatement.setString(2, lastName);
            jdbcPrepareStatement.setByte(3, age);

            jdbcConnect.setAutoCommit(false);
            int resultSet = jdbcPrepareStatement.executeUpdate();
            jdbcConnect.commit();

            StringBuilder log = new StringBuilder();
            userDaoJDBCLogger.info(
                    log.append("Updated ")
                            .append(resultSet)
                            .append(" strings, User with name : ")
                            .append(name)
                            .append(" , lastname : ")
                            .append(lastName)
                            .append(" , age : ")
                            .append(age)
                            .append(" added to Users Table")
                            .toString()
            );
        } catch (SQLException ex) {
            userDaoJDBCLogger.log(Level.SEVERE, ex.getMessage(), ex);
            if (jdbcConnect != null) {
                try {
                    jdbcConnect.rollback();
                } catch (SQLException rollback) {
                    userDaoJDBCLogger.log(Level.SEVERE, rollback.getMessage(), rollback);
                    throw new RuntimeException(rollback);
                }
            }
            throw new RuntimeException();
        }
    }

    public void removeUserById(long id) {
        try(PreparedStatement jdbcPrepareStatement = jdbcConnect.prepareStatement(SQL_DELETE_BY_ID)) {
            jdbcPrepareStatement.setLong(1, id);
            jdbcConnect.setAutoCommit(false);

            jdbcPrepareStatement.executeUpdate();
            jdbcConnect.commit();

            userDaoJDBCLogger.info("User with id = " + id + " was deleted , if id will exist");
        } catch (SQLException ex) {
            userDaoJDBCLogger.log(Level.SEVERE, ex.getMessage(), ex);
            if (jdbcConnect != null) {
                try {
                    jdbcConnect.rollback();
                } catch (SQLException rollback) {
                    userDaoJDBCLogger.log(Level.SEVERE,rollback.getMessage(),rollback);
                    throw new RuntimeException(rollback);
                }
            }
            throw new RuntimeException(ex);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement jdbcStatement = jdbcConnect.createStatement()) {
            jdbcConnect.setAutoCommit(false);
            ResultSet resultSet = jdbcStatement.executeQuery(SQL_GET_ALL_USERS);
            jdbcConnect.commit();

            while (resultSet.next()) {
                users.add(
                        new User (
                                resultSet.getString("firstname"),
                                resultSet.getString("lastname"),
                                resultSet.getByte("age"))
                );
            }
            userDaoJDBCLogger.info("Create List<User> : " + users);
        } catch (SQLException ex) {
            userDaoJDBCLogger.log(Level.SEVERE, ex.getMessage(),ex);
            if (jdbcConnect != null) {
                try{
                    jdbcConnect.rollback();
                } catch (SQLException rollback) {
                    userDaoJDBCLogger.log(Level.SEVERE, rollback.getMessage(),rollback);
                    throw new RuntimeException(rollback);
                }
            }
            throw new RuntimeException(ex);
        }
        return users;
    }

    public void cleanUsersTable() {
        try (Statement jdbcStatement = jdbcConnect.createStatement()) {
            jdbcConnect.setAutoCommit(false);
            int resultSet =jdbcStatement.executeUpdate(SQL_CLEAR_TABLE_USERS);
            jdbcConnect.commit();

            userDaoJDBCLogger.info("Deleted " + resultSet + " strings from Users. Table Users clear.");
        } catch (SQLException ex) {
            userDaoJDBCLogger.log(Level.SEVERE, ex.getMessage(), ex);
            if (jdbcConnect != null) {
                try{
                    jdbcConnect.rollback();
                } catch (SQLException rollback) {
                    userDaoJDBCLogger.log(Level.SEVERE, rollback.getMessage(), rollback);
                    throw new RuntimeException(rollback);
                }
            }
            throw new RuntimeException(ex);
        }
    }
}
