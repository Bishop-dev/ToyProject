package com.hubachov.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hubachov.dao.UserDAO;
import com.hubachov.entity.User;
import com.hubachov.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public void create(User user) throws SQLException {
        userDAO.create(user);
    }

    @Override
    @Transactional
    public void update(User user) throws SQLException {
        userDAO.update(user);
    }

    @Override
    @Transactional
    public void remove(User user) throws SQLException {
        userDAO.remove(user);
    }

    @Override
    @Transactional
    public List<User> findAll() throws SQLException {
        return userDAO.findAll();
    }

    @Override
    @Transactional
    public User findByLogin(String login) throws SQLException {
        return userDAO.findByLogin(login);
    }

    @Override
    @Transactional
    public User findByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }

}
