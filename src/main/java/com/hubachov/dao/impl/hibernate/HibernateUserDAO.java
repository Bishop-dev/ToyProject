package com.hubachov.dao.impl.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hubachov.dao.UserDAO;
import com.hubachov.entity.User;

@Repository
public class HibernateUserDAO implements UserDAO {
    private static Logger log = Logger.getLogger(HibernateUserDAO.class);
    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void create(User user) throws SQLException {
        if (user == null) {
            log.error("User is null");
            throw new NullPointerException("User is null");
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(user);
        } catch (Exception e) {
            log.error("Can't save user " + user, e);
            throw new SQLException(e);
        }
    }

    @Override
    public void update(User user) throws SQLException {
        if (user == null) {
            log.error("User is null");
            throw new NullPointerException("User is null");
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(user);
        } catch (Exception e) {
            log.error("Can't update user " + user, e);
            throw new SQLException(e);
        }
    }

    @Override
    public void remove(User user) throws SQLException {
        if (user == null) {
            log.error("User is null");
            throw new NullPointerException("User is null");
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.delete(user);
        } catch (Exception e) {
            log.error("Can't remove user " + user, e);
            throw new SQLException(e);
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            users = session.createCriteria(User.class).list();
        } catch (Exception e) {
            log.error("Can't read all users", e);
            throw new SQLException(e);
        }
        return users;
    }

    @Override
    public User findByLogin(String login) throws SQLException {
        if (login == null) {
            log.error("Login is null");
            throw new NullPointerException("Login is null");
        }
        List<User> users = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            users = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login)).list();
        } catch (Exception e) {
            log.error("Can't find user " + login, e);
            throw new SQLException(e);
        }
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        if (email == null) {
            log.error("Email is null");
            throw new NullPointerException("Email is null");
        }
        List<User> users = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            users = session.createCriteria(User.class)
                    .add(Restrictions.eq("email", email)).list();
        } catch (Exception e) {
            log.error("Can't find user with email " + email, e);
            throw new SQLException(e);
        }
        return users.isEmpty() ? null : users.get(0);
    }
}
