package com.hubachov.service;

import java.sql.SQLException;

import com.hubachov.entity.Role;

public interface RoleService {
    public void create(Role role) throws SQLException;

    public void update(Role role) throws SQLException;

    public void remove(Role role) throws SQLException;

    public Role findByName(String name) throws SQLException;
}
