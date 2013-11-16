package com.hubachov.dao;

import java.sql.SQLException;

import com.hubachov.entity.Role;

public interface RoleDAO {
    public void create(Role role) throws SQLException;

    public void update(Role role) throws SQLException;

    public void remove(Role role) throws SQLException;

    public Role findByName(String name) throws SQLException;
}
