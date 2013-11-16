package com.hubachov.dao.impl.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hubachov.dao.RoleDAO;
import com.hubachov.entity.Role;

@Repository
public class HibernateRoleDAO implements RoleDAO {
    private static final Logger log = Logger.getLogger(HibernateRoleDAO.class);
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void create(Role role) throws SQLException {
        if (role == null) {
            log.error("Role is null");
            throw new NullPointerException("Role is null");
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(role);
        } catch (Exception e) {
            log.error("Role is null", e);
            throw new SQLException(e);
        }
    }

    @Override
    public void update(Role role) throws SQLException {
        if (role == null) {
            log.error("Role is null");
            throw new NullPointerException("Role is null");
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(role);
        } catch (Exception e) {
            log.error("Can't update role " + role, e);
            throw new SQLException(e);
        }
    }

    @Override
    public void remove(Role role) throws SQLException {
        if (role == null) {
            log.error("Role is null");
            throw new NullPointerException("Role is null");
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.delete(role);
        } catch (Exception e) {
            log.error("Can't remove role " + role, e);
            throw new SQLException(e);
        }
    }

    @Override
    public Role findByName(String name) throws SQLException {
        List<Role> roles = null;
        try {
            Session session = sessionFactory.getCurrentSession();
            roles = session.createCriteria(Role.class)
                    .add(Restrictions.eq("name", name)).list();
        } catch (Exception e) {
            log.error("Can't find role " + name, e);
            throw new SQLException(e);
        }
        return roles.isEmpty() ? null : roles.get(0);
    }
}
