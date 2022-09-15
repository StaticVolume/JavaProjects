package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.loader.custom.sql.SQLCustomQuery;
import org.hibernate.query.NativeQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoHibernateImpl implements UserDao {
    private static final Logger userDaoHibernateLog = Logger.getLogger(UserDaoHibernateImpl.class.getName());
    private final SessionFactory sessionFactory;
    private static final UserDaoHibernateImpl instance = new UserDaoHibernateImpl();

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
    private UserDaoHibernateImpl() {

        sessionFactory = Util.getSessionFactory();
    }

    public static UserDaoHibernateImpl getInstance() {
        return instance != null ? instance : new UserDaoHibernateImpl();
    }



    @Override
    public void createUsersTable() {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            int sqlResult = session.createNativeQuery(SQL_CREATE_USERS_TABLE).executeUpdate();
            session.getTransaction().commit();
            userDaoHibernateLog.info("Success create Users Table");
        } catch (Exception ex) {
            userDaoHibernateLog.log(Level.SEVERE, ex.getMessage(), ex);
            session.getTransaction().rollback();
            throw new RuntimeException();
        } finally {
            session.close();
        }
    }

    @Override
    public void dropUsersTable() {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            int sqlResult = session.createNativeQuery(SQL_DROP_USERS_TABLE).executeUpdate();
            session.getTransaction().commit();
            userDaoHibernateLog.info("Success drop Users Table");
        } catch (Exception ex) {
            userDaoHibernateLog.log(Level.SEVERE, ex.getMessage(), ex);
            session.getTransaction().rollback();
            throw new RuntimeException();
        } finally {
            session.close();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.save(new User(name, lastName, age));
            session.getTransaction().commit();

            StringBuilder log = new StringBuilder();
            userDaoHibernateLog.info(
                    log.append("Updated ")
                            .append(" Add User with name : ")
                            .append(name)
                            .append(" , lastname : ")
                            .append(lastName)
                            .append(" , age : ")
                            .append(age)
                            .append(" added to Users Table")
                            .toString()
            );
        } catch (Exception ex) {
            userDaoHibernateLog.log(Level.SEVERE, ex.getMessage(), ex);
            session.getTransaction().rollback();
            throw new RuntimeException(ex);
        } finally {
            session.close();
        }
    }

    @Override
    public void removeUserById(long id) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.delete(session.get(User.class,id));
            session.getTransaction().commit();
            userDaoHibernateLog.info("Delete user with id = " + id);
        } catch (Exception ex) {
            userDaoHibernateLog.log(Level.SEVERE, ex.getMessage(), ex);
            session.getTransaction().rollback();
            throw new RuntimeException(ex);
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            users = session.createQuery("FROM User").stream().toList();
            session.getTransaction().commit();

        }
        userDaoHibernateLog.info("Get all users : " + users);
        return users;
    }

    @Override
    public void cleanUsersTable() {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.createQuery("DELETE User").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception ex) {
            userDaoHibernateLog.log(Level.SEVERE, ex.getMessage(),ex);
            session.getTransaction().rollback();
            throw  new RuntimeException(ex);
        } finally {
            session.close();
        }


    }
}
