package com.hubachov.service;

import java.sql.SQLException;
import java.util.List;

import com.hubachov.entity.User;

public interface UserService {
    public void create(User user) throws SQLException;

    public void update(User user) throws SQLException;

    public void remove(User user) throws SQLException;

    public List<User> findAll() throws SQLException;

    public User findByLogin(String login) throws SQLException;

    public User findByEmail(String email) throws SQLException;
}
