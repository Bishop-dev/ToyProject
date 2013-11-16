package com.hubachov.service.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hubachov.dao.RoleDAO;
import com.hubachov.entity.Role;
import com.hubachov.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDAO roleDAO;

    @Override
    @Transactional
    public void create(Role role) throws SQLException {
        roleDAO.create(role);
    }

    @Override
    @Transactional
    public void update(Role role) throws SQLException {
        roleDAO.update(role);
    }

    @Override
    @Transactional
    public void remove(Role role) throws SQLException {
        roleDAO.remove(role);
    }

    @Override
    @Transactional
    public Role findByName(String name) throws SQLException {
        return roleDAO.findByName(name);
    }

}
